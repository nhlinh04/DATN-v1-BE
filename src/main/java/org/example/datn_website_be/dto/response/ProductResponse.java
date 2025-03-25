package org.example.datn_website_be.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductResponse {

    private Long id;

    private String name;

    private BigDecimal pricePerBaseUnit;

    private double quantity;

    private String baseUnit;

    private Long idCategory;

    private String nameCategory;

    private String status;

    private Date createdAt;

}
