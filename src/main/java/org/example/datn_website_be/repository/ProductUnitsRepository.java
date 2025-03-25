package org.example.datn_website_be.repository;

import org.example.datn_website_be.model.Product;
import org.example.datn_website_be.model.ProductUnits;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface ProductUnitsRepository extends JpaRepository<ProductUnits, Long>{
    List<ProductUnits> findByProductId(Long productId);

    List<ProductUnits> findByProductIdAndType(Long productId,boolean type);
}
