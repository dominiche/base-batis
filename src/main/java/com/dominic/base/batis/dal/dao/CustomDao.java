package com.dominic.base.batis.dal.dao;

import com.dominic.base.batis.constant.ParamName;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Create by dominic on 2020/7/29 17:03.
 */
public interface CustomDao<T> {

    T selectOne(@Param(ParamName.SQL) String sql, @Param(ParamName.PARAM_MAP) Map<String, Object> paramMap);

    Map<String, Object> selectOneForMap(@Param(ParamName.SQL) String sql, @Param(ParamName.PARAM_MAP) Map<String, Object> paramMap);

    List<T> selectList(@Param(ParamName.SQL) String sql, @Param(ParamName.PARAM_MAP) Map<String, Object> paramMap);

    List<Map<String, Object>> selectListForMap(@Param(ParamName.SQL) String sql, @Param(ParamName.PARAM_MAP) Map<String, Object> paramMap);

    int insert(@Param(ParamName.SQL) String sql, @Param(ParamName.PARAM_MAP) Map<String, Object> paramMap);

    int update(@Param(ParamName.SQL) String sql, @Param(ParamName.PARAM_MAP) Map<String, Object> paramMap);

    int delete(@Param(ParamName.SQL) String sql, @Param(ParamName.PARAM_MAP) Map<String, Object> paramMap);
}
