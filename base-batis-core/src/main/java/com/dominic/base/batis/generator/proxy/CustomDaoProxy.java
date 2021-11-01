package com.dominic.base.batis.generator.proxy;

import com.dominic.base.batis.generator.dao.CustomDao;
import com.dominic.base.batis.generator.CustomDaoGenerator;
import org.apache.ibatis.session.SqlSessionFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Create by dominic on 2020/8/10 10:54.
 */
public class CustomDaoProxy<T> implements InvocationHandler {

    private final Class<T> entityClass;

    private volatile CustomDao<T> entityCustomDao = null;

    private SqlSessionFactory sqlSessionFactory = null;

    public CustomDaoProxy(Class<T> entityClass) {
        this.entityClass = entityClass;
    }
    public CustomDaoProxy(SqlSessionFactory sqlSessionFactory, Class<T> entityClass) {
        this.sqlSessionFactory = sqlSessionFactory;
        this.entityClass = entityClass;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        CustomDao<T> customDao = getEntityCustomDao();
        return method.invoke(customDao, args);
    }

    private CustomDao<T> getEntityCustomDao() {
        if (entityCustomDao == null) synchronized (entityClass) {
            if (entityCustomDao == null) {
                entityCustomDao = sqlSessionFactory == null ? CustomDaoGenerator.generateDao(entityClass) : CustomDaoGenerator.generateDao(sqlSessionFactory, entityClass);
            }
        }
        return entityCustomDao;
    }
}
