package org.example.datn_website_be.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "Product")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product extends BaseEntity {

    @Column
    private String name;

    @Column
    private BigDecimal pricePerBaseUnit;

    @Column
    private double quantity;

    @Column
    private String baseUnit;

    @JsonBackReference(value = "categoryProductReference")
    @ManyToOne
    @JoinColumn(name = "id_category", referencedColumnName = "id")
    private Category category;

    @JsonIgnore
    @JsonManagedReference(value = "productUnitsReference")
    @OneToMany(mappedBy = "product")
    private List<ProductUnits> productUnits;

    @JsonIgnore
    @JsonManagedReference(value = "productImagesReference")
    @OneToMany(mappedBy = "product")
    private List<ProductImage> productImages;

    @JsonIgnore
    @JsonManagedReference(value = "productProductHistoryReference")
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductHistory> productHistories;

    @JsonManagedReference(value = "productBillDetailReference")
    @OneToMany(mappedBy = "product")
    @JsonIgnore
    private List<BillDetail> billDetails;

    @JsonManagedReference(value = "productCartDetailReference")
    @OneToMany(mappedBy = "product")
    @JsonIgnore
    private List<CartDetail> cartDetails;

    @JsonManagedReference(value = "productPromotionDetailReference")
    @OneToMany(mappedBy = "product")
    @JsonIgnore
    private List<PromotionDetail> promotionDetails;
}
