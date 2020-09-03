package com.dominic.base.batis;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@MapperScan("com.dominic.base.batis.dal.dao")
@ComponentScan("com.dominic.base.batis.config")
@SpringBootApplication
public class BaseBatisTestApplication {
    public static void main(String[] args) {
        SpringApplication.run(BaseBatisTestApplication.class, args);
    }
}
