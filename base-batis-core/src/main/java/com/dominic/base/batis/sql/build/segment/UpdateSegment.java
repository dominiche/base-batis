package com.dominic.base.batis.sql.build.segment;

import lombok.Getter;
import lombok.Setter;

/**
 * Create by dominic on 2020/8/6 20:11.
 */
@Getter @Setter
public class UpdateSegment {
    private String columnName;
    private Object value;

    public UpdateSegment(String columnName, Object value) {
        this.columnName = columnName;
        this.value = value;
    }
}
