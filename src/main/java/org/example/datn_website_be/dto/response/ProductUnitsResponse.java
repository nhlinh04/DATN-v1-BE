package org.example.datn_website_be.dto.response;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductUnitsResponse {

    private String unitName;


    private double conversionFactor;


    private boolean type;
}
