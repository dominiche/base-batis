package com.dominic.base.batis.sql.db;

import com.dominic.base.batis.config.BaseBatisConfig;
import com.dominic.base.batis.sql.build.constant.DBType;
import com.dominic.base.batis.sql.db.dialect.Dialect;
import com.dominic.base.batis.sql.db.dialect.MySQLDialect;
import com.dominic.base.batis.sql.db.dialect.PostgreSQLDialect;

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
        switch (dbType) { //todo
            case MariaDB:
            case MySQL:
                dialect = new MySQLDialect();
                break;
            case PostgreSQL:
                dialect = new PostgreSQLDialect();
                break;
//            case Oracle:
//                dialect = ;
//                break;
//            case DB2:
//                dialect = ;
//                break;
            default:
                throw new RuntimeException("The Database's Not Supported! DBType:" + dbType.getDbType());
        }
        return dialect;
    }
}
