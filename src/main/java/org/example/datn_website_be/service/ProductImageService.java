package org.example.datn_website_be.service;

import org.springframework.transaction.annotation.Transactional;
import org.example.datn_website_be.dto.request.ProductImageRequest;
import org.example.datn_website_be.dto.response.ProductImageResponse;
import org.example.datn_website_be.model.Product;
import org.example.datn_website_be.model.ProductImage;
import org.example.datn_website_be.repository.ProductImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import java.util.stream.Collectors;

@Service
public class ProductImageService {
    @Autowired
    ProductImageRepository productImageRepository;

    public List<ProductImageResponse> findAll() {
        return productImageRepository.listProductImageResponse();
    }

    public List<ProductImageResponse> findListImageByIdProductDetail(Long id) {
        return productImageRepository.findListImageByIdProduct(id);
    }
    @Transactional
    public boolean createProductImage(Product product, List<byte[]> listImage) {
        try {
            for (byte[] imageBytes : listImage){
                ProductImage productImage = ProductImage.builder()
                        .product(product)
                        .imageByte(imageBytes)
                        .build();
                productImageRepository.save(productImage);
            }
            return true;
        }catch (Exception e){
            System.out.println(e.getMessage());
            return false;
        }
    }

}
