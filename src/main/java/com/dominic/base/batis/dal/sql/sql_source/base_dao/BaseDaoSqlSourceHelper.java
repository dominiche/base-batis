package com.dominic.base.batis.dal.sql.sql_source.base_dao;

import com.dominic.base.batis.config.BaseBatisConfig;
import com.dominic.base.batis.constant.ParamName;
import com.dominic.base.batis.dal.sql.build.clause.WhereClause;
import com.dominic.base.batis.dal.sql.build.clause.segment.UpdateSegment;
import com.dominic.base.batis.dal.sql.build.clause.segment.WhereSegment;
import com.dominic.base.batis.dal.sql.db.DialectRouter;
import com.dominic.base.batis.dal.sql.db.columns.ColumnInfo;
import com.dominic.base.batis.dal.sql.db.dialect.Dialect;
import com.dominic.base.batis.util.EntityUtils;
import com.dominic.base.batis.util.SqlBuilderUtils;
import lombok.Getter;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.session.Configuration;

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

            String methodName = EntityUtils.getMethodName(propertyName);
            try {
                Method method = entity.getMethod(methodName, (Class<?>[]) null);
                field2MethodMap.put(field, method);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(propertyName + "没有get方法！！！", e);
            }
        });
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
}
