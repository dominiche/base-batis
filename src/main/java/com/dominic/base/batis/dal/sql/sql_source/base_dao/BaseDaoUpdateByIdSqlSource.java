package com.dominic.base.batis.dal.sql.sql_source.base_dao;

import com.dominic.base.batis.constant.ParamName;
import com.dominic.base.batis.dal.sql.build.UpdateParam;
import com.dominic.base.batis.dal.sql.build.clause.WhereClause;
import com.dominic.base.batis.dal.sql.build.clause.segment.UpdateSegment;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.session.Configuration;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * Create by dominic on 2020/8/6 19:01.
 */
public class BaseDaoUpdateByIdSqlSource implements SqlSource {

    private final Configuration configuration;
    private final String tableName;

    private BaseDaoSqlSourceHelper helper;

    public BaseDaoUpdateByIdSqlSource(BaseDaoSqlSourceHelper helper) {
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

        //build idWhereClause
        String idColumnName = (String) map.getOrDefault(ParamName.ID_COLUMN_NAME, null);
        idColumnName = StringUtils.isEmpty(idColumnName)?helper.getIdColumnName():idColumnName;
        Object idValue = map.get(ParamName.ID_COLUMN_VALUE);
        WhereClause newClause = new WhereClause();
        if (idValue instanceof Collection) {
            newClause.in(idColumnName, (Collection) idValue);
        } else {
            newClause.eq(idColumnName, idValue);
        }
        updateParam.setWhereClause(newClause);

        Map<String, Object> additionalParameter = new HashMap<>();
        List<ParameterMapping> parameterMappings = new ArrayList<>();
        String sql = updateParam.getSql(configuration, tableName, additionalParameter, parameterMappings);

        BoundSql boundSql = new BoundSql(configuration, sql, parameterMappings, parameterObject);
        additionalParameter.forEach(boundSql::setAdditionalParameter);
        return boundSql;
    }
}
