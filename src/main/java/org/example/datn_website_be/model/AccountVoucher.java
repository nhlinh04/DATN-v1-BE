package org.example.datn_website_be.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "AccountVoucher")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountVoucher extends BaseEntity {

    @JsonBackReference(value = "accountReference")
    @ManyToOne
    @JoinColumn(name = "id_account", referencedColumnName = "id")
    private Account account;

    @JsonBackReference(value = "accountVoucherReference")
    @ManyToOne
    @JoinColumn(name = "voucher_id")
    private Voucher voucher;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false)
    private Date createdAt;

}
