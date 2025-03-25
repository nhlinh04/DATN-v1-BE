package org.example.datn_website_be.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartDetailResponse {

    private Long id;

    private String codeCart;

    private double quantity;

    private Long idCart;

    private Long idProduct;

    private String status;
}
