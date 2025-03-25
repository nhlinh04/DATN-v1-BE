package org.example.datn_website_be.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductUnitsRequest {
    private Long id; // Không thêm @NotNull để cho phép null khi tạo mới

    @NotBlank(message = "Tên đơn vị quy đổi không được để trống")
    @Size(max = 255, message = "Tên đơn vị quy đổi không được vượt quá 255 ký tự")
    private String unitName;

    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 0, message = "Số lượng phải lớn hơn hoặc bằng 0")
    private Double conversionFactor; // Đổi sang Double để khớp với dữ liệu FE

    private boolean type;
}