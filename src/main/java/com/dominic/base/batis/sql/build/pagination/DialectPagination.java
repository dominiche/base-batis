package com.dominic.base.batis.sql.build.pagination;

/**
 * Create by dominic on 2020/8/6 14:37.
 */
public interface DialectPagination {

    String getPaginationSql(String selectSql, long offset, long limit);
}
