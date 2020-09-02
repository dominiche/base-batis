package com.dominic.base.batis.dal.entity;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter@Setter
public class Product {
    private Long productId;
    private String productNo;
    private String productName;
    private BigDecimal price;
    private Integer state;
    private Date createTime;
    private Date updateTime;
}
