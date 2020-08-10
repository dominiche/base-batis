package com.dominic.base.batis.dal.sql.build.constant;

/**
 * Create by dominic on 2020/8/5 18:24.
 */
public enum Operator {
    IN("in"),
    LIKE("like"),

    EQ("="),
    NEQ("!="),
    LT("<"),
    LE("<="),
    GT(">"),
    GE(">="),
    ;


    private final String operator;

    Operator(String operator) {
        this.operator = operator;
    }

    public String getOperator() {
        return operator;
    }
}
