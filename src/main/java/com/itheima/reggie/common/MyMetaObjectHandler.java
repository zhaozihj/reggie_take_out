package com.itheima.reggie.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.tomcat.jni.Local;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    /*
    插入操作的时候自动填充
     */
    //这个方法执行insert的时候会执行
    //metaObject中存储了被修改的这个员工的所有信息，也就是元数据
    @Override
    public void insertFill(MetaObject metaObject) {

        log.info("公共字段自动填充[insert]...");
        log.info(metaObject.toString());
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime",LocalDateTime.now());

        //注意这个BaseContext不能在类中当作属性获取，就是写在外面然后方法里用，是不可以的
        metaObject.setValue("createUser",BaseContext.getCurrentId());
        metaObject.setValue("updateUser", BaseContext.getCurrentId());

    }

    /*
    更新操作的时候自动填充
     */
    //这个方法在update的时候会执行
    @Override
    public void updateFill(MetaObject metaObject) {
log.info("公共字段自动填充[update]");
log.info(metaObject.toString());
        metaObject.setValue("updateTime",LocalDateTime.now());
        metaObject.setValue("updateUser", BaseContext.getCurrentId());
    }


}
