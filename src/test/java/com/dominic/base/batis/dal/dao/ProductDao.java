package com.dominic.base.batis.dal.dao;

import com.dominic.base.batis.dal.entity.Product;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;

public interface ProductDao {
    @Insert("INSERT INTO product (product_no, product_name) VALUES(#{productNo}, #{productName})")
    @Options(useGeneratedKeys=true, keyProperty = "productId")
    int save(Product t);
}
