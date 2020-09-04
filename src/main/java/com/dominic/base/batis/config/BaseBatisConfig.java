package com.dominic.base.batis.config;

import com.dominic.base.batis.constant.DBType;

/**
 * Create by dominic on 2020/8/4 17:56.
 */
public class BaseBatisConfig {
    public static boolean mapUnderscoreToCamelCase = true;
    public static DBType dbType = DBType.MySQL;


    public static boolean isMapUnderscoreToCamelCase() {
        return mapUnderscoreToCamelCase;
    }
    public static void setMapUnderscoreToCamelCase(boolean mapUnderscoreToCamelCase) {
        BaseBatisConfig.mapUnderscoreToCamelCase = mapUnderscoreToCamelCase;
    }
    public static DBType getDbType() {
        return dbType;
    }
    public static void setDbType(DBType dbType) {
        BaseBatisConfig.dbType = dbType;
    }
}
