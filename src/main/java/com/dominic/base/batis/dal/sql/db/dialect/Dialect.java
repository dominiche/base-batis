package com.dominic.base.batis.dal.sql.db.dialect;

import com.dominic.base.batis.dal.sql.db.columns.ColumnInfo;

import java.util.List;

/**
 * Create by dominic on 2020/8/26 15:40.
 */
public interface Dialect {

    List<ColumnInfo> getColumns(String tableName);
}
