package org.example.datn_website_be.controller;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Predicate;
import jakarta.validation.Valid;
import org.example.datn_website_be.Enum.Status;
import org.example.datn_website_be.dto.request.PayBillRequest;
import org.example.datn_website_be.dto.response.PayBillResponse;
import org.example.datn_website_be.dto.response.Response;
import org.example.datn_website_be.model.Bill;
import org.example.datn_website_be.model.PayBill;
import org.example.datn_website_be.repository.BillRepository;
import org.example.datn_website_be.service.PayBillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/v1/pay-bill")
public class PayBillController {

    @Autowired
    PayBillService payBillService;
    @Autowired
    BillRepository billRepository;
    @GetMapping("/list-pay-bills")
    public Page<PayBillResponse> getAllPayBills(
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "tradingCode", required = false) String tradingCode,
            @RequestParam(value = "type", required = false) Integer type,
            @RequestParam(value = "createdAt", required = false) String createdAtStr,
            @RequestParam(value = "codeBill", required = false) String codeBill,  // Add codeBill param
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", defaultValue = "createdAt") String sortField,
            @RequestParam(value = "sortDirection", defaultValue = "DESC") String sortDirection
    ) {
        Date createdAt = parseDate(createdAtStr);

        Specification<PayBill> spec = (root, query, criteriaBuilder) -> {
            Predicate p = criteriaBuilder.conjunction();

            if (status != null && !status.isEmpty()) {
                p = criteriaBuilder.and(p, criteriaBuilder.equal(root.get("status"), status));
            }

            if (tradingCode != null && !tradingCode.isEmpty()) {
                p = criteriaBuilder.and(p, criteriaBuilder.equal(root.get("tradingCode"), tradingCode));
            }

            if (type != null) {
                p = criteriaBuilder.and(p, criteriaBuilder.equal(root.get("type"), type));
            }

            if (createdAt != null) {
                String formattedCreatedAt = new SimpleDateFormat("yyyy-MM-dd").format(createdAt);
                p = criteriaBuilder.and(p, criteriaBuilder.like(root.get("createdAt").as(String.class), "%" + formattedCreatedAt + "%"));
            }

            if (codeBill != null && !codeBill.isEmpty()) {
                p = criteriaBuilder.and(p, criteriaBuilder.equal(root.get("bill").get("codeBill"), codeBill));
            }

            return p;
        };

        // Setting the sorting direction
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));

        return payBillService.getPayBills(spec, pageable);
    }


    // Helper method to parse dates
    public Date parseDate(String dateStr) {
        if (dateStr == null) return null;

        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        try {
            if (dateStr.length() == 10) {
                return dateFormat.parse(dateStr);
            } else {
                return dateTimeFormat.parse(dateStr);
            }
        } catch (ParseException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid date format: " + dateStr + ". Expected formats: yyyy-MM-dd or yyyy-MM-dd'T'HH:mm:ss", e);
        }
    }

    @PutMapping("/update-pay-bill/{codeBill}")
    public ResponseEntity<?> updateBillStatusAndNote(
            @PathVariable String codeBill,
            @RequestParam String status
    ) {
        try {
            // Validate the status parameter
            if (status == null || status.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Status cannot be null or empty.");
            }

            // Call the service to update the status
            payBillService.updatePaymentBill(codeBill, status);

            // Return a success response
            return ResponseEntity.ok(Map.of(
                    "message", "Bill status updated successfully.",
                    "codeBill", codeBill,
                    "newStatus", status
            ));
        } catch (EntityNotFoundException e) {
            // Handle case where the bill is not found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "message", "Bill not found.",
                    "codeBill", codeBill
            ));
        } catch (IllegalArgumentException e) {
            // Handle invalid status or other argument-related issues
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "message", e.getMessage(),
                    "codeBill", codeBill
            ));
        } catch (Exception e) {
            // Handle unexpected exceptions
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "message", "An unexpected error occurred.",
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/listPayBill")
    public ResponseEntity<?> findPayBillOrderByCodeBill(@RequestParam(value = "codeBill", required = false) String codeBill) {
        try {
            if (codeBill == null) {
                return ResponseEntity.badRequest().body(
                        Response.builder()
                                .status(HttpStatus.BAD_REQUEST.toString())
                                .mess("Lỗi: Mã hóa đơn không được để trống!")
                                .build()
                );
            }
            return ResponseEntity.ok(payBillService.findPayBillOrderByCodeBill(codeBill));
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

    @PostMapping("/createPayBill")
    public ResponseEntity<?> createPayBill(@RequestBody @Valid PayBillRequest payBillRequest, BindingResult result) {
        try {
            if (result.hasErrors()) {
                List<String> errors = result.getAllErrors().stream()
                        .map(error -> error.getDefaultMessage())
                        .collect(Collectors.toList());
                return ResponseEntity.badRequest().body(errors);
            }
            Optional<Bill> billOptional = billRepository.findByCodeBill(payBillRequest.getCodeBill());
            if (billOptional.isEmpty()) {
                throw new RuntimeException("Hóa đơn không tồn tại");
            }
            if (!billOptional.get().getStatus().equals(Status.WAITING_FOR_PAYMENT.toString())) {
                throw new RuntimeException("Hóa đơn " + payBillRequest.getCodeBill() + " không còn ở trạng thái thanh toán!");
            }
            PayBill payBill = payBillService.createPayBill(payBillRequest, 1, Status.COMPLETED.toString());
            return ResponseEntity.ok(payBill);
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

    @DeleteMapping("/deletePayBill")
    public ResponseEntity<?> deletePayBillById(@RequestParam(value = "id", required = false) Long id) {
        try {
            if (id == null) {
                return ResponseEntity.badRequest().body(
                        Response.builder()
                                .status(HttpStatus.BAD_REQUEST.toString())
                                .mess("Lỗi: ID thanh toán hóa đơn không được để trống!")
                                .build()
                );
            }
            payBillService.deletePayBillById(id);
            return ResponseEntity.ok("Xóa thành công!");
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
