package com.dominic.base.batis.dal.sql.sql_source.base_dao;

import com.dominic.base.batis.constant.ParamName;
import com.dominic.base.batis.dal.sql.build.UpdateParam;
import com.dominic.base.batis.dal.sql.build.clause.WhereClause;
import com.dominic.base.batis.dal.sql.build.clause.segment.UpdateSegment;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.session.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Create by dominic on 2020/8/6 19:01.
 */
public class BaseDaoUpdateSqlSource implements SqlSource {

    private final Configuration configuration;
    private final String tableName;

    private BaseDaoSqlSourceHelper helper;

    public BaseDaoUpdateSqlSource(BaseDaoSqlSourceHelper helper) {
        this.configuration = helper.getConfiguration();
        this.tableName = helper.getTableName();

        this.helper = helper;
    }

    @Override
    public BoundSql getBoundSql(Object parameterObject) {
        Map<String, Object> map = (Map<String, Object>) parameterObject;

        UpdateParam updateParam;
        if (map.containsKey(ParamName.UPDATE_PARAM) && map.get(ParamName.UPDATE_PARAM) != null) {
            updateParam = (UpdateParam) map.get(ParamName.UPDATE_PARAM);
        } else {
            updateParam = UpdateParam.builder().build();
        }

        List<UpdateSegment> updateSegmentList = helper.buildUpdateSegments(map);
        updateSegmentList.addAll(updateParam.getUpdateList());
        updateParam.setUpdateList(updateSegmentList);

        WhereClause newClause = helper.buildWhereClause(map, updateParam.getWhereClause().isUseLike()); //for bean 'wheres'
        newClause.add(updateParam.getWhereClause().getWhereList());
        updateParam.setWhereClause(newClause);

        Map<String, Object> additionalParameter = new HashMap<>();
        List<ParameterMapping> parameterMappings = new ArrayList<>();
        String sql = updateParam.getSql(configuration, tableName, additionalParameter, parameterMappings);

        BoundSql boundSql = new BoundSql(configuration, sql, parameterMappings, parameterObject);
        additionalParameter.forEach(boundSql::setAdditionalParameter);
        return boundSql;
    }
}
