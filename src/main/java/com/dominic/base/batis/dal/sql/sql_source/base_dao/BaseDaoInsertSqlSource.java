package com.dominic.base.batis.dal.sql.sql_source.base_dao;

import com.dominic.base.batis.constant.ParamName;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.SqlSource;

import java.util.Map;

/**
 * Create by dominic on 2020/8/7 11:01.
 */
public class BaseDaoInsertSqlSource implements SqlSource {

    private BaseDaoSqlSourceHelper helper;

    public BaseDaoInsertSqlSource(BaseDaoSqlSourceHelper helper) {
        this.helper = helper;
    }

    @Override
    public BoundSql getBoundSql(Object parameterObject) {
        Map<String, Object> map = (Map<String, Object>) parameterObject;

        Object data = map.get(ParamName.INSERT_DATA);
        if (data == null) {
            throw new RuntimeException("empty insert data!!!");
        }

        return helper.getInsertBoundSql(parameterObject, data);
    }
}
