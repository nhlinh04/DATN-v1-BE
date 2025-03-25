package org.example.datn_website_be.dto.request.UpdateProduct;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.datn_website_be.dto.request.ProductRequest;
import org.example.datn_website_be.dto.request.ProductUnitsRequest;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductProductUnitsRequest {
    @Valid
    private ProductRequest productRequest;

    private List<Long> idProductUnits;
}
