package com.dominic.base.batis.sql.sql_source.base_dao.insert;

import com.dominic.base.batis.constant.ParamName;
import com.dominic.base.batis.sql.sql_source.base_dao.BaseDaoSqlSourceHelper;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.SqlSource;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Map;

/**
 * Create by dominic on 2020/9/3 14:16.
 */
public class BaseDaoSaveBatchSqlSource implements SqlSource {

    private final String mappedStatementId;

    private BaseDaoSqlSourceHelper helper;

    public BaseDaoSaveBatchSqlSource(BaseDaoSqlSourceHelper helper, String mappedStatementId) {
        this.mappedStatementId = mappedStatementId;

        this.helper = helper;
    }

    @Override
    public BoundSql getBoundSql(Object parameterObject) {
        Map<String, Object> map = (Map<String, Object>) parameterObject;

        Collection collection = (Collection) map.get(ParamName.COLLECTION);
        if (CollectionUtils.isEmpty(collection)) {
            throw new RuntimeException("empty insert data!!!");
        }

        helper.handleGeneratedKeys(map, mappedStatementId, "");
        return helper.getInsertBatchBoundSql(parameterObject, collection);
    }
}
