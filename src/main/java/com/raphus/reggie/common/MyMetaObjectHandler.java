package com.raphus.reggie.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 元数据对象处理器(进行公共字段填充)
 */
@Component
@Slf4j
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("公共字段填充[insert]...");
        log.info("当前线程为:{}",Thread.currentThread().getName());
        //获得当前登录用户的id
        Long currentId = BaseContext.getCurrentId();
        metaObject.setValue("createUser",currentId);
        metaObject.setValue("updateUser",currentId);
        metaObject.setValue("createTime",LocalDateTime.now());
        metaObject.setValue("updateTime",LocalDateTime.now());

    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("公共字段填充[update]...");
        log.info("当前线程为:{}",Thread.currentThread().getName());
        metaObject.setValue("updateUser",BaseContext.getCurrentId());
        metaObject.setValue("updateTime",LocalDateTime.now());

    }
}
