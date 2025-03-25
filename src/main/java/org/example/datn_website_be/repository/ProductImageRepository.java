package org.example.datn_website_be.repository;


import org.springframework.transaction.annotation.Transactional;
import org.example.datn_website_be.dto.response.ProductImageResponse;
import org.example.datn_website_be.model.Product;
import org.example.datn_website_be.model.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    @Query("""
                SELECT new org.example.datn_website_be.dto.response.ProductImageResponse(
                p.imageByte) 
            FROM ProductImage p  WHERE p.product.id = :id
                """)
    List<ProductImageResponse> findListImageByIdProduct(@Param("id") Long id);

    @Query(value = """
            SELECT new org.example.datn_website_be.dto.response.ProductImageResponse(
                p.imageByte) 
            FROM ProductImage p 
            """)
    List<ProductImageResponse> listProductImageResponse();

    @Transactional
    void deleteByProduct(Product product);
}
