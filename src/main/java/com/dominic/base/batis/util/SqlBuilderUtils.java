package com.dominic.base.batis.util;

import com.dominic.base.batis.annotation.WhereOperator;
import com.dominic.base.batis.constant.Operator;
import com.dominic.base.batis.sql.build.clause.segment.WhereSegment;
import com.dominic.base.batis.sql.build.pagination.DialectPagination;
import com.dominic.base.batis.sql.db.DialectRouter;
import com.dominic.base.batis.sql.db.dialect.Dialect;
import com.dominic.base.batis.sql.page.PageInfo;

import java.lang.reflect.Field;
import java.util.Collection;

/**
 * Create by dominic on 2020/8/6 14:43.
 */
public class SqlBuilderUtils {
    private static volatile DialectPagination dialectPagination = null;

    public static String getPaginationSql(String selectSql, PageInfo pageInfo) {
        if (pageInfo == null) {
            return selectSql;
        }

        Dialect dialect = DialectRouter.getDBDialect();
        long limit = pageInfo.getPageSize();
        long offset = (pageInfo.getPageIndex() - 1) * limit;
        return dialect.getPaginationSql(selectSql, offset, limit);
    }

    public static WhereSegment getWhereSegment(Field field, Object value, String columnName, boolean useLike) {
        WhereOperator annotation = field.getAnnotation(WhereOperator.class);
        if (annotation != null) {
            Operator operator = annotation.value();
            return new WhereSegment(columnName, operator, value);
        }

        if (useLike && value instanceof String) {
            return new WhereSegment(columnName, Operator.LIKE, value);
        }

        if (value instanceof Collection) {
            return new WhereSegment(columnName, Operator.IN, value);
        }

        return new WhereSegment(columnName, Operator.EQ, value);
    }
}
