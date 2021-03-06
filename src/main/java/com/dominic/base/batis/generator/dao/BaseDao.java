package com.dominic.base.batis.generator.dao;

import com.dominic.base.batis.constant.ParamName;
import com.dominic.base.batis.sql.build.SelectParam;
import com.dominic.base.batis.sql.build.UpdateParam;
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

    T selectById(@Param(ParamName.ID_COLUMN_VALUE) Object value);
    List<T> selectById(@Param(ParamName.ID_COLUMN_VALUE) Collection<Object> collection);
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

    int updateById(@Param(ParamName.UPDATE_DATA) T updateData, @Param(ParamName.ID_COLUMN_VALUE) Object value);
    int updateById(@Param(ParamName.UPDATE_DATA) T updateData, @Param(ParamName.ID_COLUMN_VALUE) Collection<Object> collection);
    int updateById(@Param(ParamName.UPDATE_DATA) T updateData, @Param(ParamName.ID_COLUMN_NAME) String idColumnName, @Param(ParamName.ID_COLUMN_VALUE) Object value);
    int updateById(@Param(ParamName.UPDATE_DATA) T updateData, @Param(ParamName.ID_COLUMN_NAME) String idColumnName, @Param(ParamName.ID_COLUMN_VALUE) Collection<Object> collection);

    int insert(@Param(ParamName.INSERT_DATA) T data);
    int insertBatch(@Param(ParamName.COLLECTION) Collection<T> data);
    int save(@Param(ParamName.INSERT_DATA) T data); //主键自增，回填
    int save(@Param(ParamName.INSERT_DATA) T data, @Param(ParamName.KEY_PROPERTY) String keyProperty); //显式指定回填属性名
    int saveBatch(@Param(ParamName.COLLECTION) Collection<T> data);
    int saveBatch(@Param(ParamName.COLLECTION) Collection<T> data, @Param(ParamName.KEY_PROPERTY) String keyProperty);

    int deleteById(@Param(ParamName.ID_COLUMN_VALUE) Object value);
    int deleteById(@Param(ParamName.ID_COLUMN_VALUE) Collection<Object> collection);
    int deleteById(@Param(ParamName.ID_COLUMN_NAME) String idColumnName, @Param(ParamName.ID_COLUMN_VALUE) Object value);
    int deleteById(@Param(ParamName.ID_COLUMN_NAME) String idColumnName, @Param(ParamName.ID_COLUMN_VALUE) Collection<Object> collection);

}
