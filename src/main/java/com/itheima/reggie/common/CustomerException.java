package com.itheima.reggie.common;

public class CustomerException extends RuntimeException {
    public CustomerException(){

    }
    public CustomerException(String msg){
        super(msg);
    }

}
