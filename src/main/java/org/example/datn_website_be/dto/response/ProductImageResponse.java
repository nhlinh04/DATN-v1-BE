package org.example.datn_website_be.dto.response;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ProductImageResponse {
    private byte[] imageByte;
}
