package com.dominic.base.batis;

import com.dominic.base.batis.dal.dao.BaseDao;
import com.dominic.base.batis.dal.dao.CustomDao;
import com.dominic.base.batis.dal.generator.BaseDaoGenerator;
import com.dominic.base.batis.dal.generator.CustomDaoGenerator;
import com.dominic.base.batis.dal.generator.proxy.BaseDaoProxy;
import com.dominic.base.batis.dal.generator.proxy.CustomDaoProxy;
import lombok.NonNull;

import java.lang.reflect.Proxy;

/**
 * Create by dominic on 2020/7/28 16:12.
 */
public class BaseBatis {

    @SuppressWarnings("unchecked")
    public static <T> CustomDao<T> getCustomDao(@NonNull Class<T> entity) {
        CustomDaoProxy<T> customDaoProxy = new CustomDaoProxy<>(entity);
        return (CustomDao<T>) Proxy.newProxyInstance(BaseBatis.class.getClassLoader(), new Class[] { CustomDao.class }, customDaoProxy);
    }
    @SuppressWarnings("unchecked")
    public static <T> BaseDao<T> getBaseDao(@NonNull String tableName, @NonNull Class<T> entity) {
        BaseDaoProxy<T> baseDaoProxy = new BaseDaoProxy<>(tableName, entity);
        return (BaseDao<T>) Proxy.newProxyInstance(BaseBatis.class.getClassLoader(), new Class[] { BaseDao.class }, baseDaoProxy);
    }

    public static <T> CustomDao<T> getDirectCustomDao(@NonNull Class<T> entity) {
        return CustomDaoGenerator.generateDao(entity);
    }
    public static <T> BaseDao<T> getDirectBaseDao(@NonNull String tableName, @NonNull Class<T> entity) {
        return BaseDaoGenerator.generateDao(tableName, entity);
    }
}
