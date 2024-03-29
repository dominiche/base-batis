package com.dominic.base.batis.config;

import com.dominic.base.batis.constant.DBType;
import org.apache.ibatis.session.SqlSessionFactory;

/**
 * Create by dominic on 2020/8/4 17:56.
 */
public class BaseBatisConfig {
    /**
     * mybatis的sqlSessionFactory实例
     * 这里只是默认的数据源配置，（多数据源支持）可以在DAO生成入口处配置
     * 注意：sqlSessionFactory、sqlSessionFactoryName必须要有一个
     */
    public static SqlSessionFactory sqlSessionFactory = null;

    /**
     * mybatis的sqlSessionFactory实例名，仅在spring环境下支持
     */
    public static String sqlSessionFactoryName = null;

    /**
     * 是否开启下划线和驼峰转换, 默认开启
     */
    public static Boolean mapUnderscoreToCamelCase = Boolean.TRUE;

    /**
     * 数据库类型，只影响使用SelectParam做分页的情况(不同数据库类型分页方式不一样)
     * 默认是MySQL，如果分页支持 “limit #{size} offset #{offset}” 这样的方式，可以不用更改数据库类型。
     */
    public static DBType dbType = DBType.MySQL;

    /**
     * ”select *“ 或 批量插入时，是否通过dbType获取准确的表columns。默认是通过entity的field做表字段的映射
     * true: 通过数据库表获取准确的columns(该方式支持的数据库类型有限)，@see {Dialect#getColumns(java.lang.String)}，{BaseDaoSqlSourceHelper#getPureColumnName2FieldMap()}
     * false: 仅通过entity的field做表字段的映射。多余的字段请用@Ignore标注，否则批量插入时可能报表中没有该字段的错误。
     */
    public static Boolean columnsByDbType = Boolean.FALSE;
    /**
     * 是否使用”select *“。默认是不使用
     * true: 使用"select *"来查询所有字段
     * false: 不使用"select *"来查询所有字段, 将根据columnsByDbType来判断使用那种方式填充具体表字段。
     */
    public static Boolean userSelectStar = Boolean.FALSE;



    public static void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        BaseBatisConfig.sqlSessionFactory = sqlSessionFactory;
    }
    public static void setSqlSessionFactoryName(String sqlSessionFactoryName) {
        BaseBatisConfig.sqlSessionFactoryName = sqlSessionFactoryName;
    }
    public static void setMapUnderscoreToCamelCase(boolean mapUnderscoreToCamelCase) {
        BaseBatisConfig.mapUnderscoreToCamelCase = mapUnderscoreToCamelCase;
    }
    public static void setDbType(DBType dbType) {
        BaseBatisConfig.dbType = dbType;
    }
    public static void setColumnsByDbType(boolean columnsByDbType) {
        BaseBatisConfig.columnsByDbType = columnsByDbType;
    }
    public static void setUserSelectStar(boolean userSelectStar) {
        BaseBatisConfig.userSelectStar = userSelectStar;
    }
}
