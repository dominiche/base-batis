package com.dominic.base.batis.sql.sql_source.base_dao.select;

import com.dominic.base.batis.config.BaseBatisConfig;
import com.dominic.base.batis.constant.ParamName;
import com.dominic.base.batis.sql.build.SelectParam;
import com.dominic.base.batis.sql.build.clause.WhereClause;
import com.dominic.base.batis.sql.db.DialectRouter;
import com.dominic.base.batis.sql.db.columns.ColumnInfo;
import com.dominic.base.batis.sql.sql_source.base_dao.BaseDaoSqlSourceHelper;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.session.Configuration;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Create by dominic on 2020/8/5 14:01.
 */
public class BaseDaoSelectSqlSource implements SqlSource {

    private final Configuration configuration;
    private final String tableName;

    private BaseDaoSqlSourceHelper helper;

    public BaseDaoSelectSqlSource(BaseDaoSqlSourceHelper helper) {
        this.configuration = helper.getConfiguration();
        this.tableName = helper.getTableName();

        this.helper = helper;
    }

    @Override
    public BoundSql getBoundSql(Object parameterObject) {
        Map<String, Object> map = (Map<String, Object>) parameterObject;

        SelectParam selectParam;
        if (map.containsKey(ParamName.SELECT_PARAM) && map.get(ParamName.SELECT_PARAM) != null) {
            selectParam = (SelectParam) map.get(ParamName.SELECT_PARAM);
        } else {
            selectParam = SelectParam.newInstance();
        }

        if ("*".equals(selectParam.getSelectFields())) { //把 select * , 换成具体的表字段, 根据BaseBatisConfig.columnsByDbType判断用查表信息的方式，还是用entity的映射
            Set<String> columns = BaseBatisConfig.columnsByDbType ?
                    DialectRouter.getDBDialect().getColumns(tableName).stream().map(ColumnInfo::getColumnName).collect(Collectors.toSet())
                    : helper.getColumnName2FieldMap().keySet();
            selectParam.setSelectFields(String.join(",", columns));
        }

        boolean useLike = selectParam.getWhereClause().isUseLike();
        WhereClause newClause = helper.buildWhereClause(map, useLike); //for bean 'wheres'
        newClause.add(selectParam.getWhereClause().getWhereList());
        selectParam.setWhereClause(newClause);

        Map<String, Object> additionalParameter = new HashMap<>();
        List<ParameterMapping> parameterMappings = new ArrayList<>();
        String sql = selectParam.getSql(configuration, tableName, additionalParameter, parameterMappings);

        BoundSql boundSql = new BoundSql(configuration, sql, parameterMappings, parameterObject);
        additionalParameter.forEach(boundSql::setAdditionalParameter);
        return boundSql;
    }
}
