package com.dominic.base.batis.sql.build.pagination;

/**
 * Create by dominic on 2020/8/6 14:40.
 */
public class DefaultPagination implements DialectPagination {
    @Override
    public String getPaginationSql(String selectSql, long offset, long limit) {
        return selectSql + " limit " + limit + " offset " + offset;
    }
}
