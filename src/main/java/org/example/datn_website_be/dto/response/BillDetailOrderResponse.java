package org.example.datn_website_be.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class BillDetailOrderResponse {
    private Long idBill;

    private Long idBillDetail;

    private BigDecimal priceDiscount;

    private double quantityBillDetail;

    private double actualQuantity;

    private Long idProduct;

    private String nameProduct;

    private double quantityProduct;

    private BigDecimal pricePerBaseUnit;

    private String baseUnit;

    private Long idPromotion;

    private String codePromotion;

    private Integer value;

    private Date endAtByPromotion;

    private Long idPromotionDetail;

    private double quantityPromotionDetail;
}
