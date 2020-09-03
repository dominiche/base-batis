package com.dominic.base.batis.dal.sql.sql_source.base_dao;

import com.dominic.base.batis.constant.ParamName;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.session.Configuration;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Create by dominic on 2020/8/7 11:01.
 */
public class BaseDaoInsertSqlSource implements SqlSource {

    private final Configuration configuration;
    private final String tableName;
    private final String mappedStatementId;

    private BaseDaoSqlSourceHelper helper;

    private boolean isConfigForSave = false;

    public BaseDaoInsertSqlSource(BaseDaoSqlSourceHelper helper, String mappedStatementId) {
        this.configuration = helper.getConfiguration();
        this.tableName = helper.getTableName();
        this.mappedStatementId = mappedStatementId;

        this.helper = helper;
    }

    @Override
    public BoundSql getBoundSql(Object parameterObject) {
        Map<String, Object> map = (Map<String, Object>) parameterObject;

        Object data = map.get(ParamName.INSERT_DATA);
        if (data == null) {
            throw new RuntimeException("empty insert data!!!");
        }
        if (map.containsKey(ParamName.KEY_PROPERTY) && !isConfigForSave) {
            helper.handleGeneratedKeys(map, mappedStatementId, ParamName.INSERT_DATA + ".");
            isConfigForSave = true;
        }

        Map<Field, Method> field2MethodMap = helper.getField2MethodMap();
        Map<String, String> property2ColumnNameMap = helper.getProperty2ColumnNameMap();
        List<ParameterMapping> parameterMappings = new ArrayList<>();
        StringBuilder builder = new StringBuilder("INSERT INTO ").append(tableName).append(" (");
        StringBuilder valueBuilder = new StringBuilder(") VALUES (");
        final boolean[] isFirst = {true};
        field2MethodMap.forEach((field, method) -> {
            Object value;
            try {
                value = method.invoke(data, (Object[]) null);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            if (value == null) {
                return;
            }

            if (isFirst[0]) {
                isFirst[0] = false;
            } else {
                builder.append(",");
                valueBuilder.append(",");
            }
            String fieldName = field.getName();
            String columnName = property2ColumnNameMap.get(fieldName);
            builder.append(columnName);
            valueBuilder.append("?");
            String property = ParamName.INSERT_DATA + "." + fieldName;
            ParameterMapping parameterMapping = new ParameterMapping.Builder(configuration, property, value.getClass()).build();
            parameterMappings.add(parameterMapping);
        });
        builder.append(valueBuilder).append(")");

        String sql = builder.toString();
        return new BoundSql(configuration, sql, parameterMappings, parameterObject);
    }
}
