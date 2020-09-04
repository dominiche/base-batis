package com.dominic.base.batis.sql.db.dialect;

import com.dominic.base.batis.generator.dao.CustomDao;
import com.dominic.base.batis.sql.db.DialectHelper;
import com.dominic.base.batis.sql.db.columns.ColumnInfo;

import java.util.HashMap;
import java.util.List;

/**
 * Create by dominic on 2020/9/4 11:16.
 */
public class DB2Dialect extends AbstractDialect {
    @Override
    public List<ColumnInfo> getColumns(String tableName) {
        String schema = "";
        if (tableName.contains(".")) {
            String[] split = tableName.split("\\.");
            schema = split[0];
            tableName = split[1];
        }
        String sql = "SELECT ColName as column_name FROM syscat.COLUMNS " +
                "WHERE tabschema=upper(#{tableSchema}) and tabname=upper(#{tableName})";
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("tableSchema", schema);
        paramMap.put("tableName", tableName);
        CustomDao<ColumnInfo> columnInfoCustomDao = DialectHelper.getColumnInfoCustomDao();
        return columnInfoCustomDao.selectList(sql, paramMap);
    }
}
