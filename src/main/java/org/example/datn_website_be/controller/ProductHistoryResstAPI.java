package org.example.datn_website_be.controller;

import org.example.datn_website_be.dto.response.Response;
import org.example.datn_website_be.model.ProductHistory;
import org.example.datn_website_be.service.ProductHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/productHistory")
public class ProductHistoryResstAPI {
    @Autowired
    ProductHistoryService productHistoryService;
    @GetMapping("/findProductHistory")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> findProductHistory(@RequestParam(value = "idProduct", required = false) Long id) {
        try {
            if (id == null) {
                return ResponseEntity.badRequest().body(
                        Response.builder()
                                .status(HttpStatus.BAD_REQUEST.toString())
                                .mess("Lỗi: ID sản phẩm không được để trống!")
                                .build()
                );
            }
            return ResponseEntity.ok(productHistoryService.findProductHistory(id));
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Response.builder()
                            .status(HttpStatus.CONFLICT.toString())
                            .mess(e.getMessage())
                            .build()
                    );
        }
    }
}
