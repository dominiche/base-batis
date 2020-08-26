package com.dominic.base.batis.dal.sql.db;

import com.dominic.base.batis.config.BaseBatisConfig;
import com.dominic.base.batis.dal.sql.build.constant.DBType;
import com.dominic.base.batis.dal.sql.db.dialect.Dialect;
import com.dominic.base.batis.dal.sql.db.dialect.PostgreSQLDialect;

/**
 * Create by dominic on 2020/8/26 17:24.
 */
public class DialectRouter {

    public static Dialect getDBDialect() {
        Dialect dialect;
        DBType dbType = BaseBatisConfig.dbType;
        switch (dbType) { //todo
//            case MySQL:
//            case MariaDB:
            case PostgreSQL:
                dialect = new PostgreSQLDialect();
                break;
//            case Oracle:
//                dialect = new OraclePagination();
//                break;
//            case DB2:
//                dialect = new DB2Pagination();
//                break;
            default:
                throw new RuntimeException("The Database's Not Supported! DBType:" + dbType.getDbType());
        }
        return dialect;
    }
}
