package com.dominic.base.batis.config;

import com.dominic.base.batis.constant.DBType;
import lombok.Getter;
import lombok.Setter;

/**
 * Create by dominic on 2020/8/4 17:56.
 */
@Getter@Setter
public class BaseBatisConfig {
    public static boolean mapUnderscoreToCamelCase = true;
    public static DBType dbType = DBType.MySQL;
}
