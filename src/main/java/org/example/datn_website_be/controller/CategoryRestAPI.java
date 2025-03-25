package org.example.datn_website_be.controller;

import jakarta.validation.Valid;
import org.example.datn_website_be.dto.request.CategoryRequest;
import org.example.datn_website_be.dto.response.Response;
import org.example.datn_website_be.dto.response.CategoryResponse;
import org.example.datn_website_be.model.Category;
import org.example.datn_website_be.service.CategoryService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/category")
public class CategoryRestAPI {
    @Autowired
    CategoryService categoryService;

    @GetMapping("/list-category")
    public ResponseEntity<?> findAllCategory() {
        try {
            List<CategoryResponse> categoryResponses = categoryService.findAllCategory();
            return ResponseEntity.ok(categoryResponses);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/list-categoryActive")
    public ResponseEntity<?> findByStatusActive() {
        try {
            return ResponseEntity.ok(categoryService.findByStatus());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/list-category-search")
    public ResponseEntity<?> findByStatusSearch(@RequestParam("search") String search) {
        try {
            return ResponseEntity.ok(categoryService.findByStatus().stream()
                    .filter(CategoryResponse -> CategoryResponse.getName().toLowerCase().contains(search.trim().toLowerCase()))
                    .collect(Collectors.toList()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/update-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateStatus(
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "aBoolean", required = false) boolean aBoolean
    ) {
        try {
            if (id == null) {
                return ResponseEntity.badRequest().body(
                        Response.builder()
                                .status(HttpStatus.BAD_REQUEST.toString())
                                .mess("Lỗi: ID danh mục không được để trống!")
                                .build()
                );
            }
            Category category = categoryService.updateStatus(id, aBoolean);
            return ResponseEntity.ok(category);
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

    @PostMapping("/create-category")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createCategory(@RequestBody @Valid CategoryRequest categoryRequest, BindingResult result) {
        try {
            if (result.hasErrors()) {
                List<String> errors = result.getAllErrors().stream()
                        .map(error -> error.getDefaultMessage())
                        .collect(Collectors.toList());
                return ResponseEntity.badRequest().body(errors);
            }
            return ResponseEntity.ok(categoryService.createCategory(categoryRequest));
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
