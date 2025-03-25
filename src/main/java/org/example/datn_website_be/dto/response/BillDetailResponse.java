package org.example.datn_website_be.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class BillDetailResponse {

    private Long id;

    private Long idProduct;

    private String nameProduct;

    private String baseUnit;

    private double quantity;

    private BigDecimal totalAmount;

    private String status;

    private BigDecimal priceDiscount;

    private BigDecimal totalMerchandise;

    private int type;

}
