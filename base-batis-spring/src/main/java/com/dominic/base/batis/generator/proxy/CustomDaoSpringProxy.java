package com.dominic.base.batis.generator.proxy;

import com.dominic.base.batis.generator.dao.CustomDao;
import com.dominic.base.batis.generator.CustomDaoSpringGenerator;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Create by dominic on 2020/8/10 10:54.
 */
public class CustomDaoSpringProxy<T> implements InvocationHandler {

    private final Class<T> entityClass;

    private volatile CustomDao<T> entityCustomDao = null;

    public CustomDaoSpringProxy(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        CustomDao<T> CustomDao = getEntityCustomDao();
        return method.invoke(CustomDao, args);
    }

    private CustomDao<T> getEntityCustomDao() {
        if (entityCustomDao == null) synchronized (entityClass) {
            if (entityCustomDao == null) {
                entityCustomDao = CustomDaoSpringGenerator.generateDao(entityClass);
            }
        }
        return entityCustomDao;
    }
}
