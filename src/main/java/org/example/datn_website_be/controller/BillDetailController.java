package org.example.datn_website_be.controller;

import jakarta.persistence.criteria.Predicate;
import jakarta.validation.Valid;
import org.example.datn_website_be.dto.request.BillDetailRequest;
import org.example.datn_website_be.dto.response.BillDetailResponse;
import org.example.datn_website_be.dto.response.BillDetailStatisticalProductRespone;
import org.example.datn_website_be.dto.response.Response;
import org.example.datn_website_be.model.BillDetail;
import org.example.datn_website_be.service.BillDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@RestController
@RequestMapping("/api/v1/bill-detail")
public class BillDetailController {

    @Autowired
    BillDetailService billDetailService;

    @GetMapping("/statisticsProduct")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE','CUSTOMER')")
    public ResponseEntity<?> getBillStatistics() {
        try{
            List<BillDetailStatisticalProductRespone> statistics = billDetailService.getBillStatistics();
            return ResponseEntity.ok(statistics);
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }
    @GetMapping("/list-bill-details")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE','CUSTOMER')")
    public Page<BillDetailResponse> getAllBillDetails(
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "codeBill", required = false) String codeBill,
            @RequestParam(value = "quantity", required = false) Integer quantity,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", defaultValue = "createdAt") String sortField,
            @RequestParam(value = "sortDirection", defaultValue = "DESC") String sortDirection
    ) {
        // Create the specification for filtering
        Specification<BillDetail> spec = (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

            if (status != null && !status.isEmpty()) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("status"), status));
            }

            if (codeBill != null && !codeBill.isEmpty()) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("bill").get("codeBill"), codeBill));
            }

            if (quantity != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("quantity"), quantity));
            }

            return predicate;
        };

        // Validate and set the sorting direction
        Sort.Direction direction;
        try {
            direction = Sort.Direction.fromString(sortDirection);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid sort direction");
        }

        // Validate the sort field
        List<String> validSortFields = Arrays.asList("createdAt", "status", "quantity", "codeBill"); // Add valid fields here
        if (!validSortFields.contains(sortField)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid sort field");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));

        // Call the service method to get the filtered results
        return billDetailService.getBillDetails(spec, pageable);
    }
    @PostMapping("/plusBillDetail")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public ResponseEntity<?> plusBillDetail(
            @RequestParam(value ="codeBill", required = false) String codeBill,
            @RequestParam(value ="idBillDetail", required = false) Long idBillDetail,
            @RequestParam(value ="idProduct", required = false) Long idProduct
    ){
        try {
            if (codeBill == null) {
                return ResponseEntity.badRequest().body(
                        Response.builder()
                                .status(HttpStatus.BAD_REQUEST.toString())
                                .mess("Lỗi: mã hóa đơn không được để trống!")
                                .build()
                );
            }
            if (idBillDetail == null) {
                return ResponseEntity.badRequest().body(
                        Response.builder()
                                .status(HttpStatus.BAD_REQUEST.toString())
                                .mess("Lỗi: ID hóa đơn chi tiết không được để trống!")
                                .build()
                );
            }
            if (idProduct == null) {
                return ResponseEntity.badRequest().body(
                        Response.builder()
                                .status(HttpStatus.BAD_REQUEST.toString())
                                .mess("Lỗi: ID sản phẩm không được để trống!")
                                .build()
                );
            }
            return ResponseEntity
                    .ok(billDetailService.plusBillDetail(codeBill,idBillDetail,idProduct));
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

    @PostMapping("/subtractBillDetail")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public ResponseEntity<?> subtractBillDetail(
            @RequestParam(value ="codeBill", required = false) String codeBill,
            @RequestParam(value ="idBillDetail", required = false) Long idBillDetail,
            @RequestParam(value ="idProduct", required = false) Long idProduct
    ){
        try {
            if (codeBill == null) {
                return ResponseEntity.badRequest().body(
                        Response.builder()
                                .status(HttpStatus.BAD_REQUEST.toString())
                                .mess("Lỗi: mã hóa đơn không được để trống!")
                                .build()
                );
            }
            if (idBillDetail == null) {
                return ResponseEntity.badRequest().body(
                        Response.builder()
                                .status(HttpStatus.BAD_REQUEST.toString())
                                .mess("Lỗi: ID hóa đơn chi tiết không được để trống!")
                                .build()
                );
            }
            if (idProduct == null) {
                return ResponseEntity.badRequest().body(
                        Response.builder()
                                .status(HttpStatus.BAD_REQUEST.toString())
                                .mess("Lỗi: ID sản phẩm không được để trống!")
                                .build()
                );
            }
            return ResponseEntity
                    .ok(billDetailService.subtractBillDetail(codeBill,idBillDetail,idProduct));
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

    @DeleteMapping("/deleteBillDetail")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public ResponseEntity<?> deleteBillDetail(
            @RequestParam(value ="codeBill", required = false) String codeBill,
            @RequestParam(value ="idBillDetail", required = false) Long idBillDetail,
            @RequestParam(value ="idProduct", required = false) Long idProduct
    ){
        try {
            if (codeBill == null) {
                return ResponseEntity.badRequest().body(
                        Response.builder()
                                .status(HttpStatus.BAD_REQUEST.toString())
                                .mess("Lỗi: mã hóa đơn không được để trống!")
                                .build()
                );
            }
            if (idBillDetail == null) {
                return ResponseEntity.badRequest().body(
                        Response.builder()
                                .status(HttpStatus.BAD_REQUEST.toString())
                                .mess("Lỗi: ID hóa đơn chi tiết không được để trống!")
                                .build()
                );
            }
            if (idProduct == null) {
                return ResponseEntity.badRequest().body(
                        Response.builder()
                                .status(HttpStatus.BAD_REQUEST.toString())
                                .mess("Lỗi: ID sản phẩm không được để trống!")
                                .build()
                );
            }
            billDetailService.deleteBillDetail(codeBill,idBillDetail,idProduct);
            return ResponseEntity
                    .ok("Xóa thành công!");
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
