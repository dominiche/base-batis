package com.dominic.base.batis.dal.sql.build.constant;

/**
 * Create by dominic on 2020/8/6 11:55.
 */
public enum OrderType {
    ASC("ASC"),
    DESC("DESC"),
    ;


    private final String orderType;

    OrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getOrderType() {
        return orderType;
    }
}
