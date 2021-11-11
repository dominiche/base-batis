package com.dominic.base.batis.sql.build.builder;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

@Getter @Setter
public class TableName {
    private final String tableName; //表名
    private String alias; //表别名

    public TableName(String tableName) {
        this.tableName = tableName;
    }
    public TableName(String tableName, String alias) {
        this.tableName = tableName;
        this.alias = alias;
    }

    public String getSql() {
        return StringUtils.isBlank(alias) ? tableName : tableName + " AS " + alias;
    }
}
