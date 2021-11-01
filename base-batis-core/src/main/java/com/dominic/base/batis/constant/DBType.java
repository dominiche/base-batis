package com.dominic.base.batis.constant;

/**
 * Create by dominic on 2020/8/5 18:24.
 */
public enum DBType {
    PostgreSQL("PostgreSQL"),
    MySQL("MySQL"),
    MariaDB("MariaDB"),
    Oracle("Oracle"),
    DB2("DB2"),
    ;

    private final String dbType;

    DBType(String dbType) {
        this.dbType = dbType;
    }

    public String getDbType() {
        return dbType;
    }
}
