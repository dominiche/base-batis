package com.dominic.base.batis.sql.build.builder.select;

import com.dominic.base.batis.sql.build.SelectParam;

public class SelectBuild {
    private final SelectParam selectParam;
    public SelectBuild(SelectParam selectParam) {
        this.selectParam = selectParam;
    }

    public SelectParam build() {
        return selectParam;
    }
}
