package com.itheima.reggie.common;

import com.itheima.reggie.entity.Employee;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class MyConfig  {
    @Bean
    public Employee get(){
        log.info("加载bean");
        return new Employee();
    }
}
