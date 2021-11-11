package com.dominic.base.batis.sql.build;

import com.dominic.base.batis.sql.build.builder.TableJoin;
import com.dominic.base.batis.sql.build.builder.TableName;
import com.dominic.base.batis.sql.build.builder.select.SelectTable;
import com.dominic.base.batis.sql.build.builder.select.SelectWhere;
import com.dominic.base.batis.sql.build.clause.WhereClause;
import com.dominic.base.batis.sql.build.segment.OrderSegment;
import com.dominic.base.batis.sql.dto.PageInfo;
import com.dominic.base.batis.util.SqlBuilderUtils;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.session.Configuration;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Create by dominic on 2020/8/5 16:15.
 */
@Getter
@Setter
public class SelectParam {

    private String selectFields = null;
    private TableName tableName = null;
    private List<TableJoin> tableJoinList;
    private WhereClause whereClause = null;
    private List<OrderSegment> orderList = null;
    private PageInfo pageInfo = null;

    private boolean selectCount = false;

    private SelectParam() {
    }

    public static SelectTable select(String selectFields) {
        SelectParam selectParam = new SelectParam();
        selectParam.setSelectFields(selectFields);
        return new SelectTable(selectParam);
    }

    public static SelectTable select() {
        SelectParam selectParam = new SelectParam();
        selectParam.setSelectFields("*");
        return new SelectTable(selectParam);
    }

    public static SelectTable selectCount() {
        SelectParam selectParam = new SelectParam();
        selectParam.setSelectFields("count(1)");
        selectParam.setSelectCount(true);
        return new SelectTable(selectParam);
    }

    public static SelectParam newInstance() {
        return new SelectParam();
    }

    public static SelectWhere where() {
        return SelectParam.select().where();
    }


    public String getSql(Configuration configuration, String tableName, Map<String, Object> additionalParameter, List<ParameterMapping> parameterMappings) {
        StringBuilder builder = new StringBuilder("SELECT ");
        builder.append(selectFields).append(" FROM ").append(this.tableName == null ? tableName : this.tableName.getSql());
        if (CollectionUtils.isNotEmpty(tableJoinList)) {
            builder.append(" ").append(tableJoinList.stream().map(TableJoin::getSql).collect(Collectors.joining(" ")));
        }
        String whereClauseSql = whereClause.getSql(configuration, additionalParameter, parameterMappings);
        if (StringUtils.isNotBlank(whereClauseSql)) {
            builder.append(" WHERE ").append(whereClauseSql);
        }
        if (CollectionUtils.isNotEmpty(orderList)) {
            boolean isFirst=true;
            builder.append(" ORDER BY ");
            for (OrderSegment orderSegment : orderList) {
                if (isFirst) {
                    isFirst = false;
                } else {
                    builder.append(",");
                }
                builder.append(orderSegment.getColumnName()).append(" ").append(orderSegment.getOrderType().getOrderType());
            }
        }

        String selectSql = builder.toString();
        if (selectCount) {
            return selectSql;
        }
        //pagination
        return SqlBuilderUtils.getPaginationSql(selectSql, pageInfo);
    }
}
