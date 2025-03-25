package org.example.datn_website_be.controller;

import jakarta.persistence.criteria.Predicate;
import jakarta.validation.Valid;
import org.example.datn_website_be.dto.request.VoucherRequest;
import org.example.datn_website_be.dto.response.Response;
import org.example.datn_website_be.dto.response.VoucherBillResponse;
import org.example.datn_website_be.dto.response.VoucherResponse;
import org.example.datn_website_be.model.Voucher;
import org.example.datn_website_be.service.VoucherService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/voucher")
public class VoucherController {

    @Autowired
    VoucherService voucherService;

    @PutMapping("/update-statuses")
    public ResponseEntity<?> updateVoucherStatusesManually() {
        try {
            voucherService.updateVoucherStatuses();
            return ResponseEntity.ok(Response.builder()
                    .status(HttpStatus.OK.toString())
                    .mess("Voucher statuses updated successfully.")
                    .build());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.builder()
                            .status(HttpStatus.INTERNAL_SERVER_ERROR.toString())
                            .mess(e.getMessage())
                            .build());
        }
    }

    @GetMapping("/list-voucher")
    public ResponseEntity<Map<String, Object>> getAllVouchers(
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "searchTerm", required = false) String searchTerm,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate endDate,
            @RequestParam(value = "type", required = false) Integer type,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size
    ) {
        String trimmedSearchTerm = Optional.ofNullable(searchTerm)
                .map(String::trim)
                .filter(term -> !term.isEmpty())
                .orElse(null);

        Specification<Voucher> spec = (root, query, criteriaBuilder) -> {
            Predicate p = criteriaBuilder.conjunction();

            if (status != null && !status.isEmpty()) {
                p = criteriaBuilder.and(p, criteriaBuilder.equal(root.get("status"), status));
            }

            if (trimmedSearchTerm != null) {
                String likePattern = "%" + trimmedSearchTerm.toLowerCase() + "%";
                Predicate codePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("codeVoucher")), likePattern);
                Predicate namePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), likePattern);
                p = criteriaBuilder.and(p, criteriaBuilder.or(codePredicate, namePredicate));
            }

            if (type != null) {
                p = criteriaBuilder.and(p, criteriaBuilder.equal(root.get("type"), type));
            }

            if (startDate != null) {
                Predicate startAtPredicate = criteriaBuilder.greaterThanOrEqualTo(root.get("startAt"), startDate.atStartOfDay());
                p = criteriaBuilder.and(p, startAtPredicate);
            }

            if (endDate != null) {
                Predicate endAtPredicate = criteriaBuilder.lessThanOrEqualTo(root.get("endAt"), endDate.atTime(23, 59, 59));
                p = criteriaBuilder.and(p, endAtPredicate);
            }

            query.orderBy(criteriaBuilder.desc(root.get("id")));

            return p;
        };

        Pageable pageable = PageRequest.of(page, size);
        Page<VoucherResponse> vouchers = voucherService.getVouchers(spec, pageable);
        long totalRecords = voucherService.countVouchers(spec);

        Map<String, Object> response = new HashMap<>();
        response.put("vouchers", vouchers.getContent());
        response.put("currentPage", vouchers.getNumber());
        response.put("totalItems", vouchers.getTotalElements());
        response.put("totalPages", vouchers.getTotalPages());
        response.put("totalRecords", totalRecords);

        return ResponseEntity.ok(response);
    }


    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createVoucher(@RequestBody @Valid VoucherRequest voucherRequest,
                                           BindingResult result) {
        try {
            if (result.hasErrors()) {
                List<String> errors = result.getAllErrors().stream()
                        .map(error -> error.getDefaultMessage())
                        .collect(Collectors.toList());
                return ResponseEntity.badRequest().body(errors);
            }

            Voucher createdVoucher = voucherService.createVoucher(voucherRequest);
            VoucherResponse response = convertToVoucherResponse(createdVoucher);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Response.builder()
                            .status(HttpStatus.CONFLICT.toString())
                            .mess(e.getMessage())
                            .build());
        }
    }


    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateVoucher(@PathVariable("id") long id,
                                           @RequestBody VoucherRequest voucherRequest) {
        try {
            Voucher updatedVoucher = voucherService.updateVoucher(id, voucherRequest);
            VoucherResponse response = convertToVoucherResponse(updatedVoucher);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Response.builder()
                            .status(HttpStatus.BAD_REQUEST.toString())
                            .mess(e.getMessage())
                            .build());
        }
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<?> getVoucher(@PathVariable Long id) {
        try {
            VoucherResponse voucherResponse = voucherService.getVoucherById(id);
            return ResponseEntity.ok(voucherResponse);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Response.builder()
                            .status(HttpStatus.BAD_REQUEST.toString())
                            .mess(e.getMessage())
                            .build());
        }
    }

    @PutMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteVoucher(@PathVariable("id") Long id) {
        try {
            Voucher updatedVoucher = voucherService.deleteVoucher(id);
            VoucherResponse response = convertToVoucherResponse(updatedVoucher);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Response.builder()
                            .status(HttpStatus.NOT_FOUND.toString())
                            .mess(e.getMessage())
                            .build());
        }
    }

    @PutMapping("/end-early/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> endVoucherEarly(@PathVariable("id") Long id) {
        try {
            Voucher updatedVoucher = voucherService.endVoucherEarly(id);
            VoucherResponse response = convertToVoucherResponse(updatedVoucher);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Response.builder()
                            .status(HttpStatus.NOT_FOUND.toString())
                            .mess(e.getMessage())
                            .build());
        }
    }

    @PutMapping("/reactivate/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> reactivateVoucher(@PathVariable("id") Long id) {
        try {
            Voucher updatedVoucher = voucherService.reactivateVoucher(id);
            VoucherResponse response = convertToVoucherResponse(updatedVoucher);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Response.builder()
                            .status(HttpStatus.NOT_FOUND.toString())
                            .mess(e.getMessage())
                            .build());
        }
    }

    public VoucherResponse convertToVoucherResponse(Voucher voucher) {
        VoucherResponse response = new VoucherResponse();
        BeanUtils.copyProperties(voucher, response);
        return response;
    }

    @GetMapping("/getListVoucherBillPublic")
    public List<VoucherBillResponse> findListVoucherByStatus() {
        return voucherService.findListVoucherByStatusAndIsPublic();
    }

    @GetMapping("/getListVoucherBillPrivate")
    public ResponseEntity<?> findListVoucherByStatusAndListIdVoucher(@RequestParam(value = "idAccount", required = false) Long idAccount) {
        try {
            if (idAccount == null) {
                return ResponseEntity.badRequest().body(
                        Response.builder()
                                .status(HttpStatus.BAD_REQUEST.toString())
                                .mess("Lỗi: ID của tài khoản không được để trống!")
                                .build()
                );
            }
            return ResponseEntity.ok().body(voucherService.findListVoucherByStatusAndIsPrivate(idAccount));
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

    @GetMapping("/getFindVoucherBill")
    public ResponseEntity<?> findVoucherByStatusAndIdVoucher(@RequestParam(value = "idVoucher", required = false) Long idVoucher) {
        try {
            if (idVoucher == null) {
                return ResponseEntity.badRequest().body(
                        Response.builder()
                                .status(HttpStatus.BAD_REQUEST.toString())
                                .mess("Lỗi: ID của phiếu giảm giá không được để trống!")
                                .build()
                );
            }
            return ResponseEntity.ok().body(voucherService.findVoucherByListIdAndStatus(idVoucher));
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

    @GetMapping("/findVoucherByCodeVoucher")
    public ResponseEntity<?> getVoucherBycodeVoucher(@RequestParam(value = "codeVoucher", required = false) String codeVoucher) {
        try {
            if (codeVoucher == null) {
                return ResponseEntity.badRequest().body(
                        Response.builder()
                                .status(HttpStatus.BAD_REQUEST.toString())
                                .mess("Lỗi: mã giảm giá của phiếu giảm giá không được để trống!")
                                .build()
                );
            }
            return ResponseEntity.ok().body(voucherService.getVoucherBycodeVoucher(codeVoucher));
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
