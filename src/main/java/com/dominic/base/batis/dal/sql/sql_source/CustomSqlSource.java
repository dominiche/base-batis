package com.dominic.base.batis.dal.sql.sql_source;

import com.dominic.base.batis.constant.ParamName;
import org.apache.ibatis.builder.SqlSourceBuilder;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.session.Configuration;

import java.util.Map;

/**
 * Create by dominic on 2020/7/30 10:48.
 */
public class CustomSqlSource implements SqlSource {

    private final Configuration configuration;

    public CustomSqlSource(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public BoundSql getBoundSql(Object parameterObject) {
        Map<String, Object> map = (Map<String, Object>) parameterObject;
        String sql = (String) map.get(ParamName.SQL);
        Map<String, Object> paramMap = (Map<String, Object>) map.get(ParamName.PARAM_MAP);

        SqlSourceBuilder sqlSourceParser = new SqlSourceBuilder(configuration);
        Class<?> parameterType = parameterObject.getClass();
        SqlSource sqlSource = sqlSourceParser.parse(sql, parameterType, paramMap);
        BoundSql boundSql = sqlSource.getBoundSql(parameterObject);
        paramMap.forEach(boundSql::setAdditionalParameter);
        return boundSql;
    }
}
