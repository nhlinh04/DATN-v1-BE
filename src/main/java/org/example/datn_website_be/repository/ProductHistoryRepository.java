package org.example.datn_website_be.repository;

import org.example.datn_website_be.model.ProductHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductHistoryRepository extends JpaRepository<ProductHistory, Long> {
    List<ProductHistory> findByProductId(Long productId);

}
