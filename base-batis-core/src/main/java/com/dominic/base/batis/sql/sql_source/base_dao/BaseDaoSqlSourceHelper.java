package com.dominic.base.batis.sql.sql_source.base_dao;

import com.dominic.base.batis.annotation.Id;
import com.dominic.base.batis.config.BaseBatisConfig;
import com.dominic.base.batis.constant.ParamName;
import com.dominic.base.batis.sql.build.clause.WhereClause;
import com.dominic.base.batis.sql.build.segment.UpdateSegment;
import com.dominic.base.batis.sql.build.segment.WhereSegment;
import com.dominic.base.batis.sql.db.DialectRouter;
import com.dominic.base.batis.sql.db.columns.ColumnInfo;
import com.dominic.base.batis.sql.db.dialect.Dialect;
import com.dominic.base.batis.util.EntityUtils;
import com.dominic.base.batis.util.SqlBuilderUtils;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.executor.keygen.Jdbc3KeyGenerator;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.session.Configuration;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Create by dominic on 2020/8/6 17:01.
 */
@Getter
public class BaseDaoSqlSourceHelper {
    private final Configuration configuration;
    private final String tableName;

    private String idPropertyName = null;
    private String idColumnName = null;

    private Map<Field, Method> field2MethodMap = new HashMap<>();
    private Map<String, String> property2ColumnNameMap = new HashMap<>();
    private volatile Map<String, Field> pureColumnName2FieldMap = null;

    public BaseDaoSqlSourceHelper(Configuration configuration, String tableName, Class<?> entity, List<Field> fieldList) {
        this.configuration = configuration;
        this.tableName = tableName;

        fieldList.forEach(field -> {
            String propertyName = field.getName();
            String columnName = EntityUtils.getColumnName(field, BaseBatisConfig.mapUnderscoreToCamelCase);
            property2ColumnNameMap.put(propertyName, columnName);

            if (StringUtils.isBlank(idPropertyName)) {
                Id id = field.getAnnotation(Id.class);
                if (id != null) {
                    idPropertyName = propertyName;
                    idColumnName = columnName;
                }
            }

            String methodName = EntityUtils.getMethodName(propertyName);
            try {
                Method method = entity.getMethod(methodName, (Class<?>[]) null);
                field2MethodMap.put(field, method);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(propertyName + "没有get方法！！！", e);
            }
        });

        if (StringUtils.isEmpty(idPropertyName)) {
            LoggerFactory.getLogger(BaseDaoSqlSourceHelper.class).warn("entity中没有用@Id标记id字段，在后续使用xxxById或save操作时应当使用显式指定id字段名的重载方法！！！");
        }
    }

    public Map<String, Field> getPureColumnName2FieldMap() {
        if (pureColumnName2FieldMap == null) synchronized (this) {
            if (pureColumnName2FieldMap == null) {
                pureColumnName2FieldMap = new HashMap<>();
                Dialect dbDialect = DialectRouter.getDBDialect();
                List<ColumnInfo> columnInfoList = dbDialect.getColumns(tableName);
                Set<String> columnSet = columnInfoList.stream().map(ColumnInfo::getColumnName).collect(Collectors.toSet());
                field2MethodMap.keySet().forEach(field -> {
                    String fieldName = field.getName();
                    String columnName = property2ColumnNameMap.get(fieldName);
                    if (columnSet.contains(columnName)) {
                        pureColumnName2FieldMap.put(columnName, field);
                    }
                });
                if (CollectionUtils.isEmpty(pureColumnName2FieldMap.entrySet())) {
                    throw new RuntimeException(String.format("没有获取到%s表的字段信息!!!无法继续批量插入操作！", tableName));
                }
            }
        }
        return pureColumnName2FieldMap;
    }

