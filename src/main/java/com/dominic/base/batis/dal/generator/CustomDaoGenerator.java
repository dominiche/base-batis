package com.dominic.base.batis.dal.generator;

import com.dominic.base.batis.config.ApplicationContextHolder;
import com.dominic.base.batis.config.BaseBatisConfig;
import com.dominic.base.batis.dal.dao.CustomDao;
import com.dominic.base.batis.dal.sql.sql_source.CustomSqlSource;
import com.dominic.base.batis.util.EntityUtils;
import lombok.NonNull;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Create by dominic on 2020/7/29 17:25.
 */
public class CustomDaoGenerator {

    private static Map<String, CustomDao> cacheMap = new ConcurrentHashMap<>();

    public static <T> CustomDao<T> generateDao(@NonNull Class<T> entity) {
        ApplicationContext applicationContext = ApplicationContextHolder.getApplicationContext();
        String[] factories = applicationContext.getBeanNamesForType(SqlSessionFactory.class);
        if (factories.length <= 0) {
            throw new RuntimeException("mybatis还没有配置SqlSessionFactory！");
        }

        String sqlSessionFactoryName = factories[0]; //暂不支持多sqlSessionFactory的情况
        SqlSessionFactory sqlSessionFactory = (SqlSessionFactory) applicationContext.getBean(sqlSessionFactoryName);

        String beanClassName = EntityUtils.getDaoClassName(entity, EntityUtils.customDaoSuffix);
        String beanName = EntityUtils.getDaoBeanName(entity, EntityUtils.customDaoSuffix);
        if (applicationContext.containsBean(beanName)) {
            return (CustomDao<T>) applicationContext.getBean(beanName);
        } else if (cacheMap.containsKey(beanName)){
            return (CustomDao<T>) cacheMap.get(beanName);
        }

        //config MappedStatement
        Configuration configuration = sqlSessionFactory.getConfiguration();
        configMappedStatement(configuration, beanClassName, entity);


        //get dao
        Class<?> beanDaoClass = EntityUtils.getBeanDaoClass(beanClassName, entity, CustomDao.class);
        if (applicationContext instanceof BeanDefinitionRegistry) {
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
        } else {
            try {
                if (!configuration.hasMapper(beanDaoClass)) {
                    configuration.addMapper(beanDaoClass);
                }
                MapperFactoryBean mapperFactoryBean = new MapperFactoryBean(beanDaoClass);
                mapperFactoryBean.setSqlSessionFactory(sqlSessionFactory);
                CustomDao<T> beanDao = (CustomDao<T>) mapperFactoryBean.getObject();
                cacheMap.put(beanName, beanDao);
                return beanDao;
            } catch (Exception e) {
                throw new RuntimeException("获取beanDao出错：" + e.getMessage());
            }
        }
    }

    private static <T> void configMappedStatement(Configuration configuration, String beanClassName, @NonNull Class<T> entity) {
        //selectOne
        String statementId = beanClassName + "." + "selectOne";
        List<ResultMap> resultMaps = new ArrayList<>();
        Class<?> resultType = entity;
        List<ResultMapping> resultMappingList = new ArrayList<>();
        EntityUtils.getFields(entity).forEach(field -> {
            String propertyName = field.getName();
            String columnName = EntityUtils.getColumnName(field, BaseBatisConfig.mapUnderscoreToCamelCase);
            ResultMapping resultMapping = new ResultMapping.Builder(configuration, propertyName, columnName, field.getType()).build();
            resultMappingList.add(resultMapping);
        });
        ResultMap inlineResultMap = new ResultMap.Builder(configuration, statementId + "-Inline",
                resultType, resultMappingList, true).build();
        resultMaps.add(inlineResultMap);
        CustomSqlSource sqlSource = new CustomSqlSource(configuration);
        MappedStatement.Builder builder = new MappedStatement.Builder(configuration, statementId, sqlSource, SqlCommandType.SELECT);
        MappedStatement selectOneStatement = builder.resultMaps(resultMaps).build();
        configuration.addMappedStatement(selectOneStatement);

        //selectOneForMap
        statementId = beanClassName + "." + "selectOneForMap";
        ResultMap selectOneForMap_inlineResultMap = new ResultMap.Builder(configuration, statementId + "-Inline",
                Map.class, resultMappingList, true).build();
        MappedStatement selectOneForMapStatement = new MappedStatement.Builder(configuration, statementId, sqlSource, SqlCommandType.SELECT)
                .resultMaps(Collections.singletonList(selectOneForMap_inlineResultMap)).build();
        configuration.addMappedStatement(selectOneForMapStatement);

        //selectList
        statementId = beanClassName + "." + "selectList";
        MappedStatement selectListStatement = new MappedStatement.Builder(configuration, statementId, sqlSource, SqlCommandType.SELECT)
                .resultMaps(Collections.singletonList(inlineResultMap)).build();
        configuration.addMappedStatement(selectListStatement);

        //selectListForMap
        statementId = beanClassName + "." + "selectListForMap";
        MappedStatement selectListForMapStatement = new MappedStatement.Builder(configuration, statementId, sqlSource, SqlCommandType.SELECT)
                .resultMaps(Collections.singletonList(selectOneForMap_inlineResultMap)).build();
        configuration.addMappedStatement(selectListForMapStatement);

        //insert
        statementId = beanClassName + "." + "insert";
        MappedStatement insertStatement = new MappedStatement.Builder(configuration, statementId, sqlSource, SqlCommandType.INSERT).build();
        configuration.addMappedStatement(insertStatement);

        //update
        statementId = beanClassName + "." + "update";
        MappedStatement updateStatement = new MappedStatement.Builder(configuration, statementId, sqlSource, SqlCommandType.UPDATE).build();
        configuration.addMappedStatement(updateStatement);

        //delete
        statementId = beanClassName + "." + "delete";
        MappedStatement deleteStatement = new MappedStatement.Builder(configuration, statementId, sqlSource, SqlCommandType.DELETE).build();
        configuration.addMappedStatement(deleteStatement);
    }
}
