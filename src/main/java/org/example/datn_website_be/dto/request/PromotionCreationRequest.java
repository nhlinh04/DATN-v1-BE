package org.example.datn_website_be.dto.request;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PromotionCreationRequest {
    @Valid
    private List<ProductPromoRequest> productPromoRequest;
    @Valid
    private PromotionRequest promotionRequest;
}
