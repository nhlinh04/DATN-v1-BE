package org.example.datn_website_be.repository;

import org.example.datn_website_be.dto.response.ProductPromotionResponse;
import org.example.datn_website_be.model.PromotionDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PromotionDetailRepository extends JpaRepository<PromotionDetail,Long> {
    @Query("select p from PromotionDetail p where p.product.id=:idProduct and p.status IN (:statuses)")
    Optional<PromotionDetail> findPromotionDetailByIdProductDetailAndStatuses(@Param("idProduct") Long idProduct, @Param("statuses") List<String> statuses);
    @Query("select p from PromotionDetail p where p.promotion.id = :idPromotion and p.status IN (:statuses)")
    List<PromotionDetail> findPromotionDetailByIdPromotionAndStatuses(@Param("idPromotion") Long idPromotion, @Param("statuses") List<String> statuses);

    @Query("""
    SELECT new org.example.datn_website_be.dto.response.ProductPromotionResponse(
    p.id,p.name,p.pricePerBaseUnit,p.quantity,p.baseUnit,
    pro.id,pro.codePromotion,pro.value,pro.endAt,prod.id,prod.quantity,prod.status
    )
    FROM PromotionDetail prod
    INNER JOIN prod.promotion pro
    INNER JOIN prod.product p
    WHERE pro.id=:idPromotion
    """)
    List<ProductPromotionResponse> findProductByIdPromotion(@Param("idPromotion") Long idPromotion);
    @Query("SELECT NEW org.example.datn_website_be.dto.response.ProductPromotionResponse(" +
            "p.id,p.name,p.pricePerBaseUnit,p.quantity,p.baseUnit, " +
            "pro.id,pro.codePromotion,pro.value,pro.endAt,prod.id,prod.quantity,prod.status) " +
            "FROM Product p " +
            "JOIN p.promotionDetails prod WITH prod.status = 'ONGOING' " +
            "JOIN prod.promotion pro WITH pro.status = 'ONGOING' " +
            "WHERE p.status = 'ACTIVE' AND p.id = :idProduct")
    Optional<ProductPromotionResponse> findProductDetailByIdProduct(@Param("idProduct") Long idProduct);

    Optional<PromotionDetail> findByIdAndAndStatus(Long id, String status);
}
