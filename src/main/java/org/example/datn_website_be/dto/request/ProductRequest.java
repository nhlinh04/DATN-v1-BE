package org.example.datn_website_be.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {

    private Long id;

    @NotBlank(message = "Tên sản phẩm không được để trống")
    @Size(max = 255, message = "Tên sản phẩm không được vượt quá 255 ký tự")
    private String name;
    @NotNull(message = "Giá không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Giá phải lớn hơn 0")
    private BigDecimal pricePerBaseUnit;

    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 0, message = "Số lượng phải lớn hơn hoặc bằng 0")
    private double quantity;

    @NotBlank(message = "Tên đơn vị gốc không được để trống")
    @Size(max = 255, message = "Tên đơn vị gốc không được vượt quá 255 ký tự")
    private String baseUnit;

    private List<byte[]> listImages;

    @NotNull(message = "Danh mục không được để trống")
    private Long idCategory;

    @NotNull(message = "Danh sách quy đổi không được để trống")
    @Size(min = 1, message = "Phải có ít nhất một giá trị quy đổi")
    private List<@Valid ProductUnitsRequest> productUnits;

}
