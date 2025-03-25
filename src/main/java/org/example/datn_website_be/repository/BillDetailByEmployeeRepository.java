package org.example.datn_website_be.repository;

import org.example.datn_website_be.dto.response.BillDetailOrderResponse;
import org.example.datn_website_be.model.BillDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BillDetailByEmployeeRepository extends JpaRepository<BillDetail, Long> {
    @Query(value = "SELECT " +
            "new org.example.datn_website_be.dto.response.BillDetailOrderResponse( " +
            "b.id, bd.id,bd.priceDiscount, bd.quantity,bd.actualQuantity, p.id, p.name,  p.quantity, p.pricePerBaseUnit, p.baseUnit," +
            "COALESCE(pro.id, NULL), COALESCE(pro.codePromotion, NULL), COALESCE(pro.value, NULL), " +
            "COALESCE(pro.endAt, NULL), COALESCE(prod.id, NULL), COALESCE(prod.quantity, 0.0)) " +
            "FROM BillDetail bd " +
            "INNER JOIN bd.bill b  " +
            "INNER JOIN bd.product p  " +
            "LEFT JOIN p.promotionDetails prod WITH prod.status = 'ONGOING' " +
            "LEFT JOIN prod.promotion pro WITH pro.status = 'ONGOING'" +
            "WHERE b.codeBill = :codeBill")
    List<BillDetailOrderResponse> getBillDetailsByCodeBill(String codeBill);

}
