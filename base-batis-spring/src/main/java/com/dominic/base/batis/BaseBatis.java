package com.dominic.base.batis;

import com.dominic.base.batis.config.ApplicationContextHolder;
import com.dominic.base.batis.generator.BaseDaoSpringGenerator;
import com.dominic.base.batis.generator.CustomDaoSpringGenerator;
import com.dominic.base.batis.generator.dao.BaseDao;
import com.dominic.base.batis.generator.dao.CustomDao;
import com.dominic.base.batis.generator.proxy.BaseDaoSpringProxy;
import com.dominic.base.batis.generator.proxy.CustomDaoSpringProxy;
import lombok.NonNull;
import org.apache.ibatis.session.SqlSessionFactory;

import java.lang.reflect.Proxy;

/**
 * Create by dominic on 2020/7/28 16:12.
 */
public class BaseBatis {


    /**
     * 做了动态代理，即在第一次调用dao方法时才真正去生成CustomDao
     */
    @SuppressWarnings("unchecked")
    public static <T> CustomDao<T> getCustomDao(@NonNull Class<T> entity) {
        CustomDaoSpringProxy<T> customDaoSpringProxy = new CustomDaoSpringProxy<>(entity);
        return (CustomDao<T>) Proxy.newProxyInstance(BaseBatisCore.class.getClassLoader(), new Class[] { CustomDao.class }, customDaoSpringProxy);
    }
    /**
     * 做了动态代理，即在第一次调用dao方法时才真正去生成BaseDao
     */
    @SuppressWarnings("unchecked")
    public static <T> BaseDao<T> getBaseDao(@NonNull String tableName, @NonNull Class<T> entity) {
        BaseDaoSpringProxy<T> baseDaoSpringProxy = new BaseDaoSpringProxy<>(tableName, entity);
        return (BaseDao<T>) Proxy.newProxyInstance(BaseBatisCore.class.getClassLoader(), new Class[] { BaseDao.class }, baseDaoSpringProxy);
    }

    /**
     * 直接生成CustomDao
     * 注意：需要在BaseBatisConfig初始化之后才能支持，否者会报错，拿不到sqlSessionFactory
     */
    public static <T> CustomDao<T> getDirectCustomDao(@NonNull Class<T> entity) {
        return CustomDaoSpringGenerator.generateDao(entity);
    }
    /**
     * 直接生成BaseDao
     * 注意：需要在BaseBatisConfig初始化之后才能支持，否者会报错，拿不到sqlSessionFactory
     */
    public static <T> BaseDao<T> getDirectBaseDao(@NonNull String tableName, @NonNull Class<T> entity) {
        return BaseDaoSpringGenerator.generateDao(tableName, entity);
    }


    /***
     * 多数据源支持
     */
    public static BaseBatisBuilder sqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        return new BaseBatisBuilder(sqlSessionFactory);
    }

    /***
     * 多数据源支持
     */
    public static BaseBatisBuilder sqlSessionFactory(String sqlSessionFactoryName) {
        SqlSessionFactory sqlSessionFactory = ApplicationContextHolder.getApplicationContext().getBean(sqlSessionFactoryName, SqlSessionFactory.class);
        return new BaseBatisBuilder(sqlSessionFactory);
    }

    public static class BaseBatisBuilder {
        private final SqlSessionFactory sqlSessionFactory;
        public BaseBatisBuilder(SqlSessionFactory sqlSessionFactory) {
            this.sqlSessionFactory = sqlSessionFactory;
        }

        @SuppressWarnings("unchecked")
        public static <T> CustomDao<T> getCustomDao(@NonNull Class<T> entity) {
            CustomDaoSpringProxy<T> customDaoSpringProxy = new CustomDaoSpringProxy<>(entity);
            return (CustomDao<T>) Proxy.newProxyInstance(BaseBatisCore.class.getClassLoader(), new Class[] { CustomDao.class }, customDaoSpringProxy);
        }

        @SuppressWarnings("unchecked")
        public static <T> BaseDao<T> getBaseDao(@NonNull String tableName, @NonNull Class<T> entity) {
            BaseDaoSpringProxy<T> baseDaoSpringProxy = new BaseDaoSpringProxy<>(tableName, entity);
            return (BaseDao<T>) Proxy.newProxyInstance(BaseBatisCore.class.getClassLoader(), new Class[] { BaseDao.class }, baseDaoSpringProxy);
        }


        public <T> CustomDao<T> getDirectCustomDao(@NonNull Class<T> entity) {
            return CustomDaoSpringGenerator.generateDao(sqlSessionFactory, entity);
        }

        public <T> BaseDao<T> getDirectBaseDao(@NonNull String tableName, @NonNull Class<T> entity) {
            return BaseDaoSpringGenerator.generateDao(sqlSessionFactory, tableName, entity);
        }
    }
}
