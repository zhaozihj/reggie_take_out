package com.itheima.reggie.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

//这里是指所有Controller注解和RestController注解标记的类中的方法异常捕捉
@ControllerAdvice(annotations = {RestController.class, Controller.class})
//返回json要加上这个注解
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {

    //这里的注解的value是这个方法捕捉的异常类型
    @ExceptionHandler({SQLIntegrityConstraintViolationException.class})
    //参数ex是异常信息
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException ex){
        //获取异常信息
        //Duplicate entry '123' for key 'idx_username'
        log.error(ex.getMessage());

        String message=ex.getMessage();
        if(message.contains("Duplicate entry")){
            String[] s = message.split(" ");
            //从异常信息中获取重复的用户名
            message=s[2]+"已存在";
            //这个msg内容会在页面显示
            return R.error(message);
        }
        return R.error("未知错误");
    }

    @ExceptionHandler({CustomerException.class})
    public R<String> exceptionHandler1(CustomerException ex){
        //ex.getMessage()获取的就是自己传递的报错信息，就是那个异常类构造器参数
        return R.error(ex.getMessage());
    }


}
