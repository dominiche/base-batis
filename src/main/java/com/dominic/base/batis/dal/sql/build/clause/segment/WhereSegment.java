package com.dominic.base.batis.dal.sql.build.clause.segment;

import com.dominic.base.batis.dal.sql.build.constant.Operator;
import lombok.Getter;
import lombok.Setter;

/**
 * Create by dominic on 2020/8/5 16:27.
 */
@Getter @Setter
public class WhereSegment {
    private String columnName;
    private Operator operator;
    private Object value;

    public WhereSegment(String columnName, Object value) {
        this.columnName = columnName;
        this.value = value;
        this.operator = Operator.EQ;
    }
    public WhereSegment(String columnName, Operator operator, Object value) {
        this.columnName = columnName;
        this.operator = operator;
        this.value = value;
    }
    public WhereSegment(String columnName, String value, boolean like) {
        this.columnName = columnName;
        this.value = value;
        this.operator = like?Operator.LIKE : Operator.EQ;
    }
}
