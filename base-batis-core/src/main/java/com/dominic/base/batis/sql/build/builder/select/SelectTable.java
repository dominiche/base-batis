package com.dominic.base.batis.sql.build.builder.select;

import com.dominic.base.batis.constant.TableJoinType;
import com.dominic.base.batis.sql.build.SelectParam;
import com.dominic.base.batis.sql.build.builder.TableJoin;
import com.dominic.base.batis.sql.build.builder.TableName;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;

public class SelectTable {
    private final SelectParam selectParam;
    public SelectTable(SelectParam selectParam) {
        this.selectParam = selectParam;
    }

    public SelectTable.SelectTableJoin from(String tableName) {
        selectParam.setTableName(new TableName(tableName));
        return new SelectTable.SelectTableJoin(selectParam);
    }

    public SelectTable.SelectTableJoin from(String tableName, String alias) {
        selectParam.setTableName(new TableName(tableName, alias));
        return new SelectTable.SelectTableJoin(selectParam);
    }

    public SelectWhere where() {
        return new SelectWhere(selectParam);
    }

    public class SelectTableJoin {
        private final SelectParam selectParam;
        public SelectTableJoin(SelectParam selectParam) {
            this.selectParam = selectParam;
        }

        public SelectTable.SelectTableJoinOn join(String joinTableName) {
            TableJoin tableJoin = new TableJoin(new TableName(joinTableName));
            return new SelectTable.SelectTableJoinOn(selectParam, tableJoin);
        }
        public SelectTable.SelectTableJoinOn join(String joinTableName, String alias) {
            TableJoin tableJoin = new TableJoin(new TableName(joinTableName, alias));
            return new SelectTable.SelectTableJoinOn(selectParam, tableJoin);
        }

        public SelectTable.SelectTableJoinOn innerJoin(String joinTableName) {
            return join(joinTableName);
        }
        public SelectTable.SelectTableJoinOn innerJoin(String joinTableName, String alias) {
            return join(joinTableName, alias);
        }

        public SelectTable.SelectTableJoinOn leftJoin(String joinTableName) {
            TableJoin tableJoin = new TableJoin(TableJoinType.LEFT_JOIN, new TableName(joinTableName));
            return new SelectTable.SelectTableJoinOn(selectParam, tableJoin);
        }
        public SelectTable.SelectTableJoinOn leftJoin(String joinTableName, String alias) {
            TableJoin tableJoin = new TableJoin(TableJoinType.LEFT_JOIN, new TableName(joinTableName, alias));
            return new SelectTable.SelectTableJoinOn(selectParam, tableJoin);
        }

        public SelectTable.SelectTableJoinOn rightJoin(String joinTableName) {
            TableJoin tableJoin = new TableJoin(TableJoinType.RIGHT_JOIN, new TableName(joinTableName));
            return new SelectTable.SelectTableJoinOn(selectParam, tableJoin);
        }
        public SelectTable.SelectTableJoinOn rightJoin(String joinTableName, String alias) {
            TableJoin tableJoin = new TableJoin(TableJoinType.RIGHT_JOIN, new TableName(joinTableName, alias));
            return new SelectTable.SelectTableJoinOn(selectParam, tableJoin);
        }


        public SelectWhere where() {
            return new SelectWhere(selectParam);
        }
    }

    public class SelectTableJoinOn {
        private final SelectParam selectParam;
        private final TableJoin tableJoin;
        public SelectTableJoinOn(SelectParam selectParam, TableJoin tableJoin) {
            this.selectParam = selectParam;
            this.tableJoin = tableJoin;
            if (CollectionUtils.isEmpty(this.selectParam.getTableJoinList())) {
                this.selectParam.setTableJoinList(new ArrayList<>());
            }
        }

        /**
         * join的连接语句，如: a.product_no=b.product_no
         * 注意：语句前面不需要写“on”, 程序会自动补上的
         */
        public SelectTable.SelectTableJoin on(String joinOn) {
            tableJoin.setJoinOn(joinOn);
            selectParam.getTableJoinList().add(tableJoin);
            return new SelectTable.SelectTableJoin(selectParam);
        }
    }
}
