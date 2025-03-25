package org.example.datn_website_be.controller;

import jakarta.validation.Valid;
import org.example.datn_website_be.dto.request.ActualQuantity.ActualQuantityRequest;
import org.example.datn_website_be.dto.request.ProductDetailPromoRequest;
import org.example.datn_website_be.dto.response.BillDetailOrderResponse;
import org.example.datn_website_be.dto.response.Response;
import org.example.datn_website_be.service.BillDetailByEmployeeService;
import org.example.datn_website_be.service.BillDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/billDetailByEmployee")
public class BillDetailRestAPI {
    @Autowired
     BillDetailService billDetailService;
    @Autowired
     BillDetailByEmployeeService billDetailByEmployeeService;
    @PostMapping("/createBillDetailByEmployee")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public ResponseEntity<?> createBillDetailByEmployee(
            @RequestParam(value ="codeBill", required = false) String codeBill,
            @RequestBody @Valid List<ProductDetailPromoRequest> productDetail, BindingResult result
    ){
        try {
            if (codeBill == null) {
                return ResponseEntity.badRequest().body(
                        Response.builder()
                                .status(HttpStatus.BAD_REQUEST.toString())
                                .mess("Lỗi: ID hóa đơn không được để trống!")
                                .build()
                );
            }
            if (result.hasErrors()) {
                List<String> errors = result.getAllErrors().stream()
                        .map(error -> error.getDefaultMessage())
                        .collect(Collectors.toList());
                return ResponseEntity.badRequest().body(errors);
            }
            billDetailService.createBillDetailByIdBill(codeBill,productDetail);
            return ResponseEntity
                    .ok("Thêm sản phẩm vào giỏ hàng thành công!");
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



    @PostMapping("/updateBillAndCreateBillDetailByIdBill")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public ResponseEntity<?> updateBillAndCreateBillDetailByIdBill(
            @RequestParam(value ="codeBill", required = false) String codeBill,
            @RequestBody @Valid List<ProductDetailPromoRequest> productDetail, BindingResult result
    ){
        try {
            if (codeBill == null) {
                return ResponseEntity.badRequest().body(
                        Response.builder()
                                .status(HttpStatus.BAD_REQUEST.toString())
                                .mess("Lỗi: ID hóa đơn không được để trống!")
                                .build()
                );
            }
            if (result.hasErrors()) {
                List<String> errors = result.getAllErrors().stream()
                        .map(error -> error.getDefaultMessage())
                        .collect(Collectors.toList());
                return ResponseEntity.badRequest().body(errors);
            }
            billDetailService.updateBillAndCreateBillDetailByIdBill(codeBill,productDetail);
            return ResponseEntity
                    .ok("Thêm sản phẩm thành công!");
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
    @PostMapping("/updateActualQuantity")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public ResponseEntity<?> updateActualQuantity(
            @RequestBody @Valid List<ActualQuantityRequest> actualQuantityRequests, BindingResult result
    ){
        try {
            if (result.hasErrors()) {
                List<String> errors = result.getAllErrors().stream()
                        .map(error -> error.getDefaultMessage())
                        .collect(Collectors.toList());
                return ResponseEntity.badRequest().body(errors);
            }
            billDetailService.updateActualQuantity(actualQuantityRequests);
            return ResponseEntity
                    .ok("Thành công!");
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

    @GetMapping("/detail")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE','CUSTOMER')")
    public ResponseEntity<?> getBillByCodeBill(@RequestParam(value ="codeBill", required = false) String codeBill) {
        if (codeBill==null){
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .status(HttpStatus.BAD_REQUEST.toString())
                            .mess("Lỗi: Mã hóa đơn không được để trống!")
                            .build()
            );
        }
        List<BillDetailOrderResponse> billDetailOrderResponses = billDetailByEmployeeService.getBillDetailsByCodeBill(codeBill);
        return ResponseEntity.ok(billDetailOrderResponses);
    }
    @PostMapping("/plusBillDetail")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public ResponseEntity<?> plusBillDetail(
            @RequestParam(value ="idBillDetail", required = false) Long idBillDetail,
            @RequestParam(value ="idProduct", required = false) Long idProduct
    ){
        try {
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
                                .mess("Lỗi: ID sản phẩm  không được để trống!")
                                .build()
                );
            }
            return ResponseEntity
                    .ok(billDetailByEmployeeService.plusBillDetail(idBillDetail,idProduct));
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
            @RequestParam(value ="idBillDetail", required = false) Long idBillDetail,
            @RequestParam(value ="idProduct", required = false) Long idProduct
    ){
        try {
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
                    .ok(billDetailByEmployeeService.subtractBillDetail(idBillDetail,idProduct));
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
            @RequestParam(value ="idBillDetail", required = false) Long idBillDetail,
            @RequestParam(value ="idProduct", required = false) Long idProduct
    ){
        try {
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
            billDetailByEmployeeService.deleteBillDetail(idBillDetail,idProduct);
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
