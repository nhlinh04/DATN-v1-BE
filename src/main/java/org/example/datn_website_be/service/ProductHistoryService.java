package org.example.datn_website_be.service;

import org.example.datn_website_be.model.ProductHistory;
import org.example.datn_website_be.repository.ProductHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductHistoryService {
    @Autowired
    public ProductHistoryRepository productHistoryRepository;
    public List<ProductHistory> findProductHistory(Long idProduct){
        return productHistoryRepository.findByProductId(idProduct);
    }
}
