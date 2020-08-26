package com.dominic.base.batis.dal.sql.db;

import com.dominic.base.batis.BaseBatis;
import com.dominic.base.batis.dal.dao.CustomDao;
import com.dominic.base.batis.dal.sql.db.columns.ColumnInfo;

/**
 * Create by dominic on 2020/8/26 16:29.
 */
public class DialectHelper {

    private static CustomDao<ColumnInfo> columnInfoCustomDao = BaseBatis.getCustomDao(ColumnInfo.class);

    public static CustomDao<ColumnInfo> getColumnInfoCustomDao() {
        return columnInfoCustomDao;
    }
}
