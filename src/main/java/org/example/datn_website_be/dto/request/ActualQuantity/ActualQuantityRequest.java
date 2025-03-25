package org.example.datn_website_be.dto.request.ActualQuantity;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActualQuantityRequest {
    @NotNull(message = "Id của hóa đơn là bắt buộc")
    private Long id;

    @NotNull(message = "Số lượng của sản phẩm là bắt buộc")
    @Positive(message = "Số lượng phải lớn hơn 0")
    private double actualQuantity;
}
