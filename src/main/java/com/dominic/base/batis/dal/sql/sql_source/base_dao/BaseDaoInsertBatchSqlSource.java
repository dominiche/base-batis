package com.dominic.base.batis.dal.sql.sql_source.base_dao;

import com.dominic.base.batis.constant.ParamName;
import com.dominic.base.batis.dal.sql.db.DialectRouter;
import com.dominic.base.batis.dal.sql.db.columns.ColumnInfo;
import com.dominic.base.batis.dal.sql.db.dialect.Dialect;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.session.Configuration;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Create by dominic on 2020/8/26 15:20.
 */
public class BaseDaoInsertBatchSqlSource implements SqlSource {

    private final Configuration configuration;
    private final String tableName;

    private BaseDaoSqlSourceHelper helper;

    private volatile Map<String, String> pureProperty2ColumnNameMap = null;

    public BaseDaoInsertBatchSqlSource(BaseDaoSqlSourceHelper helper) {
        this.configuration = helper.getConfiguration();
        this.tableName = helper.getTableName();

        this.helper = helper;
    }

    @Override
    public BoundSql getBoundSql(Object parameterObject) {
        Map<String, Object> map = (Map<String, Object>) parameterObject;

        Collection collection = (Collection)map.get(ParamName.INSERT_DATA);
        if (CollectionUtils.isEmpty(collection)) {
            throw new RuntimeException("empty insert data!!!");
        }

        Collection<String> columns = getPureProperty2ColumnNameMap().values();
        String columnSql = String.join(",", columns);
        List<ParameterMapping> parameterMappings = new ArrayList<>();
        StringBuilder builder = new StringBuilder("INSERT INTO ").append(tableName)
                .append(" (").append(columnSql).append(") VALUES ");

        Map<Field, Method> field2MethodMap = helper.getField2MethodMap();

        Iterator iterator = collection.iterator();
        int index = 0;
        while (iterator.hasNext()) {
            Object data = iterator.next();
            StringBuilder valueBuilder = new StringBuilder("(");
            int finalIndex = index;
            field2MethodMap.forEach((field, method) -> {
                String fieldName = field.getName();
                if (!pureProperty2ColumnNameMap.containsKey(fieldName)) {
                    return;
                }

                Object value;
                try {
                    value = method.invoke(data, (Object[]) null);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                valueBuilder.append("?").append(",");
                String property = ParamName.INSERT_DATA + "[" + finalIndex + "]" + "." + fieldName;
                ParameterMapping parameterMapping = new ParameterMapping.Builder(configuration, property, value.getClass()).build();
                parameterMappings.add(parameterMapping);
            });
            valueBuilder.deleteCharAt(valueBuilder.length() - 1).append(")");
            builder.append(valueBuilder.toString());
            if (iterator.hasNext()) {
                builder.append(",");
            }
            ++index;
        }

        String sql = builder.toString();
        return new BoundSql(configuration, sql, parameterMappings, parameterObject);
    }

    public Map<String, String> getPureProperty2ColumnNameMap() {
        if (pureProperty2ColumnNameMap == null) {
            synchronized (this) {
                if (pureProperty2ColumnNameMap == null) {
                    pureProperty2ColumnNameMap = new HashMap<>();
                    Dialect dbDialect = DialectRouter.getDBDialect();
                    List<ColumnInfo> columnInfoList = dbDialect.getColumns(tableName);
                    if (!CollectionUtils.isEmpty(columnInfoList)) {
                        Set<String> columnSet = columnInfoList.stream().map(ColumnInfo::getColumnName).collect(Collectors.toSet());
                        Map<String, String> property2ColumnNameMap = helper.getProperty2ColumnNameMap();
                        property2ColumnNameMap.forEach((key, value) -> {
                            if (columnSet.contains(value)) {
                                pureProperty2ColumnNameMap.put(key, value);
                            }
                        });
                    }
                }
            }
        }
        return pureProperty2ColumnNameMap;
    }
}
