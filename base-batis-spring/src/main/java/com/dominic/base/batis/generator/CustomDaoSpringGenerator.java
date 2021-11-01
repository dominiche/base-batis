package com.dominic.base.batis.generator;

import com.dominic.base.batis.config.ApplicationContextHolder;
import com.dominic.base.batis.config.BaseBatisConfig;
import com.dominic.base.batis.generator.dao.CustomDao;
import com.dominic.base.batis.util.EntityUtils;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;

/**
 * Create by dominic on 2020/7/29 17:25.
 */
public class CustomDaoSpringGenerator extends CustomDaoGenerator {

    public static <T> CustomDao<T> generateDao(@NonNull Class<T> entity) {
        return CustomDaoSpringGenerator.generateDao(BaseBatisConfig.sqlSessionFactory, entity);
    }
    public static <T> CustomDao<T> generateDao(SqlSessionFactory sqlSessionFactory, @NonNull Class<T> entity) {
        ApplicationContext applicationContext = ApplicationContextHolder.getApplicationContext();
        if (applicationContext == null)  {
            throw new RuntimeException("不是spring环境，或ApplicationContextHolder没有配置！");
        }
        if (!(applicationContext instanceof BeanDefinitionRegistry)) {
            return CustomDaoGenerator.generateDao(sqlSessionFactory, entity);
        }

        if (sqlSessionFactory == null) {
            if (StringUtils.isNotBlank(BaseBatisConfig.sqlSessionFactoryName)) {
                String sqlSessionFactoryName = BaseBatisConfig.sqlSessionFactoryName;
                sqlSessionFactory = applicationContext.getBean(sqlSessionFactoryName, SqlSessionFactory.class);
            }

            if (sqlSessionFactory == null) throw new RuntimeException("BaseBatisConfig还没有配置sqlSessionFactory！");

            BaseBatisConfig.setSqlSessionFactory(sqlSessionFactory);
        }

        String beanClassName = EntityUtils.getDaoClassName(entity, EntityUtils.customDaoSuffix);
        String beanName = EntityUtils.getDaoBeanName(entity, EntityUtils.customDaoSuffix);
        if (applicationContext.containsBean(beanName)) {
            return (CustomDao<T>) applicationContext.getBean(beanName);
        }

        //config MappedStatement
        Configuration configuration = sqlSessionFactory.getConfiguration();
        if (BaseBatisConfig.mapUnderscoreToCamelCase == null) BaseBatisConfig.setMapUnderscoreToCamelCase(configuration.isMapUnderscoreToCamelCase());
        configMappedStatement(configuration, beanClassName, entity);


        //get dao
        Class<?> beanDaoClass = EntityUtils.getBeanDaoClass(beanClassName, entity, CustomDao.class);
        BeanDefinitionRegistry registry = (BeanDefinitionRegistry) applicationContext;
        AnnotatedGenericBeanDefinition beanDefinition = new AnnotatedGenericBeanDefinition(beanDaoClass);
        // the mapper interface is the original class of the bean
        // but, the actual class of the bean is MapperFactoryBean
        beanDefinition.getConstructorArgumentValues().addGenericArgumentValue(beanDaoClass); // issue #59
        beanDefinition.setBeanClass(MapperFactoryBean.class);
        beanDefinition.getPropertyValues().add("sqlSessionFactory", sqlSessionFactory);
        registry.registerBeanDefinition(beanName, beanDefinition);
        CustomDao<T> beanDao = (CustomDao<T>)applicationContext.getBean(beanName);
        return beanDao;
    }
}
