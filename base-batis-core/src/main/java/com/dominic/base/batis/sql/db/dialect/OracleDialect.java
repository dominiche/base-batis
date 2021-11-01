package com.dominic.base.batis.sql.db.dialect;

import com.dominic.base.batis.generator.dao.CustomDao;
import com.dominic.base.batis.sql.db.DialectHelper;
import com.dominic.base.batis.sql.db.columns.ColumnInfo;

import java.util.HashMap;
import java.util.List;

/**
 * Create by dominic on 2020/9/3 16:20.
 */
public class OracleDialect extends AbstractDialect {
    @Override
    public List<ColumnInfo> getColumns(String tableName) {
        String sql = "select column_name from user_tab_columns where table_name=#{tableName}";
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("tableName", tableName);
        CustomDao<ColumnInfo> columnInfoCustomDao = DialectHelper.getColumnInfoCustomDao();
        return columnInfoCustomDao.selectList(sql, paramMap);
    }

    @Override
    public String getPaginationSql(String selectSql, long offset, long limit) {
        return "SELECT * FROM ( " +
                "SELECT t.*, ROWNUM rn FROM ( " + selectSql + " ) t WHERE ROWNUM <=" + (offset + limit) +
                ") WHERE rn > " + offset;
    }
}
