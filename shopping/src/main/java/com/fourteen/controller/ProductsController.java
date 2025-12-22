package com.fourteen.controller;

import com.fourteen.pojo.PageResult;
import com.fourteen.pojo.Product;
import com.fourteen.pojo.ProductsQueryParam;
import com.fourteen.pojo.Result;
import com.fourteen.service.ProductsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/products")
public class ProductsController {
    @Autowired
    private ProductsService productsService;

    @GetMapping
    public Result page(ProductsQueryParam productsQueryParam){
        log.info("分页查询：{}",productsQueryParam);
        PageResult<Product> pageResult=productsService.page(productsQueryParam);
        
        // 转换返回格式，符合接口文档要求
        List<Map<String, Object>> productList = new ArrayList<>();
        for (Product product : pageResult.getRows()) {
            Map<String, Object> item = new HashMap<>();
            item.put("productId", product.getProductId());
            item.put("mainImage", product.getMainImage());
            item.put("name", product.getName());
            item.put("price", product.getPrice());
            item.put("stockStatus", product.getStock()); // 接口文档要求是stockStatus
            productList.add(item);
        }
        
        Map<String, Object> data = new HashMap<>();
        data.put("list", productList);
        data.put("total", pageResult.getTotal());
        data.put("totalPage", (int) Math.ceil((double) pageResult.getTotal() / productsQueryParam.getPageSize()));
        data.put("currentPage", productsQueryParam.getPage());
        
        return Result.success(data);
    }
    
    @GetMapping("/search")
    public Result searchProducts(@RequestParam String keyword,
                                 @RequestParam(defaultValue = "1") Integer page,
                                 @RequestParam(defaultValue = "30") Integer pageSize) {
        log.info("搜索商品：keyword={}, page={}, pageSize={}", keyword, page, pageSize);
        
        // 验证关键词长度（1-30位）
        if (keyword == null || keyword.trim().isEmpty()) {
            return Result.error("搜索关键词不能为空");
        }
        if (keyword.length() > 30) {
            return Result.error("搜索关键词长度不能超过30位");
        }
        
        ProductsQueryParam param = new ProductsQueryParam();
        param.setPage(page);
        param.setPageSize(pageSize);
        param.setName(keyword.trim());
        
        PageResult<Product> pageResult = productsService.page(param);
        
        List<Map<String, Object>> productList = new ArrayList<>();
        for (Product product : pageResult.getRows()) {
            Map<String, Object> item = new HashMap<>();
            item.put("productId", product.getProductId());
            item.put("mainImage", product.getMainImage());
            item.put("name", product.getName());
            item.put("price", product.getPrice());
            item.put("stockStatus", product.getStock());
            productList.add(item);
        }
        
        Map<String, Object> data = new HashMap<>();
        data.put("list", productList);
        data.put("total", pageResult.getTotal());
        data.put("totalPage", (int) Math.ceil((double) pageResult.getTotal() / pageSize));
        data.put("currentPage", page);
        data.put("keyword", keyword);
        
        return Result.success(data);
    }

    @GetMapping("/{productId}")
    public Result getProductDetail(@PathVariable Integer productId) {
        log.info("查询商品详情：{}", productId);
        Product product = productsService.getProductById(productId);
        if (product == null) {
            return Result.error("商品不存在");
        }
        
        // 转换返回格式，符合接口文档要求
        Map<String, Object> data = new HashMap<>();
        data.put("productId", product.getProductId());
        data.put("name", product.getName());
        data.put("price", product.getPrice());
        data.put("stock", product.getStock());
        
        // 处理图片列表
        List<String> images = new ArrayList<>();
        images.add(product.getMainImage()); // 主图
        if (product.getDetailImages() != null && !product.getDetailImages().isEmpty()) {
            // 解析detail_images JSON数组或逗号分隔的字符串
            String detailImages = product.getDetailImages();
            if (detailImages.startsWith("[")) {
                // JSON数组格式，简单解析（实际应该用JSON库）
                detailImages = detailImages.replace("[", "").replace("]", "").replace("\"", "");
                String[] imageArray = detailImages.split(",");
                for (String img : imageArray) {
                    if (!img.trim().isEmpty()) {
                        images.add(img.trim());
                    }
                }
            } else {
                // 逗号分隔格式
                String[] imageArray = detailImages.split(",");
                for (String img : imageArray) {
                    if (!img.trim().isEmpty()) {
                        images.add(img.trim());
                    }
                }
            }
        }
        data.put("images", images);
        data.put("description", product.getDescription());
        
        return Result.success(data);
    }
}
