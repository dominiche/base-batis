package com.dominic.base.batis.sql.db;

import com.dominic.base.batis.config.BaseBatisConfig;
import com.dominic.base.batis.constant.DBType;
import com.dominic.base.batis.sql.db.dialect.*;

/**
 * Create by dominic on 2020/8/26 17:24.
 */
public class DialectRouter {
    private static Dialect dialect = null;

    public static Dialect getDBDialect() {
        if (dialect != null) {
            return dialect;
        }
        DBType dbType = BaseBatisConfig.dbType;
        switch (dbType) {
            case MariaDB:
            case MySQL: dialect = new MySQLDialect(); break;
            case PostgreSQL: dialect = new PostgreSQLDialect(); break;
            case Oracle: dialect = new OracleDialect(); break;
            case DB2: dialect = new DB2Dialect(); break;
            default: throw new RuntimeException(dbType.getDbType() + " is Not Supported !");
        }
        return dialect;
    }
}
