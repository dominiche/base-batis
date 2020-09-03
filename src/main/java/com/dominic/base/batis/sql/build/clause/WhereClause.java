package com.dominic.base.batis.sql.build.clause;

import com.dominic.base.batis.constant.ParamName;
import com.dominic.base.batis.sql.build.clause.segment.WhereSegment;
import com.dominic.base.batis.constant.Operator;
import lombok.Getter;
import lombok.Setter;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.session.Configuration;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * Create by dominic on 2020/8/5 18:15.
 */
@Getter @Setter
public class WhereClause {
    private boolean useLike = false; //string类型的条件是否使用like
    private List<WhereSegment> whereList = new ArrayList<>();

    public void add(WhereSegment whereSegment) {
        whereList.add(whereSegment);
    }
    public void add(List<WhereSegment> whereSegments) {
        if (!CollectionUtils.isEmpty(whereSegments)) {
            whereList.addAll(whereSegments);
        }
    }
    public void useLike() {
        this.useLike = true;
    }
    public void like(String columnName, String value) {
        whereList.add(new WhereSegment(columnName, value, true));
    }
    public void in(String columnName, Collection value) {
        whereList.add(new WhereSegment(columnName, Operator.IN, value));
    }
    public void eq(String columnName, Object value) {
        whereList.add(new WhereSegment(columnName, Operator.EQ, value));
    }
    public void notEq(String columnName, Object value) {
        whereList.add(new WhereSegment(columnName, Operator.NEQ, value));
    }
    public void lessThan(String columnName, Object value) {
        whereList.add(new WhereSegment(columnName, Operator.LT, value));
    }
    public void lessEqual(String columnName, Object value) {
        whereList.add(new WhereSegment(columnName, Operator.LE, value));
    }
    public void greaterThan(String columnName, Object value) {
        whereList.add(new WhereSegment(columnName, Operator.GT, value));
    }
    public void greaterEqual(String columnName, Object value) {
        whereList.add(new WhereSegment(columnName, Operator.GE, value));
    }

    public String getSql(Configuration configuration, Map<String, Object> additionalParameter, List<ParameterMapping> parameterMappings) {
        if (CollectionUtils.isEmpty(whereList)) {
            return "";
        }

        StringBuilder builder = new StringBuilder();
        boolean isFirst = true;
        for (WhereSegment segment : whereList) {
            if (isFirst) {
                isFirst = false;
            } else {
                builder.append(" AND ");
            }
            Operator operator = segment.getOperator();
            Object value = segment.getValue();
            if (value == null) {
                continue;
            }

            String property = ParamName.WHERE_PARAM + "." + segment.getColumnName();
            if (operator == Operator.LIKE && value instanceof String) {
                builder.append(segment.getColumnName()).append(" like CONCAT(?,'%')");

                ParameterMapping parameterMapping = new ParameterMapping.Builder(configuration, property, value.getClass()).build();
                parameterMappings.add(parameterMapping);
                additionalParameter.put(property, value);
            } else if (operator == Operator.IN && value instanceof Collection) {
                Collection coll = (Collection) value;
                builder.append(segment.getColumnName()).append(" IN (");
                int index = 1;
                Iterator iterator = coll.iterator();
                while (iterator.hasNext()) {
                    Object next = iterator.next();
                    String newPropertyName = property + "_" + index;
                    ++index;
                    builder.append("?");
                    if (iterator.hasNext()) {
                        builder.append(",");
                    }

                    ParameterMapping parameterMapping = new ParameterMapping.Builder(configuration, newPropertyName, next.getClass()).build();
                    parameterMappings.add(parameterMapping);
                    additionalParameter.put(newPropertyName, next);
                }
                builder.append(")");
            } else {
                builder.append(segment.getColumnName()).append(operator.getOperator()).append("?");
                ParameterMapping parameterMapping = new ParameterMapping.Builder(configuration, property, value.getClass()).build();
                parameterMappings.add(parameterMapping);
                additionalParameter.put(property, value);
            }
        }
        return builder.toString();
    }
}
