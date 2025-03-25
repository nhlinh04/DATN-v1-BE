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
public class CartDetailProductDetailResponse {
    private Long idCart;

    private Long idCartDetail;

    private Long idAccount;

    private double quantityCartDetail;

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

    public BigDecimal calculatePricePerProductDetail() {
        BigDecimal price = pricePerBaseUnit;
        double quantity = quantityCartDetail;

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
