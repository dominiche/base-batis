package com.dominic.base.batis.sql.build;

import com.dominic.base.batis.constant.ParamName;
import com.dominic.base.batis.sql.build.clause.WhereClause;
import com.dominic.base.batis.sql.build.clause.segment.UpdateSegment;
import lombok.Getter;
import lombok.Setter;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.session.Configuration;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Create by dominic on 2020/8/6 20:12.
 */
@Getter
@Setter
public class UpdateParam {

    private List<UpdateSegment> updateList = null;
    private WhereClause whereClause = null;

    private UpdateParam() {
    }


    public static UpdateParamBuilder builder() {
        return new UpdateParamBuilder();
    }

    public static class UpdateParamBuilder {
        private WhereClause whereClause = new WhereClause();
        private List<UpdateSegment> updateList = new ArrayList<>();

        public UpdateParamBuilder like(String columnName, String value) {
            this.whereClause.like(columnName, value);
            return this;
        }
        public UpdateParamBuilder in(String columnName, Collection value) {
            this.whereClause.in(columnName, value);
            return this;
        }
        public UpdateParamBuilder where(String columnName, Object value) {
            this.whereClause.eq(columnName, value);
            return this;
        }
        public UpdateParamBuilder eq(String columnName, Object value) {
            this.whereClause.eq(columnName, value);
            return this;
        }
        public UpdateParamBuilder notEq(String columnName, Object value) {
            this.whereClause.notEq(columnName, value);
            return this;
        }
        public UpdateParamBuilder lessThan(String columnName, Object value) {
            this.whereClause.lessThan(columnName, value);
            return this;
        }
        public UpdateParamBuilder lessEqual(String columnName, Object value) {
            this.whereClause.lessEqual(columnName, value);
            return this;
        }
        public UpdateParamBuilder greaterThan(String columnName, Object value) {
            this.whereClause.greaterThan(columnName, value);
            return this;
        }
        public UpdateParamBuilder greaterEqual(String columnName, Object value) {
            this.whereClause.greaterEqual(columnName, value);
            return this;
        }


        public UpdateParamBuilder set(String columnName, Object value) {
            this.updateList.add(new UpdateSegment(columnName, value));
            return this;
        }
        public UpdateParamBuilder update(String columnName, Object value) {
            this.updateList.add(new UpdateSegment(columnName, value));
            return this;
        }

        public UpdateParam build() {
            UpdateParam param = new UpdateParam();
            param.setWhereClause(this.whereClause);
            param.setUpdateList(this.updateList);
            return param;
        }
    }

    public String getSql(Configuration configuration, String tableName, Map<String, Object> additionalParameter, List<ParameterMapping> parameterMappings) {
        StringBuilder builder = new StringBuilder("UPDATE ");
        builder.append(tableName).append(" SET ");

        if (CollectionUtils.isEmpty(updateList)) {
            throw new RuntimeException("update field is empty!!!");
        }

        boolean isFirst = true;
        for (UpdateSegment segment : updateList) {
            if (isFirst) {
                isFirst = false;
            } else {
                builder.append(",");
            }
            String columnName = segment.getColumnName();
            Object value = segment.getValue();
            if (value == null) {
                builder.append(columnName).append("=NULL");
                continue;
            }

            builder.append(columnName).append("=?");
            String property = ParamName.WHERE_PARAM + "." + columnName;
            ParameterMapping parameterMapping = new ParameterMapping.Builder(configuration, property, value.getClass()).build();
            parameterMappings.add(parameterMapping);
            additionalParameter.put(property, value);
        }


        String whereClauseSql = whereClause.getSql(configuration, additionalParameter, parameterMappings);
        if (StringUtils.isEmpty(whereClauseSql)) {
            throw new RuntimeException("not support empty where when update!!!");
        }
        builder.append(" WHERE ").append(whereClauseSql);


        return builder.toString();
    }
}
