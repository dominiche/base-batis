package com.dominic.base.batis.dal.sql.sql_source.base_dao;

import com.dominic.base.batis.constant.ParamName;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.session.Configuration;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Create by dominic on 2020/8/26 15:20.
 */
public class BaseDaoInsertBatchSqlSource implements SqlSource {

    private final Configuration configuration;
    private final String tableName;
    private final String mappedStatementId;

    private BaseDaoSqlSourceHelper helper;

    public BaseDaoInsertBatchSqlSource(BaseDaoSqlSourceHelper helper, String mappedStatementId) {
        this.configuration = helper.getConfiguration();
        this.tableName = helper.getTableName();
        this.mappedStatementId = mappedStatementId;

        this.helper = helper;
    }

    @Override
    public BoundSql getBoundSql(Object parameterObject) {
        Map<String, Object> map = (Map<String, Object>) parameterObject;

        Collection collection = (Collection) map.get(ParamName.COLLECTION);
        if (CollectionUtils.isEmpty(collection)) {
            throw new RuntimeException("empty insert data!!!");
        }
        helper.handleGeneratedKeys(map, mappedStatementId, "");

        Map<String, Field> pureColumnName2FieldMap = helper.getPureColumnName2FieldMap();
        List<String> columnList = new ArrayList<>(pureColumnName2FieldMap.keySet());
        String columnSql = String.join(",", columnList);
        List<ParameterMapping> parameterMappings = new ArrayList<>();
        StringBuilder builder = new StringBuilder("INSERT INTO ").append(tableName)
                .append(" (").append(columnSql).append(") VALUES ");

        Iterator iterator = collection.iterator();
        int index = 0;
        while (iterator.hasNext()) {
            iterator.next();
            StringBuilder valueBuilder = new StringBuilder("(");
            int finalIndex = index;
            columnList.forEach(columnName -> {
                Field field = pureColumnName2FieldMap.get(columnName);
                String fieldName = field.getName();
                valueBuilder.append("?").append(",");
                String property = ParamName.COLLECTION + "[" + finalIndex + "]" + "." + fieldName;
                ParameterMapping parameterMapping = new ParameterMapping.Builder(configuration, property, field.getType()).build();
                parameterMappings.add(parameterMapping);
            });
            builder.append(valueBuilder, 0, valueBuilder.length() - 1).append(")");
            if (iterator.hasNext()) {
                builder.append(",");
            }
            ++index;
        }

        String sql = builder.toString();
        return new BoundSql(configuration, sql, parameterMappings, parameterObject);
    }
}
