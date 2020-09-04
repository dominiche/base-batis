package com.dominic.base.batis.sql.build;

import com.dominic.base.batis.sql.dto.PageInfo;
import com.dominic.base.batis.sql.build.clause.WhereClause;
import com.dominic.base.batis.sql.build.segment.OrderSegment;
import com.dominic.base.batis.constant.OrderType;
import com.dominic.base.batis.util.SqlBuilderUtils;
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
 * Create by dominic on 2020/8/5 16:15.
 */
@Getter
@Setter
public class SelectParam {

    private String selectFields = null;
    private WhereClause whereClause = null;
    private List<OrderSegment> orderList = null;
    private PageInfo pageInfo = null;

    private boolean selectCount = false;

    private SelectParam() {
    }

    public static SelectWhere select(String selectFields) {
        return new SelectWhere(selectFields);
    }

    public static SelectWhere select() {
        return new SelectWhere("*");
    }

    public static class SelectWhere {
        private String selectFields = null;
        private WhereClause whereClause = new WhereClause();
        private List<OrderSegment> orderList = new ArrayList<>();
        private PageInfo pageInfo = null;

        public SelectWhere(String selectFields) {
            this.selectFields = selectFields;
        }

        public SelectWhere useLike() {
            this.whereClause.useLike();
            return this;
        }
        public SelectWhere like(String columnName, String value) {
            this.whereClause.like(columnName, value);
            return this;
        }
        public SelectWhere in(String columnName, Collection value) {
            this.whereClause.in(columnName, value);
            return this;
        }
        public SelectWhere where(String columnName, Object value) {
            this.whereClause.eq(columnName, value);
            return this;
        }
        public SelectWhere eq(String columnName, Object value) {
            this.whereClause.eq(columnName, value);
            return this;
        }
        public SelectWhere notEq(String columnName, Object value) {
            this.whereClause.notEq(columnName, value);
            return this;
        }
        public SelectWhere lessThan(String columnName, Object value) {
            this.whereClause.lessThan(columnName, value);
            return this;
        }
        public SelectWhere lessEqual(String columnName, Object value) {
            this.whereClause.lessEqual(columnName, value);
            return this;
        }
        public SelectWhere greaterThan(String columnName, Object value) {
            this.whereClause.greaterThan(columnName, value);
            return this;
        }
        public SelectWhere greaterEqual(String columnName, Object value) {
            this.whereClause.greaterEqual(columnName, value);
            return this;
        }

        public SelectWhere order(String columnName, OrderType orderType) {
            this.orderList.add(new OrderSegment(columnName, orderType));
            return this;
        }
        public SelectWhere pageInfo(int pageIndex, int pageSize) {
            this.pageInfo = new PageInfo(pageIndex, pageSize);
            return this;
        }

        public SelectParam build() {
            SelectParam param = new SelectParam();
            param.setSelectFields(this.selectFields);
            param.setWhereClause(this.whereClause);
            param.setOrderList(this.orderList);
            param.setPageInfo(this.pageInfo);
            return param;
        }
    }

    public String getSql(Configuration configuration, String tableName, Map<String, Object> additionalParameter, List<ParameterMapping> parameterMappings) {
        StringBuilder builder = new StringBuilder("SELECT ");
        builder.append(selectFields).append(" FROM ").append(tableName);
        String whereClauseSql = whereClause.getSql(configuration, additionalParameter, parameterMappings);
        if (!StringUtils.isEmpty(whereClauseSql)) {
            builder.append(" WHERE ").append(whereClauseSql);
        }
        if (!CollectionUtils.isEmpty(orderList)) {
            boolean isFirst=true;
            builder.append(" ORDER BY ");
            for (OrderSegment orderSegment : orderList) {
                if (isFirst) {
                    isFirst = false;
                } else {
                    builder.append(",");
                }
                builder.append(orderSegment.getColumnName()).append(" ").append(orderSegment.getOrderType().getOrderType());
            }
        }

        String selectSql = builder.toString();
        if (selectCount) {
            return selectSql;
        }
        //pagination
        return SqlBuilderUtils.getPaginationSql(selectSql, pageInfo);
    }
}
