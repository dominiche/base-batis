package com.dominic.base.batis.dal.generator.proxy;

import com.dominic.base.batis.dal.dao.BaseDao;
import com.dominic.base.batis.dal.generator.BaseDaoGenerator;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Create by dominic on 2020/8/10 10:54.
 */
public class BaseDaoProxy<T> implements InvocationHandler {

    private final String tableName;
    private final Class<T> entityClass;

    private volatile BaseDao<T> entityBaseDao = null;

    public BaseDaoProxy(String tableName, Class<T> entityClass) {
        this.tableName = tableName;
        this.entityClass = entityClass;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        BaseDao<T> baseDao = getEntityBaseDao();
        return method.invoke(baseDao, args);
    }

    private BaseDao<T> getEntityBaseDao() {
        if (entityBaseDao == null) synchronized (entityClass) {
            if (entityBaseDao == null) {
                entityBaseDao = BaseDaoGenerator.generateDao(tableName, entityClass);
            }
        }
        return entityBaseDao;
    }
}
