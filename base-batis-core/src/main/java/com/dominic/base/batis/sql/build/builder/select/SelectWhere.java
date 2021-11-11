package com.dominic.base.batis.sql.build.builder.select;

import com.dominic.base.batis.constant.OrderType;
import com.dominic.base.batis.sql.build.SelectParam;
import com.dominic.base.batis.sql.build.clause.WhereClause;
import com.dominic.base.batis.sql.build.segment.OrderSegment;
import com.dominic.base.batis.sql.dto.PageInfo;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;

public class SelectWhere {
    private final SelectParam selectParam;
    public SelectWhere(SelectParam selectParam) {
        this.selectParam = selectParam;
        selectParam.setWhereClause(new WhereClause());
    }

    public SelectWhere useLike() {
        selectParam.getWhereClause().useLike();
        return this;
    }
    public SelectWhere like(String columnName, String value) {
        selectParam.getWhereClause().like(columnName, value);
        return this;
    }
    public <T> SelectWhere in(String columnName, Collection<T> value) {
        selectParam.getWhereClause().in(columnName, value);
        return this;
    }
    public SelectWhere eq(String columnName, Object value) {
        selectParam.getWhereClause().eq(columnName, value);
        return this;
    }
    public SelectWhere notEq(String columnName, Object value) {
        selectParam.getWhereClause().notEq(columnName, value);
        return this;
    }
    public SelectWhere lessThan(String columnName, Object value) {
        selectParam.getWhereClause().lessThan(columnName, value);
        return this;
    }
    public SelectWhere lessEqual(String columnName, Object value) {
        selectParam.getWhereClause().lessEqual(columnName, value);
        return this;
    }
    public SelectWhere greaterThan(String columnName, Object value) {
        selectParam.getWhereClause().greaterThan(columnName, value);
        return this;
    }
    public SelectWhere greaterEqual(String columnName, Object value) {
        selectParam.getWhereClause().greaterEqual(columnName, value);
        return this;
    }

    public SelectOrder order(String columnName, OrderType orderType) {
        if (CollectionUtils.isEmpty(selectParam.getOrderList())) {
            selectParam.setOrderList(new ArrayList<>());
        }
        selectParam.getOrderList().add(new OrderSegment(columnName, orderType));
        return new SelectOrder(selectParam);
    }
    public SelectOrder order() {
        if (CollectionUtils.isEmpty(selectParam.getOrderList())) {
            selectParam.setOrderList(new ArrayList<>());
        }
        return new SelectOrder(selectParam);
    }

    public SelectBuild page(int pageIndex, int pageSize) {
        selectParam.setPageInfo(new PageInfo(pageIndex, pageSize));
        return new SelectBuild(selectParam);
    }

    public SelectParam build() {
        return selectParam;
    }
}
