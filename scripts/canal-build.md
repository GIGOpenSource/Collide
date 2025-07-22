# Canal源码修改维护指南

## 修改目的
解决MySQL->Canal->ES数据同步中的时间格式转换问题，将默认的ISO 8601格式改为简单的`yyyy-MM-dd HH:mm:ss`格式。

## 问题背景
- Canal默认格式：`2024-05-13T16:04:16+08:00`
- Spring期望格式：`2024-05-13 16:04:16`
- 转换失败导致：`ConversionException: Unable to convert value to java.util.Date`

## 修改位置
文件：`client-adapter/es8x/src/main/java/com/alibaba/otter/canal/client/adapter/es8x/support/ESSyncUtil.java`

### 需要修改的8处代码位置

```java
// 原始代码
DateTime dateTime = new DateTime(((java.sql.Timestamp) val).getTime());
if (dateTime.getMillisOfSecond() != 0) {
  res = dateTime.toString("yyyy-MM-dd'T'HH:mm:ss.SSS" + Util.timeZone);
} else {
  res = dateTime.toString("yyyy-MM-dd'T'HH:mm:ss" + Util.timeZone);
}

// 推荐的修改代码（保留时区）
DateTime dateTime = new DateTime(((java.sql.Timestamp) val).getTime());
if (dateTime.getMillisOfSecond() != 0) {
  res = dateTime.toString("yyyy-MM-dd'T'HH:mm:ss.SSS'+08:00'");
} else {
  res = dateTime.toString("yyyy-MM-dd'T'HH:mm:ss'+08:00'");
}

// 或者使用更通用的时区（推荐）
DateTime dateTime = new DateTime(((java.sql.Timestamp) val).getTime());
if (dateTime.getMillisOfSecond() != 0) {
  res = dateTime.toString("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
} else {
  res = dateTime.toString("yyyy-MM-dd'T'HH:mm:ssXXX");
}
```

## 构建步骤

### 1. 环境准备
```bash
# 确保Java 8+和Maven 3.6+已安装
java -version
mvn -version
```

### 2. 下载源码
```bash
wget https://github.com/alibaba/canal/archive/canal-1.1.7.tar.gz
tar -xzf canal-1.1.7.tar.gz
cd canal-canal-1.1.7
```

### 3. 修改源码
在`client-adapter/es8x/src/main/java/com/alibaba/otter/canal/client/adapter/es8x/support/ESSyncUtil.java`文件中找到所有日期转换的地方进行修改。

### 4. 编译打包
```bash
cd client-adapter/es8x
mvn clean package -Dmaven.test.skip=true
```

### 5. 获取结果包
编译完成后，在`target/`目录下找到：
`client-adapter.es8x-1.1.7-jar-with-dependencies.jar`

## 部署步骤

### 1. 备份原包
```bash
cd /root/package/canal-adapter/plugin
mv client-adapter.es8x-1.1.7-jar-with-dependencies.jar client-adapter.es8x-1.1.7-jar-with-dependencies.jar_backup
```

### 2. 上传新包
```bash
# 上传编译后的新包到Canal的plugin目录
scp client-adapter.es8x-1.1.7-jar-with-dependencies.jar root@canal-server:/root/package/canal-adapter/plugin/
```

### 3. 重启服务
```bash
cd /root/package/canal-adapter/bin
./restart.sh
# 注意：必须使用restart.sh，不要用stop.sh然后start.sh
```

## 验证步骤

### 1. 检查ES中的数据格式
```bash
curl -X GET "localhost:9200/your_index/_search?size=1&pretty" | grep -A5 -B5 "sale_time"
```

### 2. 检查应用日志
确认不再有`ConversionException`错误。

### 3. 测试功能
验证时间相关的查询和过滤功能正常工作。

## 注意事项

1. **版本升级**：Canal版本升级时需要重新应用这个修改
2. **时区处理**：修改后时间信息不再包含时区，确保这符合业务需求
3. **备份策略**：建议保留原始包的备份
4. **文档更新**：团队成员需要了解这个修改

## 回滚方案

如果出现问题，可以快速回滚：
```bash
cd /root/package/canal-adapter/plugin
mv client-adapter.es8x-1.1.7-jar-with-dependencies.jar client-adapter.es8x-1.1.7-jar-with-dependencies.jar_modified
mv client-adapter.es8x-1.1.7-jar-with-dependencies.jar_backup client-adapter.es8x-1.1.7-jar-with-dependencies.jar
cd ../bin
./restart.sh
```

## 联系信息
如有问题，请联系：[维护人员姓名和联系方式] 