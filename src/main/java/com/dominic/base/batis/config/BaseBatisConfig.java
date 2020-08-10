package com.dominic.base.batis.config;

import com.dominic.base.batis.dal.sql.build.constant.DBType;

/**
 * Create by dominic on 2020/8/4 17:56.
 */
public class BaseBatisConfig {
    public static boolean mapUnderscoreToCamelCase = true;
    public static DBType dbType = DBType.PostgreSQL;
}
