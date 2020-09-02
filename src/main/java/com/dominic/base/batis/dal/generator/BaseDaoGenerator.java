package com.dominic.base.batis.dal.generator;

import com.dominic.base.batis.config.ApplicationContextHolder;
import com.dominic.base.batis.config.BaseBatisConfig;
import com.dominic.base.batis.dal.dao.BaseDao;
import com.dominic.base.batis.dal.sql.sql_source.base_dao.*;
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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Create by dominic on 2020/8/5 09:41.
 */
public class BaseDaoGenerator {

    private static Map<String, BaseDao> cacheMap = new ConcurrentHashMap<>();

    public static <T> BaseDao<T> generateDao(@NonNull String tableName, @NonNull Class<T> entity) {
        ApplicationContext applicationContext = ApplicationContextHolder.getApplicationContext();
        String[] factories = applicationContext.getBeanNamesForType(SqlSessionFactory.class);
        if (factories.length <= 0) {
            throw new RuntimeException("mybatis还没有配置SqlSessionFactory！");
        }

        String sqlSessionFactoryName = factories[0]; //暂不支持多sqlSessionFactory的情况
        SqlSessionFactory sqlSessionFactory = (SqlSessionFactory) applicationContext.getBean(sqlSessionFactoryName);

        String beanClassName = EntityUtils.getDaoClassName(entity, EntityUtils.baseDaoSuffix);
        String beanName = EntityUtils.getDaoBeanName(entity, EntityUtils.baseDaoSuffix);
        if (applicationContext.containsBean(beanName)) {
            return (BaseDao<T>) applicationContext.getBean(beanName);
        } else if (cacheMap.containsKey(beanName)){
            return (BaseDao<T>) cacheMap.get(beanName);
        }

        //config MappedStatement
        Configuration configuration = sqlSessionFactory.getConfiguration();
        configMappedStatement(configuration, beanClassName, entity, tableName);

        //get dao
        Class<?> beanDaoClass = EntityUtils.getBeanDaoClass(beanClassName, entity, BaseDao.class);
        if (applicationContext instanceof BeanDefinitionRegistry) {
            BeanDefinitionRegistry registry = (BeanDefinitionRegistry) applicationContext;
            AnnotatedGenericBeanDefinition beanDefinition = new AnnotatedGenericBeanDefinition(beanDaoClass);
            beanDefinition.getConstructorArgumentValues().addGenericArgumentValue(beanDaoClass);
            beanDefinition.setBeanClass(MapperFactoryBean.class);
            beanDefinition.getPropertyValues().add("sqlSessionFactory", sqlSessionFactory);
            registry.registerBeanDefinition(beanName, beanDefinition);
            BaseDao<T> beanDao = (BaseDao<T>)applicationContext.getBean(beanName);
            return beanDao;
        } else {
            try {
                if (!configuration.hasMapper(beanDaoClass)) {
                    configuration.addMapper(beanDaoClass);
                }
                MapperFactoryBean mapperFactoryBean = new MapperFactoryBean(beanDaoClass);
                mapperFactoryBean.setSqlSessionFactory(sqlSessionFactory);
                BaseDao<T> beanDao = (BaseDao<T>) mapperFactoryBean.getObject();
                cacheMap.put(beanName, beanDao);
                return beanDao;
            } catch (Exception e) {
                throw new RuntimeException("获取beanDao出错：" + e.getMessage());
            }
        }
    }

