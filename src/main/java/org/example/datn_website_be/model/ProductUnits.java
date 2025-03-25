package org.example.datn_website_be.model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "ProductUnits")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductUnits extends BaseEntity{

    @Column
    private String unitName;

    @Column
    private double conversionFactor;

    @Column
    private boolean type;

    @ManyToOne
    @JoinColumn(name = "id_product", referencedColumnName = "id")
    @JsonIgnore
    private Product product;
}
