package com.fourteen.mapper;

import com.fourteen.pojo.Product;
import com.fourteen.pojo.ProductsQueryParam;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ProductsMapper {
    List<Product> list(ProductsQueryParam productsQueryParam);
    
    List<Product> listAll(ProductsQueryParam productsQueryParam); // 查询所有商品（包括下架的）
    
    Product findById(Integer productId);
    
    @Update("update products set stock=#{stock} where product_id=#{productId}")
    void updateStock(Integer productId, Integer stock);
    
    @Insert("insert into products (name, category_id, price, stock, main_image, detail_images, " +
            "description, status, created_at, updated_at) " +
            "values (#{name}, #{categoryId}, #{price}, #{stock}, #{mainImage}, #{detailImages}, " +
            "#{description}, #{status}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "productId")
    void addProduct(Product product);
    
    @Update("update products set name=#{name}, category_id=#{categoryId}, price=#{price}, " +
            "stock=#{stock}, main_image=#{mainImage}, detail_images=#{detailImages}, " +
            "description=#{description}, status=#{status}, updated_at=#{updatedAt} " +
            "where product_id=#{productId}")
    void updateProduct(Product product);
    
    @Delete("delete from products where product_id=#{productId}")
    void deleteProduct(Integer productId);
    
    @Update("update products set status=#{status} where product_id=#{productId}")
    void updateProductStatus(Integer productId, Integer status);
}
