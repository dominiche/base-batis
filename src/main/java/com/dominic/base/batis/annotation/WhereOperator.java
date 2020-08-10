package com.dominic.base.batis.annotation;

import com.dominic.base.batis.dal.sql.build.constant.Operator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 构建where条件时的操作符，默认是"="
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface WhereOperator {
    Operator value() default Operator.EQ;
}
