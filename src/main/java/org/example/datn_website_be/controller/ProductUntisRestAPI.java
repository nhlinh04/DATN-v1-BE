package org.example.datn_website_be.controller;

import org.example.datn_website_be.dto.response.Response;
import org.example.datn_website_be.model.ProductUnits;
import org.example.datn_website_be.service.ProductUnitsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/productUntis")
public class ProductUntisRestAPI {
    @Autowired
    ProductUnitsService productUnitsService;
    @GetMapping("/findListProductUnitsById")
    public ResponseEntity<?> findListProductUnitsByIdByIdProduct(@RequestParam(value = "idProductUnits", required = false) Long id) {
        try {
            if (id == null) {
                return ResponseEntity.badRequest().body(
                        Response.builder()
                                .status(HttpStatus.BAD_REQUEST.toString())
                                .mess("Lỗi: ID không được để trống!")
                                .build()
                );
            }
            return ResponseEntity.ok(productUnitsService.findProductUnitsById(id));
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
    @GetMapping("/findProductUnitsById")
    public ResponseEntity<?> findProductUnitsById(@RequestParam(value = "idProductUnits", required = false) Long id) {
        try {
            if (id == null) {
                return ResponseEntity.badRequest().body(
                        Response.builder()
                                .status(HttpStatus.BAD_REQUEST.toString())
                                .mess("Lỗi: ID không được để trống!")
                                .build()
                );
            }
            return ResponseEntity.ok(productUnitsService.findByProductUnits(id));
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
    @GetMapping("/findProductResponseByIdAndType")
    public ResponseEntity<?> findProductResponseByIdAndType(
            @RequestParam(value = "idProductUnits", required = false) Long id,
            @RequestParam(value = "type", required = false) boolean type
    ) {
        try {
            if (id == null) {
                return ResponseEntity.badRequest().body(
                        Response.builder()
                                .status(HttpStatus.BAD_REQUEST.toString())
                                .mess("Lỗi: ID không được để trống!")
                                .build()
                );
            }
            return ResponseEntity.ok(productUnitsService.findProductUnitsById(id));
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
