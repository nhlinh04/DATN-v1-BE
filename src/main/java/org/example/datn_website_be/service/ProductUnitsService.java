package org.example.datn_website_be.service;

import org.example.datn_website_be.Enum.Status;
import org.example.datn_website_be.dto.request.ProductUnitsRequest;
import org.example.datn_website_be.model.Product;
import org.example.datn_website_be.model.ProductUnits;
import org.example.datn_website_be.repository.ProductUnitsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductUnitsService {
    @Autowired
    ProductUnitsRepository productUnitsRepository;
    @Transactional
    public boolean createProductUnits(Product product, List<ProductUnitsRequest> productUnitRequests) {
        for (ProductUnitsRequest request : productUnitRequests) {
            ProductUnits productUnits = ProductUnits.builder()
                    .unitName(request.getUnitName())
                    .conversionFactor(request.getConversionFactor())
                    .product(product)
                    .type(request.isType())
                    .build();
            productUnits.setStatus(Status.ACTIVE.toString());
            productUnitsRepository.save(productUnits);
        }
        return true;
    }
    @Transactional
    public boolean updateProductUnits(Product product, List<ProductUnitsRequest> productUnitRequests) {
        for (ProductUnitsRequest request : productUnitRequests) {
            ProductUnits productUnits;
            if(request.getId() == null){
                productUnits = ProductUnits.builder()
                    .unitName(request.getUnitName())
                    .conversionFactor(request.getConversionFactor())
                    .product(product)
                    .type(request.isType())
                    .build();
            productUnits.setStatus(Status.ACTIVE.toString());
            }else{
                productUnits = productUnitsRepository.findById(request.getId()).get();
                productUnits.setUnitName(request.getUnitName());
                productUnits.setConversionFactor(request.getConversionFactor());
                productUnits.setType(request.isType());
            }
            productUnitsRepository.save(productUnits);
        }
        return true;
    }
    @Transactional
    public boolean deleteProductUnits(List<Long> idProductUnits) {
        productUnitsRepository.deleteAllById(idProductUnits);
        return true;
    }

    public List<ProductUnits> findProductUnitsById(Long id) {
        List<ProductUnits> list = productUnitsRepository.findByProductId(id);
        return list;
    }
    public List<ProductUnits> findByProductIdAndType(Long id,boolean type) {
        List<ProductUnits> list = productUnitsRepository.findByProductIdAndType(id, type);
        return list;
    }
    public ProductUnits findByProductUnits(Long id) {
        ProductUnits productUnits = productUnitsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài nguyên sản phẩm trong hệ thống!"));;
        return productUnits;
    }
}
