package com.dominic.base.batis.entity;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter @Setter
public class ProductUnionDTO extends Product {
    private String supplier;
    private BigDecimal originPrice;
}
