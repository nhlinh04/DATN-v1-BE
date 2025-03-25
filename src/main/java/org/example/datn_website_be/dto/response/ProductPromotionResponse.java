package org.example.datn_website_be.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductPromotionResponse {

    private Long idProduct;

    private String nameProduct;

    private BigDecimal pricePerBaseUnit;

    private double quantityProduct;

    private String baseUnit;

    private Long idPromotion;

    private String codePromotion;

    private Integer value;

    private Date endAtByPromotion;

    private Long idPromotionDetail;

    private double quantityPromotionDetail;

    private String status;

}
