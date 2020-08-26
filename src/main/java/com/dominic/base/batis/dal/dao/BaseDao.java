package com.dominic.base.batis.dal.dao;

import com.dominic.base.batis.constant.ParamName;
import com.dominic.base.batis.dal.sql.build.SelectParam;
import com.dominic.base.batis.dal.sql.build.UpdateParam;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

/**
 * Create by dominic on 2020/8/5 9:58.
 */
public interface BaseDao<T> {

    T selectOne(@Param(ParamName.WHERES) T wheres);
    T selectOne(@Param(ParamName.SELECT_PARAM) SelectParam whereParam);
    T selectOne(@Param(ParamName.WHERES) T wheres, @Param(ParamName.SELECT_PARAM) SelectParam whereParam);

    T selectById(@Param(ParamName.ID_COLUMN_NAME) String idColumnName, @Param(ParamName.ID_COLUMN_VALUE) Object value);
    List<T> selectById(@Param(ParamName.ID_COLUMN_NAME) String idColumnName, @Param(ParamName.ID_COLUMN_VALUE) Collection<Object> collection);

    List<T> selectList(@Param(ParamName.WHERES) T wheres);
    List<T> selectList(@Param(ParamName.SELECT_PARAM) SelectParam selectParam);
    List<T> selectList(@Param(ParamName.WHERES) T wheres, @Param(ParamName.SELECT_PARAM) SelectParam selectParam);

    long selectCount(@Param(ParamName.WHERES) T wheres);
    long selectCount(@Param(ParamName.SELECT_PARAM) SelectParam selectParam);
    long selectCount(@Param(ParamName.WHERES) T wheres, @Param(ParamName.SELECT_PARAM) SelectParam selectParam);

    int update(@Param(ParamName.UPDATE_PARAM) UpdateParam updateParam);
    int update(@Param(ParamName.UPDATE_DATA) T updateData, @Param(ParamName.WHERES) T wheres);
    int update(@Param(ParamName.UPDATE_DATA) T updateData, @Param(ParamName.UPDATE_PARAM) UpdateParam updateParam);
    int update(@Param(ParamName.UPDATE_DATA) T updateData, @Param(ParamName.WHERES) T wheres, @Param(ParamName.UPDATE_PARAM) UpdateParam updateParam);

    int insert(@Param(ParamName.INSERT_DATA) T data);
    int insertBatch(@Param(ParamName.INSERT_DATA) Collection<T> data);
    //save ? todo
    //saveBatch ?  todo

    int deleteById(@Param(ParamName.ID_COLUMN_NAME) String idColumnName, @Param(ParamName.ID_COLUMN_VALUE) Object value);
    int deleteById(@Param(ParamName.ID_COLUMN_NAME) String idColumnName, @Param(ParamName.ID_COLUMN_VALUE) Collection<Object> collection);

}
