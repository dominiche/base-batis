package com.dominic.base.batis.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于entity字段上，表示忽略该字段，不对该字段做数据库表字段映射
 * 包括生成column、更新域、查询条件、更新条件等都忽略
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Ignore {
}
