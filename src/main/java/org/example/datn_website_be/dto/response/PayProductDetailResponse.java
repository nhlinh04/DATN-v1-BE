package org.example.datn_website_be.dto.response;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
@Getter
@Setter
@NoArgsConstructor
public class PayProductDetailResponse {
    private Long idProduct;

    private String nameProduct;

    private double quantityProduct;

    private BigDecimal pricePerBaseUnit;

    private String baseUnit;

    private Long idCategory;

    private String nameCategory;

    private Long idPromotion;

    private String codePromotion;

    private Integer value;

    private Date endAtByPromotion;

    private Long idPromotionDetail;

    private double quantityPromotionDetail;

    private double quantityBuy;

    private String error;

    public PayProductDetailResponse(Long idProduct, String nameProduct,double quantityProduct, BigDecimal pricePerBaseUnit,String baseUnit,Long idCategory,String nameCategory ,Long idPromotion, String codePromotion, Integer value, Date endAtByPromotion, Long idPromotionDetail, double quantityPromotionDetail) {
        this.idProduct = idProduct;
        this.nameProduct = nameProduct;
        this.quantityProduct = quantityProduct;
        this.pricePerBaseUnit = pricePerBaseUnit;
        this.baseUnit = baseUnit;
        this.idCategory = idCategory;
        this.nameCategory = nameCategory;
        this.idPromotion = idPromotion;
        this.codePromotion = codePromotion;
        this.value = value;
        this.endAtByPromotion = endAtByPromotion;
        this.idPromotionDetail = idPromotionDetail;
        this.quantityPromotionDetail = quantityPromotionDetail;
    }

    public PayProductDetailResponse(Long idProduct, double quantityBuy, String error) {
        this.idProduct = idProduct;
        this.quantityBuy = quantityBuy;
        this.error = error;
    }
    public BigDecimal calculatePricePerProductDetail() {
        BigDecimal price = pricePerBaseUnit;
        double quantity = quantityBuy;

        if (value == null) {
            // Không có khuyến mãi
            return price.multiply(BigDecimal.valueOf(quantity));
        } else if (quantity <= quantityPromotionDetail) {
            // Có khuyến mãi và số lượng trong giỏ <= số lượng áp dụng khuyến mãi
            BigDecimal discountPrice = price.multiply(BigDecimal.valueOf(1 - value / 100));
            return discountPrice.multiply(BigDecimal.valueOf(quantity));
        } else {
            // Có khuyến mãi và số lượng trong giỏ > số lượng áp dụng khuyến mãi
            BigDecimal discountPrice = price.multiply(BigDecimal.valueOf(1 - value / 100))
                    .multiply(BigDecimal.valueOf(quantityPromotionDetail));
            BigDecimal nonDiscountPrice = price.multiply(BigDecimal.valueOf(quantity - quantityPromotionDetail));
            return discountPrice.add(nonDiscountPrice);
        }
    }
}

