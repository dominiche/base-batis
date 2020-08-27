package com.dominic.base.batis.dal.sql.db.dialect;

import com.dominic.base.batis.dal.dao.CustomDao;
import com.dominic.base.batis.dal.sql.db.DialectHelper;
import com.dominic.base.batis.dal.sql.db.columns.ColumnInfo;

import java.util.HashMap;
import java.util.List;

/**
 * Create by dominic on 2020/8/27 15:24.
 */
public class MySQLDialect extends AbstractDialect {
    @Override
    public List<ColumnInfo> getColumns(String tableName) {
        String sql = "select column_name from information_schema.columns where table_name=#{tableName}";
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("tableName", tableName);
        CustomDao<ColumnInfo> columnInfoCustomDao = DialectHelper.getColumnInfoCustomDao();
        return columnInfoCustomDao.selectList(sql, paramMap);
    }
}
