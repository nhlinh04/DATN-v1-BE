package org.example.datn_website_be.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "BillDetail")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BillDetail extends BaseEntity {

    @Column
    private double quantity;

    @Column
    private double actualQuantity;

    @Column
    private BigDecimal priceDiscount;

    @Column
    private String note;

    @JsonBackReference(value = "billBillDetailReference")
    @ManyToOne
    @JoinColumn(name = "id_bill", referencedColumnName = "id")
    private Bill bill;

    @JsonBackReference(value = "productBillDetailReference")
    @ManyToOne
    @JoinColumn(name = "id_product", referencedColumnName = "id")
    private Product product;
}
