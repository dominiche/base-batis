package com.dominic.base.batis;

import com.dominic.base.batis.generator.BaseDaoGenerator;
import com.dominic.base.batis.generator.CustomDaoGenerator;
import com.dominic.base.batis.generator.dao.BaseDao;
import com.dominic.base.batis.generator.dao.CustomDao;
import com.dominic.base.batis.generator.proxy.BaseDaoProxy;
import com.dominic.base.batis.generator.proxy.CustomDaoProxy;
import lombok.NonNull;
import org.apache.ibatis.session.SqlSessionFactory;

import java.lang.reflect.Proxy;

/**
 * Create by dominic on 2020/7/28 16:12.
 */
public class BaseBatisCore {

    /**
     * 做了动态代理，即在第一次调用dao方法时才真正去生成CustomDao
     */
    @SuppressWarnings("unchecked")
    public static <T> CustomDao<T> getCustomDao(@NonNull Class<T> entity) {
        CustomDaoProxy<T> customDaoProxy = new CustomDaoProxy<>(entity);
        return (CustomDao<T>) Proxy.newProxyInstance(BaseBatisCore.class.getClassLoader(), new Class[] { CustomDao.class }, customDaoProxy);
    }
    /**
     * 做了动态代理，即在第一次调用dao方法时才真正去生成BaseDao
     */
    @SuppressWarnings("unchecked")
    public static <T> BaseDao<T> getBaseDao(@NonNull String tableName, @NonNull Class<T> entity) {
        BaseDaoProxy<T> baseDaoProxy = new BaseDaoProxy<>(tableName, entity);
        return (BaseDao<T>) Proxy.newProxyInstance(BaseBatisCore.class.getClassLoader(), new Class[] { BaseDao.class }, baseDaoProxy);
    }

    /**
     * 直接生成CustomDao
     * 注意：需要在BaseBatisConfig初始化之后才能支持，否者会报错，拿不到sqlSessionFactory
     */
    public static <T> CustomDao<T> getDirectCustomDao(@NonNull Class<T> entity) {
        return CustomDaoGenerator.generateDao(entity);
    }
    /**
     * 直接生成BaseDao
     * 注意：需要在BaseBatisConfig初始化之后才能支持，否者会报错，拿不到sqlSessionFactory
     */
    public static <T> BaseDao<T> getDirectBaseDao(@NonNull String tableName, @NonNull Class<T> entity) {
        return BaseDaoGenerator.generateDao(tableName, entity);
    }


    /***
     * 多数据源支持
     */
    public static BaseBatisBuilder sqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        return new BaseBatisBuilder(sqlSessionFactory);
    }

    public static class BaseBatisBuilder {
        private final SqlSessionFactory sqlSessionFactory;
        public BaseBatisBuilder(SqlSessionFactory sqlSessionFactory) {
            this.sqlSessionFactory = sqlSessionFactory;
        }

        @SuppressWarnings("unchecked")
        public static <T> CustomDao<T> getCustomDao(@NonNull Class<T> entity) {
            CustomDaoProxy<T> customDaoProxy = new CustomDaoProxy<>(entity);
            return (CustomDao<T>) Proxy.newProxyInstance(BaseBatisCore.class.getClassLoader(), new Class[] { CustomDao.class }, customDaoProxy);
        }

        @SuppressWarnings("unchecked")
        public static <T> BaseDao<T> getBaseDao(@NonNull String tableName, @NonNull Class<T> entity) {
            BaseDaoProxy<T> baseDaoProxy = new BaseDaoProxy<>(tableName, entity);
            return (BaseDao<T>) Proxy.newProxyInstance(BaseBatisCore.class.getClassLoader(), new Class[] { BaseDao.class }, baseDaoProxy);
        }


        public <T> CustomDao<T> getDirectCustomDao(@NonNull Class<T> entity) {
            return CustomDaoGenerator.generateDao(sqlSessionFactory, entity);
        }

        public <T> BaseDao<T> getDirectBaseDao(@NonNull String tableName, @NonNull Class<T> entity) {
            return BaseDaoGenerator.generateDao(sqlSessionFactory, tableName, entity);
        }
    }
}
