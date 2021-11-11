package com.dominic.base.batis.sql.build.builder.select;

import com.dominic.base.batis.constant.OrderType;
import com.dominic.base.batis.sql.build.SelectParam;
import com.dominic.base.batis.sql.build.segment.OrderSegment;
import com.dominic.base.batis.sql.dto.PageInfo;

public class SelectOrder {
    private final SelectParam selectParam;

    public SelectOrder(SelectParam selectParam) {
        this.selectParam = selectParam;
    }

    public SelectOrder asc(String columnName) {
        selectParam.getOrderList().add(new OrderSegment(columnName, OrderType.ASC));
        return this;
    }
    public SelectOrder desc(String columnName) {
        selectParam.getOrderList().add(new OrderSegment(columnName, OrderType.DESC));
        return this;
    }

    public SelectBuild page(int pageIndex, int pageSize) {
        selectParam.setPageInfo(new PageInfo(pageIndex, pageSize));
        return new SelectBuild(selectParam);
    }
}
