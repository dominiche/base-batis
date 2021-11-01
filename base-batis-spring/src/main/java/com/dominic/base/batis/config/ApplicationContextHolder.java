package com.dominic.base.batis.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

/**
 * Create by dominic on 2020/7/22 17:51.
 */
@Configuration
public class ApplicationContextHolder implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ApplicationContextHolder.applicationContext = applicationContext;

        //设置默认sqlSessionFactoryName
        if (StringUtils.isBlank(BaseBatisConfig.sqlSessionFactoryName)) {
            BaseBatisConfig.setSqlSessionFactoryName("sqlSessionFactory");
        }
    }
}
