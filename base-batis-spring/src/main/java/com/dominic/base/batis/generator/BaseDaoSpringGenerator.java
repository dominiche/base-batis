package com.dominic.base.batis.generator;

import com.dominic.base.batis.config.ApplicationContextHolder;
import com.dominic.base.batis.config.BaseBatisConfig;
import com.dominic.base.batis.generator.dao.BaseDao;
import com.dominic.base.batis.util.EntityUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.lang.NonNull;

/**
 * Create by dominic on 2020/8/5 09:41.
 */
public class BaseDaoSpringGenerator extends BaseDaoGenerator {

    public static <T> BaseDao<T> generateDao(@lombok.NonNull String tableName, @lombok.NonNull Class<T> entity) {
        return BaseDaoSpringGenerator.generateDao(BaseBatisConfig.sqlSessionFactory, tableName, entity);
    }
    public static <T> BaseDao<T> generateDao(SqlSessionFactory sqlSessionFactory, @NonNull String tableName, @NonNull Class<T> entity) {
        ApplicationContext applicationContext = ApplicationContextHolder.getApplicationContext();
        if (applicationContext == null)  {
            throw new RuntimeException("不是spring环境，或ApplicationContextHolder没有配置！");
        }
        if (!(applicationContext instanceof BeanDefinitionRegistry)) {
            return BaseDaoGenerator.generateDao(sqlSessionFactory, tableName, entity);
        }

        if (sqlSessionFactory == null) {
            if (StringUtils.isNotBlank(BaseBatisConfig.sqlSessionFactoryName)) {
                String sqlSessionFactoryName = BaseBatisConfig.sqlSessionFactoryName;
                sqlSessionFactory = applicationContext.getBean(sqlSessionFactoryName, SqlSessionFactory.class);
            }

            if (sqlSessionFactory == null) throw new RuntimeException("BaseBatisConfig还没有配置sqlSessionFactory！");

            BaseBatisConfig.setSqlSessionFactory(sqlSessionFactory);
        }

        String beanClassName = EntityUtils.getDaoClassName(entity, EntityUtils.baseDaoSuffix);
        String beanName = EntityUtils.getDaoBeanName(entity, EntityUtils.baseDaoSuffix);
        if (applicationContext.containsBean(beanName)) {
            return (BaseDao<T>) applicationContext.getBean(beanName);
        }

        //config MappedStatement
        Configuration configuration = sqlSessionFactory.getConfiguration();
        if (BaseBatisConfig.mapUnderscoreToCamelCase == null) BaseBatisConfig.setMapUnderscoreToCamelCase(configuration.isMapUnderscoreToCamelCase());
        configMappedStatement(configuration, beanClassName, entity, tableName);

        //get dao
        Class<?> beanDaoClass = EntityUtils.getBeanDaoClass(beanClassName, entity, BaseDao.class);
        BeanDefinitionRegistry registry = (BeanDefinitionRegistry) applicationContext;
        AnnotatedGenericBeanDefinition beanDefinition = new AnnotatedGenericBeanDefinition(beanDaoClass);
        beanDefinition.getConstructorArgumentValues().addGenericArgumentValue(beanDaoClass);
        beanDefinition.setBeanClass(MapperFactoryBean.class);
        beanDefinition.getPropertyValues().add("sqlSessionFactory", sqlSessionFactory);
        registry.registerBeanDefinition(beanName, beanDefinition);
        BaseDao<T> beanDao = (BaseDao<T>)applicationContext.getBean(beanName);
        return beanDao;
    }
}