    public WhereClause buildWhereClause(Map<String, Object> parameterObjectMap, boolean useLike) {
        WhereClause newClause = new WhereClause();
        newClause.setUseLike(useLike);
        if (parameterObjectMap.containsKey(ParamName.WHERES) && parameterObjectMap.get(ParamName.WHERES) != null) {
            Object wheres = parameterObjectMap.get(ParamName.WHERES);
            field2MethodMap.forEach((field, method) -> {
                Object value;
                try {
                    value = method.invoke(wheres, (Object[]) null);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                if (value == null) {
                    return;
                }

                String columnName = property2ColumnNameMap.get(field.getName());
                WhereSegment whereSegment = SqlBuilderUtils.getWhereSegment(field, value, columnName, useLike);
                newClause.add(whereSegment);
            });
        }
        return newClause;
    }

    public List<UpdateSegment> buildUpdateSegments(Map<String, Object> parameterObjectMap) {
        List<UpdateSegment> updateSegmentList = new ArrayList<>();
        if (parameterObjectMap.containsKey(ParamName.UPDATE_DATA) && parameterObjectMap.get(ParamName.UPDATE_DATA) != null) {
            Object updateData = parameterObjectMap.get(ParamName.UPDATE_DATA);
            field2MethodMap.forEach((field, method) -> {
                Object value;
                try {
                    value = method.invoke(updateData, (Object[]) null);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                if (value == null) {
                    return;
                }

                String columnName = property2ColumnNameMap.get(field.getName());
                UpdateSegment updateSegment = new UpdateSegment(columnName, value);
                updateSegmentList.add(updateSegment);
            });
        }
        return updateSegmentList;
    }

    public String buildIdWhereSql(String idColumnName, Object value, List<ParameterMapping> parameterMappings) {
        if (StringUtils.isEmpty(idColumnName)) {
            throw new RuntimeException("id字段名为空，请使用@Id在entity中标记id字段，或使用显式指定id字段名的重载方法！");
        }
        String idWhereSql;
        if (value instanceof Collection) {
            StringBuilder builder = new StringBuilder(idColumnName).append(" IN (");
            Collection coll = (Collection) value;
            int index = 0;
            Iterator iterator = coll.iterator();
            while (iterator.hasNext()) {
                Object next = iterator.next();
                String property = ParamName.ID_COLUMN_VALUE + "[" + index + "]";
                ++index;
                builder.append("?");
                if (iterator.hasNext()) {
                    builder.append(",");
                }
                ParameterMapping parameterMapping = new ParameterMapping.Builder(configuration, property, next.getClass()).build();
                parameterMappings.add(parameterMapping);
            }
            builder.append(")");
            idWhereSql = builder.toString();
        } else {
            idWhereSql = idColumnName + "=?";
            ParameterMapping parameterMapping = new ParameterMapping.Builder(configuration, ParamName.ID_COLUMN_VALUE, value.getClass()).build();
            parameterMappings.add(parameterMapping);
        }
        return idWhereSql;
    }

    public void handleGeneratedKeys(Map<String, Object> map, String mappedStatementId, String keyPropertyPrefix) {
        if (map.containsKey(ParamName.KEY_PROPERTY)) {//对save()、saveBatch()显式指定自增字段的方法, 添加自增回填处理
            String keyProperty = (String) map.get(ParamName.KEY_PROPERTY);
            if (!StringUtils.isEmpty(keyProperty)) {
                String[] keyProperties = keyProperty.split(",");
                for (int i=0; i<keyProperties.length; ++i) {
                    keyProperties[i] = keyPropertyPrefix + keyProperties[i];
                }
                MappedStatement mappedStatement = configuration.getMappedStatement(mappedStatementId);
                MetaObject metaObject = SystemMetaObject.forObject(mappedStatement);
                metaObject.setValue("keyGenerator", Jdbc3KeyGenerator.INSTANCE);
                metaObject.setValue("keyProperties", keyProperties);
            }
        }
    }

    public BoundSql getInsertBoundSql(Object parameterObject, Object data) {
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
            ParameterMapping parameterMapping = new ParameterMapping.Builder(configuration, property, field.getType()).build();
            parameterMappings.add(parameterMapping);
        });
        builder.append(valueBuilder).append(")");

        String sql = builder.toString();
        return new BoundSql(configuration, sql, parameterMappings, parameterObject);
    }

    public BoundSql getInsertBatchBoundSql(Object parameterObject, Collection collection) {
        Map<String, Field> pureColumnName2FieldMap = getPureColumnName2FieldMap();
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
