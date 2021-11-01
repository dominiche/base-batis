package com.dominic.base.batis.sql.sql_source.base_dao.insert;

import com.dominic.base.batis.constant.ParamName;
import com.dominic.base.batis.sql.sql_source.base_dao.BaseDaoSqlSourceHelper;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.SqlSource;

import java.util.Map;

/**
 * Create by dominic on 2020/9/3 14:20.
 */
public class BaseDaoSaveSqlSource implements SqlSource {

    private final String mappedStatementId;

    private BaseDaoSqlSourceHelper helper;

    public BaseDaoSaveSqlSource(BaseDaoSqlSourceHelper helper, String mappedStatementId) {
        this.mappedStatementId = mappedStatementId;
        this.helper = helper;
    }

    @Override
    public BoundSql getBoundSql(Object parameterObject) {
        Map<String, Object> map = (Map<String, Object>) parameterObject;
        Object data = map.get(ParamName.INSERT_DATA);
        if (data == null) {
            throw new RuntimeException("empty insert data!!!");
        }
        helper.handleGeneratedKeys(map, mappedStatementId, ParamName.INSERT_DATA + ".");
        return helper.getInsertBoundSql(parameterObject, data);
    }
}
