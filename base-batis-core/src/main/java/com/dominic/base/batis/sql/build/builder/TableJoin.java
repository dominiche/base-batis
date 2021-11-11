package com.dominic.base.batis.sql.build.builder;

import com.dominic.base.batis.constant.TableJoinType;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class TableJoin {
    private TableJoinType joinType;
    private final TableName tableName;
    private String joinOn; //join on 语句

    public TableJoin(TableName tableName) {
        this.joinType = TableJoinType.JOIN;
        this.tableName = tableName;
    }
    public TableJoin(TableJoinType joinType, TableName tableName) {
        this.joinType = joinType;
        this.tableName = tableName;
    }

    public String getSql() {
        return joinType.getType() + " " + tableName.getSql() + " ON " + joinOn;
    }
}
