package org.example.datn_website_be.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "ProductHistory")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductHistory extends BaseEntity{
    @Column
    private String note;

    @Column
    private Date createAt;

    @JsonBackReference(value = "productProductHistoryReference")
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @JsonBackReference(value = "accountBillHistoryReference")
    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    private String status;
}
