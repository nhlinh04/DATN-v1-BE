package org.example.datn_website_be.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder


public class BillDetailStatisticalProductRespone {

    private Long idProduct;

    private String nameProduct;

    private double quantity;

    private BigDecimal priceDiscount;

    private BigDecimal revenue;

    private double actualQuantity;

    private BigDecimal actualRevenue;

}
