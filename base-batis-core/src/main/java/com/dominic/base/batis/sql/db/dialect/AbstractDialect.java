package com.dominic.base.batis.sql.db.dialect;

import com.dominic.base.batis.sql.db.columns.ColumnInfo;

import java.util.List;

/**
 * Create by dominic on 2020/8/26 16:14.
 */
public abstract class AbstractDialect implements Dialect {

    @Override
    public abstract List<ColumnInfo> getColumns(String tableName);

    @Override
    public String getPaginationSql(String selectSql, long offset, long limit) {
        return selectSql + " limit " + limit + " offset " + offset;
    }
}
