package com.dominic.base.batis.dao;

import com.dominic.base.batis.entity.Product;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

@Mapper
public interface ProductDao {
    @Insert("INSERT INTO product (product_no, product_name) VALUES(#{productNo}, #{productName})")
    @Options(useGeneratedKeys=true, keyProperty = "productId")
    int save(Product t);
}
