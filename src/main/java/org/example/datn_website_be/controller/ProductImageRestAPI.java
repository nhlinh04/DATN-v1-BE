package org.example.datn_website_be.controller;


import org.example.datn_website_be.dto.response.ProductImageResponse;
import org.example.datn_website_be.service.ProductImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/api/v1/image")
public class ProductImageRestAPI {
    @Autowired
    ProductImageService productImageService;
    @GetMapping("/listProductImage")
    public List<ProductImageResponse> findListImageByIdProductDetail(@RequestParam(value = "idProduct", required = false) Long id) {
        if (id == null) {
            id = 0L;
        }
        return productImageService.findListImageByIdProductDetail(id);
    }
}
