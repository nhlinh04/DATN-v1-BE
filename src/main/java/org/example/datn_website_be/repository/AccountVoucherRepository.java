package org.example.datn_website_be.repository;

import org.springframework.transaction.annotation.Transactional;
import org.example.datn_website_be.dto.response.AccountVoucherResponse;
import org.example.datn_website_be.model.AccountVoucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountVoucherRepository extends JpaRepository<AccountVoucher, Long> {

    @Query("""
            SELECT new org.example.datn_website_be.dto.response.AccountVoucherResponse(
            av.id, a.id, a.name, v.id, v.name, av.status)
            FROM AccountVoucher av 
            JOIN av.account a 
            JOIN av.voucher v
            WHERE av.status = :status
            """)
    List<AccountVoucherResponse> listAccountVoucherResponsesByStatus(@Param("status") String status);

    @Modifying
    @Transactional
    @Query("UPDATE AccountVoucher av SET av.status = :status WHERE av.voucher.id = :voucherId")
    void updateStatusByVoucherId(@Param("voucherId") Long voucherId, @Param("status") String status);

    @Query("SELECT av.account.id FROM AccountVoucher av WHERE av.voucher.id = :voucherId")
    List<Long> findAccountIdsByVoucherId(@Param("voucherId") Long voucherId);

    @Query("SELECT av.voucher.id FROM AccountVoucher av WHERE av.account.id = :idAccount And av.status = 'ACTIVE'")
    List<Long> findIdVoucherByIdAccount(@Param("idAccount") Long idAccount);

    @Query("""
            SELECT av 
            FROM AccountVoucher av 
            INNER JOIN av.voucher v 
            WHERE av.account.id = :idAccount And v.id = :idVoucher And av.status = 'ACTIVE'
            """)
    Optional<AccountVoucher> findAccountVoucherByIdAccountAndidVoucher(@Param("idAccount") Long idAccount,@Param("idVoucher") Long idVoucher);
}
