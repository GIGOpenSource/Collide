package org.apache.shardingsphere.transaction.base.seata.at;

import com.google.common.base.Preconditions;
import io.seata.config.FileConfiguration;
import io.seata.core.context.RootContext;
import io.seata.core.exception.TransactionException;
import io.seata.core.rpc.netty.RmNettyRemotingClient;
import io.seata.core.rpc.netty.TmNettyRemotingClient;
import io.seata.rm.RMClient;
import io.seata.rm.datasource.DataSourceProxy;
import io.seata.tm.TMClient;
import io.seata.tm.api.GlobalTransaction;
import io.seata.tm.api.GlobalTransactionContext;
import lombok.SneakyThrows;
import org.apache.shardingsphere.infra.database.type.DatabaseType;
import org.apache.shardingsphere.transaction.core.ResourceDataSource;
import org.apache.shardingsphere.transaction.core.TransactionType;
import org.apache.shardingsphere.transaction.spi.ShardingSphereTransactionManager;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 为了解决在Seata+ShardingJDBC场景下，事务失效的问题
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
public class SeataATShardingSphereTransactionManager implements ShardingSphereTransactionManager {
    private final Map<String, DataSource> dataSourceMap = new HashMap<>();

    private final String applicationId;

    private final String transactionServiceGroup;

    private final boolean enableSeataAT;

    private final int globalTXTimeout;

    public SeataATShardingSphereTransactionManager() {
        FileConfiguration config = new FileConfiguration("seata.conf");
        enableSeataAT = config.getBoolean("sharding.transaction.seata.at.enable", true);
        applicationId = config.getConfig("client.application.id");
        transactionServiceGroup = config.getConfig("client.transaction.service.group", "default");
        globalTXTimeout = config.getInt("sharding.transaction.seata.tx.timeout", 60);
    }

    @Override
    public void init(final DatabaseType databaseType, final Collection<ResourceDataSource> resourceDataSources, final String providerType) {
        if (enableSeataAT) {
            initSeataRPCClient();
            resourceDataSources.forEach(each -> dataSourceMap.put(each.getOriginalName(), new DataSourceProxy(each.getDataSource())));
        }
    }

    @SuppressWarnings("AlibabaLowerCamelCaseVariableNaming")
    private void initSeataRPCClient() {
        Preconditions.checkNotNull(applicationId, "please config application id within seata.conf file.");
        TMClient.init(applicationId, transactionServiceGroup);
        RMClient.init(applicationId, transactionServiceGroup);
    }

    @Override
    public TransactionType getTransactionType() {
        return TransactionType.BASE;
    }

    @SuppressWarnings("AlibabaRemoveCommentedCode")
    @Override
    public boolean isInTransaction() {
        Preconditions.checkState(enableSeataAT, "sharding seata-at transaction has been disabled.");
        //return null != RootContext.getXID();
        return null != SeataTransactionHolder.get();
    }

    @Override
    public Connection getConnection(final String databaseName, final String dataSourceName) throws SQLException {
        Preconditions.checkState(enableSeataAT, "sharding seata-at transaction has been disabled.");
        return dataSourceMap.get(databaseName + "." + dataSourceName).getConnection();
    }

    @Override
    public void begin() {
        begin(globalTXTimeout);
    }

    @Override
    @SneakyThrows(TransactionException.class)
    public void begin(final int timeout) {
        if (timeout < 0) {
            throw new TransactionException("timeout should more than 0s");
        }
        Preconditions.checkState(enableSeataAT, "sharding seata-at transaction has been disabled.");
        GlobalTransaction globalTransaction = GlobalTransactionContext.getCurrentOrCreate();
        globalTransaction.begin(timeout * 1000);
        SeataTransactionHolder.set(globalTransaction);
    }

    @Override
    @SneakyThrows(TransactionException.class)
    public void commit(final boolean rollbackOnly) {
        Preconditions.checkState(enableSeataAT, "sharding seata-at transaction has been disabled.");
        try {
            SeataTransactionHolder.get().commit();
        } finally {
            SeataTransactionHolder.clear();
            RootContext.unbind();
        }
    }

    @Override
    @SneakyThrows(TransactionException.class)
    public void rollback() {
        Preconditions.checkState(enableSeataAT, "sharding seata-at transaction has been disabled.");
        try {
            SeataTransactionHolder.get().rollback();
        } finally {
            SeataTransactionHolder.clear();
            RootContext.unbind();
        }
    }

    @Override
    public void close() {
        dataSourceMap.clear();
        SeataTransactionHolder.clear();
        RmNettyRemotingClient.getInstance().destroy();
        TmNettyRemotingClient.getInstance().destroy();
    }
}
