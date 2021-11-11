package com.dominic.base.batis.constant;

/**
 * Create by dominic on 2021/11/4 15:16.
 */
public enum TableJoinType {
    LEFT_JOIN("LEFT JOIN"),
    RIGHT_JOIN("RIGHT JOIN"),
    JOIN("JOIN"), //inner join
    ;


    private final String type;

    TableJoinType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
