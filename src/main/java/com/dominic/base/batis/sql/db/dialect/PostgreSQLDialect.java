package com.dominic.base.batis.sql.db.dialect;

import com.dominic.base.batis.generator.dao.CustomDao;
import com.dominic.base.batis.sql.db.DialectHelper;
import com.dominic.base.batis.sql.db.columns.ColumnInfo;

import java.util.HashMap;
import java.util.List;

/**
 * Create by dominic on 2020/8/26 16:41.
 */
public class PostgreSQLDialect extends AbstractDialect {
    @Override
    public List<ColumnInfo> getColumns(String tableName) {
        String schema = "";
        if (tableName.contains(".")) {
            String[] split = tableName.split("\\.");
            schema = split[0];
            tableName = split[1];
        }
        String sql = "select column_name from information_schema.columns where table_schema=#{tableSchema} and table_name=#{tableName}";
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("tableSchema", schema);
        paramMap.put("tableName", tableName);
        CustomDao<ColumnInfo> columnInfoCustomDao = DialectHelper.getColumnInfoCustomDao();
        return columnInfoCustomDao.selectList(sql, paramMap);
    }
}
