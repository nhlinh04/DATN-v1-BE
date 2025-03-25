package org.example.datn_website_be.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class ProductViewCustomerReponse {

    private Long idProduct;

    private String nameProduct;

    private double quantity;

    private String baseUnit;

    private Long idCategory;

    private String nameCategory;

    private BigDecimal priceBase;

    private BigDecimal priceSale;

}
