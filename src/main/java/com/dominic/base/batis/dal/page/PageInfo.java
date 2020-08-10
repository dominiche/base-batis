package com.dominic.base.batis.dal.page;


import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter @Setter
public class PageInfo implements Serializable {

    private Integer pageIndex= 1; // 第几页
    private Integer pageSize = 10; // 每页记录数

    public PageInfo() {
    }

    public PageInfo(int pageIndex, int pageSize) {
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
    }

}