    private static <T> void configMappedStatement(Configuration configuration, String beanClassName, Class<T> entity, String tableName) {
        //selectOne
        String statementId = beanClassName + "." + "selectOne";
        List<ResultMap> resultMaps = new ArrayList<>();
        Class<?> resultType = entity;
        List<ResultMapping> resultMappingList = new ArrayList<>();
        List<Field> fieldList = EntityUtils.getFields(entity);
        fieldList.forEach(field -> {
            String propertyName = field.getName();
            String columnName = EntityUtils.getColumnName(field, BaseBatisConfig.mapUnderscoreToCamelCase);
            ResultMapping resultMapping = new ResultMapping.Builder(configuration, propertyName, columnName, field.getType()).build();
            resultMappingList.add(resultMapping);
        });
        ResultMap inlineResultMap = new ResultMap.Builder(configuration, statementId + "-Inline",
                resultType, resultMappingList, true).build();
        resultMaps.add(inlineResultMap);
        BaseDaoSqlSourceHelper sqlSourceHelper = new BaseDaoSqlSourceHelper(configuration, tableName, entity, fieldList);
        BaseDaoSelectSqlSource sqlSource = new BaseDaoSelectSqlSource(sqlSourceHelper);
        MappedStatement.Builder builder = new MappedStatement.Builder(configuration, statementId, sqlSource, SqlCommandType.SELECT);
        MappedStatement selectOneStatement = builder.resultMaps(resultMaps).build();
        configuration.addMappedStatement(selectOneStatement);

        //selectById
        statementId = beanClassName + "." + "selectById";
        BaseDaoSelectByIdSqlSource selectById_sqlSource = new BaseDaoSelectByIdSqlSource(sqlSourceHelper);
        MappedStatement selectByIdStatement = new MappedStatement.Builder(configuration, statementId, selectById_sqlSource, SqlCommandType.SELECT)
                .resultMaps(Collections.singletonList(inlineResultMap)).build();
        configuration.addMappedStatement(selectByIdStatement);

        //selectList
        statementId = beanClassName + "." + "selectList";
        MappedStatement selectListStatement = new MappedStatement.Builder(configuration, statementId, sqlSource, SqlCommandType.SELECT)
                .resultMaps(Collections.singletonList(inlineResultMap)).build();
        configuration.addMappedStatement(selectListStatement);

        //selectCount
        statementId = beanClassName + "." + "selectCount";
        ResultMap selectCount_inlineResultMap = new ResultMap.Builder(configuration, statementId + "-Inline",
                Long.class, new ArrayList<>(), false).build();
        BaseDaoSelectCountSqlSource selectCountSqlSource = new BaseDaoSelectCountSqlSource(sqlSourceHelper);
        MappedStatement selectCountStatement = new MappedStatement.Builder(configuration, statementId, selectCountSqlSource, SqlCommandType.SELECT)
                .resultMaps(Collections.singletonList(selectCount_inlineResultMap))
                .build();
        configuration.addMappedStatement(selectCountStatement);

        //insert
        statementId = beanClassName + "." + "insert";
        BaseDaoInsertSqlSource insert_sqlSource = new BaseDaoInsertSqlSource(sqlSourceHelper, statementId);
        MappedStatement insertStatement = new MappedStatement.Builder(configuration, statementId, insert_sqlSource, SqlCommandType.INSERT).build();
        configuration.addMappedStatement(insertStatement);

        //save
        statementId = beanClassName + "." + "save";
        BaseDaoInsertSqlSource save_sqlSource = new BaseDaoInsertSqlSource(sqlSourceHelper, statementId);
        MappedStatement saveStatement = new MappedStatement.Builder(configuration, statementId, save_sqlSource, SqlCommandType.INSERT).build();
        configuration.addMappedStatement(saveStatement);

        //insertBatch
        statementId = beanClassName + "." + "insertBatch";
        BaseDaoInsertBatchSqlSource insertBatch_sqlSource = new BaseDaoInsertBatchSqlSource(sqlSourceHelper, statementId);
        MappedStatement insertBatchStatement = new MappedStatement.Builder(configuration, statementId, insertBatch_sqlSource, SqlCommandType.INSERT).build();
        configuration.addMappedStatement(insertBatchStatement);

        //saveBatch
        statementId = beanClassName + "." + "saveBatch";
        BaseDaoInsertBatchSqlSource saveBatch_sqlSource = new BaseDaoInsertBatchSqlSource(sqlSourceHelper, statementId);
        MappedStatement saveBatchStatement = new MappedStatement.Builder(configuration, statementId, saveBatch_sqlSource, SqlCommandType.INSERT).build();
        configuration.addMappedStatement(saveBatchStatement);

        //update
        statementId = beanClassName + "." + "update";
        BaseDaoUpdateSqlSource update_sqlSource = new BaseDaoUpdateSqlSource(sqlSourceHelper);
        MappedStatement updateStatement = new MappedStatement.Builder(configuration, statementId, update_sqlSource, SqlCommandType.UPDATE).build();
        configuration.addMappedStatement(updateStatement);

        //deleteById
        statementId = beanClassName + "." + "deleteById";
        BaseDaoDeleteByIdSqlSource deleteById_sqlSource = new BaseDaoDeleteByIdSqlSource(sqlSourceHelper);
        MappedStatement deleteStatement = new MappedStatement.Builder(configuration, statementId, deleteById_sqlSource, SqlCommandType.DELETE).build();
        configuration.addMappedStatement(deleteStatement);
    }
}
