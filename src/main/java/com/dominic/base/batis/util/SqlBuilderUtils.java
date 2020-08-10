package com.dominic.base.batis.util;

import com.dominic.base.batis.config.BaseBatisConfig;
import com.dominic.base.batis.dal.page.PageInfo;
import com.dominic.base.batis.dal.sql.build.constant.DBType;
import com.dominic.base.batis.dal.sql.build.pagination.DB2Pagination;
import com.dominic.base.batis.dal.sql.build.pagination.DefaultPagination;
import com.dominic.base.batis.dal.sql.build.pagination.DialectPagination;
import com.dominic.base.batis.dal.sql.build.pagination.OraclePagination;

/**
 * Create by dominic on 2020/8/6 14:43.
 */
public class SqlBuilderUtils {
    private static volatile DialectPagination dialectPagination = null;

    public static String getPaginationSql(String selectSql, PageInfo pageInfo) {
        if (pageInfo == null) {
            return selectSql;
        }

        if (dialectPagination == null) {
            dialectPagination = getDefaultPagination();
        }

        long limit = pageInfo.getPageSize();
        long offset = (pageInfo.getPageIndex() - 1) * limit;
        return dialectPagination.getPaginationSql(selectSql, offset, limit);
    }

    private static DialectPagination getDefaultPagination() {
        DialectPagination pagination;
        DBType dbType = BaseBatisConfig.dbType;
        switch (dbType) {
            case MySQL:
            case MariaDB:
            case PostgreSQL:
                pagination = new DefaultPagination();
                break;
            case Oracle:
                pagination = new OraclePagination();
                break;
            case DB2:
                pagination = new DB2Pagination();
                break;
            default:
                throw new RuntimeException("The Database's Not Supported! DBType:" + dbType.getDbType());
        }
        return pagination;
    }
}