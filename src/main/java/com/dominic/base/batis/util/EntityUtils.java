package com.dominic.base.batis.util;

import com.dominic.base.batis.annotation.AliasName;
import com.dominic.base.batis.annotation.Ignore;
import com.dominic.base.batis.annotation.WhereOperator;
import com.dominic.base.batis.dal.generator.BaseDaoGenerator;
import com.dominic.base.batis.dal.sql.build.constant.Operator;
import com.dominic.base.batis.dal.sql.build.clause.segment.WhereSegment;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.type.TypeDescription;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Create by dominic on 2020/8/4 17:26.
 */
public class EntityUtils {
    public static String baseDaoSuffix = "BaseDao$genByBaseBatis";
    public static String customDaoSuffix = "CustomDao$genByBaseBatis";

    public static String getDaoClassName(Class<?> clazz, String daoSuffix) {
        String className = clazz.getName();
        return className + daoSuffix;
    }
    public static String getDaoBeanName(Class<?> clazz, String daoSuffix) {
        String simpleName = clazz.getSimpleName();
        return simpleName.substring(0, 1).toLowerCase() + simpleName.substring(1)  + daoSuffix;
    }

    public static String getMethodName(String property) {
        return "get" + property.substring(0, 1).toUpperCase() + property.substring(1);
    }

    public static String mapCamelCaseToUnderscore(String fieldName) {
        StringBuilder builder = new StringBuilder();
        for (char c : fieldName.toCharArray()) {
            if (c >= 'A' && c <= 'Z') {
                builder.append("_").append((char) (c + 32)); //to lower case
            } else {
                builder.append(c);
            }
        }
        return builder.toString();
    }

    public static <T> Collection<Field> getAllFields(Class<T> clazz) {
        Map<String, Field> name2FieldMap = Stream.of(clazz.getDeclaredFields())
                .collect(Collectors.toMap(Field::getName, v -> v));
        //父类属性
        Class<? super T> superclass = clazz.getSuperclass();
        while (!superclass.equals(Object.class)) {
            Stream.of(superclass.getDeclaredFields()).forEach(field -> {
                String fieldName = field.getName();
                if (!name2FieldMap.containsKey(fieldName)) {
                    name2FieldMap.put(fieldName, field);
                }
            });
            superclass = superclass.getSuperclass();
        }
        return name2FieldMap.values();
    }

    public static <T> List<Field> getFields(Class<T> clazz) {
        return EntityUtils.getAllFields(clazz).stream().filter(EntityUtils::notIgnore).collect(Collectors.toList());
    }

    public static boolean notIgnore(Field field) {
        return field.getAnnotation(Ignore.class) == null
                && !Modifier.isTransient(field.getModifiers()) //过滤transient属性
                && !Modifier.isStatic(field.getModifiers()); //过滤静态属性, 如: "serialVersionUID"
    }

    public static String getColumnName(Field field, boolean isMapUnderscoreToCamelCase) {
        AliasName aliasName = field.getAnnotation(AliasName.class);
        if (null != aliasName && !StringUtils.isEmpty(aliasName.value())) {
            return aliasName.value();
        }

        String name = field.getName();
        if (isMapUnderscoreToCamelCase) {
            name = EntityUtils.mapCamelCaseToUnderscore(name);
        }
        return name;
    }

    public static WhereSegment getWhereSegment(Field field, Object value, String columnName, boolean useLike) {
        WhereOperator annotation = field.getAnnotation(WhereOperator.class);
        if (annotation != null) {
            Operator operator = annotation.value();
            return new WhereSegment(columnName, operator, value);
        }

        if (useLike && value instanceof String) {
            return new WhereSegment(columnName, Operator.LIKE, value);
        }

        return new WhereSegment(columnName, Operator.EQ, value);
    }


    public static <T> Class<?> getBeanDaoClass(String beanClassName, Class<T> entity, Class parentClass) {
        TypeDescription.Generic genericSuperClass =
                TypeDescription.Generic.Builder.parameterizedType(parentClass, entity).build();
        return new ByteBuddy()
                .makeInterface(genericSuperClass)
                .name(beanClassName)
                .make()
                .load(BaseDaoGenerator.class.getClassLoader())
                .getLoaded();
    }
}
