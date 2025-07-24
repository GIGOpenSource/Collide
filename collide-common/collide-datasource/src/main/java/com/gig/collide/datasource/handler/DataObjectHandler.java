package com.gig.collide.datasource.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;

import java.time.LocalDateTime;
import java.util.Date;


public class DataObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        // 支持旧字段名（向后兼容）
        this.setFieldValByNameIfNull("gmtCreate", new Date(), metaObject);
        this.setFieldValByNameIfNull("gmtModified", new Date(), metaObject);
        
        // 支持新字段名
        this.setFieldValByNameIfNull("createTime", LocalDateTime.now(), metaObject);
        this.setFieldValByNameIfNull("updateTime", LocalDateTime.now(), metaObject);
        
        // 其他字段（可选）
        this.setFieldValByNameIfNull("deleted", 0, metaObject);
        this.setFieldValByNameIfNull("lockVersion", 0, metaObject);
    }

    /**
     * 当没有值的时候再设置属性，如果有值则不设置。主要是方便单元测试
     * @param fieldName
     * @param fieldVal
     * @param metaObject
     */
    private void setFieldValByNameIfNull(String fieldName, Object fieldVal, MetaObject metaObject) {
        // 检查字段是否存在，避免字段不存在时抛出异常
        if (metaObject.hasSetter(fieldName) && metaObject.getValue(fieldName) == null) {
            this.setFieldValByName(fieldName, fieldVal, metaObject);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        // 支持旧字段名（向后兼容）
        if (metaObject.hasSetter("gmtModified")) {
            this.setFieldValByName("gmtModified", new Date(), metaObject);
        }
        
        // 支持新字段名
        if (metaObject.hasSetter("updateTime")) {
            this.setFieldValByName("updateTime", LocalDateTime.now(), metaObject);
        }
    }
}

