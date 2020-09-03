package com.dominic.base.batis.sql.build.pagination;

/**
 * Create by dominic on 2020/8/6 14:40.
 * refer: https://blog.csdn.net/shangboerds/article/details/4586887
 */
public class DB2Pagination implements DialectPagination {
    @Override
    public String getPaginationSql(String selectSql, long offset, long limit) {
        long startNumber = offset + 1;
        long endNumber = offset + limit;
        return "SELECT * FROM ( " +
                    "SELECT B.*, ROWNUMBER() OVER() AS RN FROM ( " + selectSql + ") AS B " +
                ") AS A WHERE A.RN BETWEEN " + startNumber + " AND "+ endNumber;
    }
}
