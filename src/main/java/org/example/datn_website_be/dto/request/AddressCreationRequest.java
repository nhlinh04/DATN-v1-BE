package org.example.datn_website_be.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressCreationRequest {

    @NotBlank(message = "Mã thành phố là bắt buộc")
    private String codeCity;

    @NotBlank(message = "Mã tỉnh thành là bắt buộc")
    private String codeDistrict;

    @NotBlank(message = "Mã huyện/ phường/ xã là bắt buộc")
    private String codeWard;

    @NotBlank(message = "Địa chỉ là bắt buộc")
    @Size(min = 2, max = 100, message = "Tên phải chứa ít nhất 2 ký tự không được vượt quá 100 ký tự")
    private String address;
}
