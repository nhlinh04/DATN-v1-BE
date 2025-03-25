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

import java.util.Date;

@Entity
@Table(name = "BillHistory")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BillHistory extends BaseEntity {

    @Column
    private String note;

    @Column
    private Date createAt;

    @JsonBackReference(value = "billBillHistoryReference")
    @ManyToOne
    @JoinColumn(name = "bill_id")
    private Bill bill;

    @JsonBackReference(value = "accountBillHistoryReference")
    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    private String status;

}
