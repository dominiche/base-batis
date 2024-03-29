package com.dominic.base.batis.generator;

import com.dominic.base.batis.config.BaseBatisConfig;
import com.dominic.base.batis.constant.ParamName;
import com.dominic.base.batis.generator.dao.BaseDao;
import com.dominic.base.batis.sql.sql_source.base_dao.*;
import com.dominic.base.batis.sql.sql_source.base_dao.delete.BaseDaoDeleteByIdSqlSource;
import com.dominic.base.batis.sql.sql_source.base_dao.insert.BaseDaoInsertBatchSqlSource;
import com.dominic.base.batis.sql.sql_source.base_dao.insert.BaseDaoInsertSqlSource;
import com.dominic.base.batis.sql.sql_source.base_dao.insert.BaseDaoSaveBatchSqlSource;
import com.dominic.base.batis.sql.sql_source.base_dao.insert.BaseDaoSaveSqlSource;
import com.dominic.base.batis.sql.sql_source.base_dao.select.BaseDaoSelectByIdSqlSource;
import com.dominic.base.batis.sql.sql_source.base_dao.select.BaseDaoSelectCountSqlSource;
import com.dominic.base.batis.sql.sql_source.base_dao.select.BaseDaoSelectSqlSource;
import com.dominic.base.batis.sql.sql_source.base_dao.update.BaseDaoUpdateByIdSqlSource;
import com.dominic.base.batis.sql.sql_source.base_dao.update.BaseDaoUpdateSqlSource;
import com.dominic.base.batis.util.EntityUtils;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.executor.keygen.Jdbc3KeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionManager;

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

    protected static final Map<String, BaseDao> cacheMap = new ConcurrentHashMap<>();

    public static <T> BaseDao<T> generateDao(@NonNull String tableName, @NonNull Class<T> entity) {
        return BaseDaoGenerator.generateDao(BaseBatisConfig.sqlSessionFactory, tableName, entity);
    }
    public static <T> BaseDao<T> generateDao(SqlSessionFactory sqlSessionFactory, @NonNull String tableName, @NonNull Class<T> entity) {
        if (sqlSessionFactory == null) {
            throw new RuntimeException("BaseBatisConfig还没有配置sqlSessionFactory！");
        }

        String beanClassName = EntityUtils.getDaoClassName(entity, EntityUtils.baseDaoSuffix);
        String beanName = EntityUtils.getDaoBeanName(entity, EntityUtils.baseDaoSuffix);
        String cacheKey = tableName + "_" + beanName;
        if (cacheMap.containsKey(cacheKey)){
            return (BaseDao<T>) cacheMap.get(cacheKey);
        }

        //config MappedStatement
        Configuration configuration = sqlSessionFactory.getConfiguration();
        if (BaseBatisConfig.mapUnderscoreToCamelCase == null) BaseBatisConfig.setMapUnderscoreToCamelCase(configuration.isMapUnderscoreToCamelCase());
        configMappedStatement(configuration, beanClassName, entity, tableName);

        //get dao
        Class<BaseDao<T>> beanDaoClass = (Class<BaseDao<T>>)EntityUtils.getBeanDaoClass(beanClassName, entity, BaseDao.class);
        try {
            if (!configuration.hasMapper(beanDaoClass)) {
                configuration.addMapper(beanDaoClass);
            }

            SqlSessionManager sqlSessionManager = SqlSessionManager.newInstance(sqlSessionFactory);
            BaseDao<T> beanDao = sqlSessionManager.getMapper(beanDaoClass);
            cacheMap.put(cacheKey, beanDao);
            return beanDao;
        } catch (Exception e) {
            throw new RuntimeException("获取beanDao出错：" + e.getMessage());
        }
    }

    protected static <T> void configMappedStatement(Configuration configuration, String beanClassName, Class<T> entity, String tableName) {
        //selectOne
        String statementId = beanClassName + "." + "selectOne";
        List<ResultMap> resultMaps = new ArrayList<>();
        Class<T> resultType = entity;
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
        BaseDaoInsertSqlSource insert_sqlSource = new BaseDaoInsertSqlSource(sqlSourceHelper);
        MappedStatement insertStatement = new MappedStatement.Builder(configuration, statementId, insert_sqlSource, SqlCommandType.INSERT).build();
        configuration.addMappedStatement(insertStatement);

        //insertBatch
        statementId = beanClassName + "." + "insertBatch";
        BaseDaoInsertBatchSqlSource insertBatch_sqlSource = new BaseDaoInsertBatchSqlSource(sqlSourceHelper);
        MappedStatement insertBatchStatement = new MappedStatement.Builder(configuration, statementId, insertBatch_sqlSource, SqlCommandType.INSERT).build();
        configuration.addMappedStatement(insertBatchStatement);

        //save
        statementId = beanClassName + "." + "save";
        BaseDaoSaveSqlSource save_sqlSource = new BaseDaoSaveSqlSource(sqlSourceHelper, statementId);
        MappedStatement saveStatement;
        if (StringUtils.isBlank(sqlSourceHelper.getIdPropertyName())) {
            saveStatement = new MappedStatement.Builder(configuration, statementId, save_sqlSource, SqlCommandType.INSERT).build();
        } else { //有标记了@Id，添加id自增回填处理
            saveStatement = new MappedStatement.Builder(configuration, statementId, save_sqlSource, SqlCommandType.INSERT)
                    .keyGenerator(Jdbc3KeyGenerator.INSTANCE)
                    .keyProperty(ParamName.INSERT_DATA + "." + sqlSourceHelper.getIdPropertyName())
                    .build();
        }
        configuration.addMappedStatement(saveStatement);

        //saveBatch
        statementId = beanClassName + "." + "saveBatch";
        BaseDaoSaveBatchSqlSource saveBatch_sqlSource = new BaseDaoSaveBatchSqlSource(sqlSourceHelper, statementId);
        MappedStatement saveBatchStatement;
        if (StringUtils.isBlank(sqlSourceHelper.getIdPropertyName())) {
            saveBatchStatement = new MappedStatement.Builder(configuration, statementId, saveBatch_sqlSource, SqlCommandType.INSERT).build();
        } else { //有标记了@Id，添加id自增回填处理
            saveBatchStatement = new MappedStatement.Builder(configuration, statementId, saveBatch_sqlSource, SqlCommandType.INSERT)
                    .keyGenerator(Jdbc3KeyGenerator.INSTANCE).keyProperty(sqlSourceHelper.getIdPropertyName()).build();
        }
        configuration.addMappedStatement(saveBatchStatement);

        //update
        statementId = beanClassName + "." + "update";
        BaseDaoUpdateSqlSource update_sqlSource = new BaseDaoUpdateSqlSource(sqlSourceHelper);
        MappedStatement updateStatement = new MappedStatement.Builder(configuration, statementId, update_sqlSource, SqlCommandType.UPDATE).build();
        configuration.addMappedStatement(updateStatement);

        //updateById
        statementId = beanClassName + "." + "updateById";
        BaseDaoUpdateByIdSqlSource updateById_sqlSource = new BaseDaoUpdateByIdSqlSource(sqlSourceHelper);
        MappedStatement updateByIdStatement = new MappedStatement.Builder(configuration, statementId, updateById_sqlSource, SqlCommandType.UPDATE).build();
        configuration.addMappedStatement(updateByIdStatement);

        //deleteById
        statementId = beanClassName + "." + "deleteById";
        BaseDaoDeleteByIdSqlSource deleteById_sqlSource = new BaseDaoDeleteByIdSqlSource(sqlSourceHelper);
        MappedStatement deleteStatement = new MappedStatement.Builder(configuration, statementId, deleteById_sqlSource, SqlCommandType.DELETE).build();
        configuration.addMappedStatement(deleteStatement);
    }
}
