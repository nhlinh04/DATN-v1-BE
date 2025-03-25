package org.example.datn_website_be.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductDetailPromoRequest {
    @NotNull(message = "Id của sản phẩm là bắt buộc")
    private Long idProduct;
    @NotNull(message = "Số lượng của sản phẩm là bắt buộc")
    @Positive(message = "Số lượng phải lớn hơn 0")
    private double quantity;
}
