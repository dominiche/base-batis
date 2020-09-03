package com.dominic.base.batis.sql.build.pagination;

/**
 * Create by dominic on 2020/8/6 14:40.
 * refer: https://zhuanlan.zhihu.com/p/59286113
 */
public class OraclePagination implements DialectPagination {
    @Override
    public String getPaginationSql(String selectSql, long offset, long limit) {
        return "SELECT * FROM ( " +
                    "SELECT t.*, ROWNUM rn FROM ( " + selectSql + " ) t WHERE ROWNUM <=" + (offset + limit) +
                ") WHERE rn > " + offset;
    }
}
