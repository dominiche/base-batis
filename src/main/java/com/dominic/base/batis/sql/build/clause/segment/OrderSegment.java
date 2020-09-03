package com.dominic.base.batis.sql.build.clause.segment;

import com.dominic.base.batis.sql.build.constant.OrderType;
import lombok.Getter;
import lombok.Setter;

/**
 * Create by dominic on 2020/8/5 16:27.
 */
@Getter @Setter
public class OrderSegment {
    private String columnName;
    private OrderType orderType; //ASC、DESC

    public OrderSegment(String columnName, OrderType orderType) {
        this.columnName = columnName;
        this.orderType = orderType;
    }
}
