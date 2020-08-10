package com.dominic.base.batis.dal.sql.sql_source.base_dao;

import com.dominic.base.batis.constant.ParamName;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.session.Configuration;

import java.util.*;

/**
 * Create by dominic on 2020/8/7 11:53.
 */
public class BaseDaoDeleteByIdSqlSource implements SqlSource {

    private final Configuration configuration;
    private final String tableName;

    private final BaseDaoSqlSourceHelper helper;

    public BaseDaoDeleteByIdSqlSource(BaseDaoSqlSourceHelper helper) {
        this.configuration = helper.getConfiguration();
        this.tableName = helper.getTableName();
        this.helper = helper;
    }

    @Override
    public BoundSql getBoundSql(Object parameterObject) {
        Map<String, Object> map = (Map<String, Object>) parameterObject;
        String idColumnName = (String) map.get(ParamName.ID_COLUMN_NAME);
        Object value = map.get(ParamName.ID_COLUMN_VALUE);
        String sql = "DELETE FROM " + tableName + " WHERE ";
        List<ParameterMapping> parameterMappings = new ArrayList<>();
        String idWhereSql = helper.buildIdWhereSql(idColumnName, value, parameterMappings);
        sql = sql + idWhereSql;
        return new BoundSql(configuration, sql, parameterMappings, parameterObject);
    }
}
