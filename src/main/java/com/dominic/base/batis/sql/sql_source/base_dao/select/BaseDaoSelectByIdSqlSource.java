package com.dominic.base.batis.sql.sql_source.base_dao.select;

import com.dominic.base.batis.constant.ParamName;
import com.dominic.base.batis.sql.sql_source.base_dao.BaseDaoSqlSourceHelper;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.session.Configuration;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * Create by dominic on 2020/8/5 14:01.
 */
public class BaseDaoSelectByIdSqlSource implements SqlSource {

    private final Configuration configuration;
    private final String tableName;

    private final BaseDaoSqlSourceHelper helper;

    public BaseDaoSelectByIdSqlSource(BaseDaoSqlSourceHelper helper) {
        this.configuration = helper.getConfiguration();
        this.tableName = helper.getTableName();
        this.helper = helper;
    }

    @Override
    public BoundSql getBoundSql(Object parameterObject) {
        Map<String, Object> map = (Map<String, Object>) parameterObject;
        String idColumnName = (String) map.getOrDefault(ParamName.ID_COLUMN_NAME, null);
        idColumnName = StringUtils.isEmpty(idColumnName)?helper.getIdColumnName():idColumnName;
        Object value = map.get(ParamName.ID_COLUMN_VALUE);
        String sql = "SELECT * FROM " + tableName + " WHERE ";
        List<ParameterMapping> parameterMappings = new ArrayList<>();
        String idWhereSql = helper.buildIdWhereSql(idColumnName, value, parameterMappings);
        sql = sql + idWhereSql;
        return new BoundSql(configuration, sql, parameterMappings, parameterObject);
    }
}
