package org.example.datn_website_be.repository;

import org.example.datn_website_be.dto.response.CartDetailProductDetailResponse;
import org.example.datn_website_be.dto.response.CartDetailResponse;
import org.example.datn_website_be.model.Bill;
import org.example.datn_website_be.model.CartDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface CartDetailRepository extends JpaRepository<CartDetail, Long> {
    @Query("""
            SELECT DISTINCT (cd.codeCart)
            FROM CartDetail cd JOIN Cart c ON cd.cart.id = c.id
            WHERE c.id = :id
            """)
    List<String> listCodeCartByIdCart(@Param("id") Long id);

    @Query("""
            SELECT new org.example.datn_website_be.dto.response.CartDetailResponse(
            cd.id, cd.codeCart, cd.quantity, c.id, p.id, cd.status)
            FROM CartDetail cd JOIN Cart c ON cd.cart.id = c.id
            JOIN Product p ON cd.product.id = p.id
            WHERE cd.cart.id = :id and cd.codeCart = :codeCart
            """)
    List<CartDetailResponse> listCartDetailResponseById(@Param("id") Long id, @Param("codeCart") String codeCart);

    @Modifying
    @Transactional
    @Query("UPDATE CartDetail set quantity = :quantity where cart.id = :id and codeCart = :codeCart")
    void updateById(@Param("quantity") Integer quantity, @Param("id") Long id, @Param("codeCart") String codeCart);

    @Query("""
                SELECT new org.example.datn_website_be.dto.response.CartDetailProductDetailResponse(
                    cart.id, cd.id, ac.id, cd.quantity, p.id, p.name,
                    p.quantity, p.pricePerBaseUnit, p.baseUnit, 
                    COALESCE(pro.id, NULL), COALESCE(pro.codePromotion, NULL),
                    COALESCE(pro.value, NULL), COALESCE(pro.endAt, NULL), 
                    COALESCE(prod.id, NULL), COALESCE(prod.quantity, 0.0))
                FROM CartDetail cd 
                INNER JOIN cd.cart cart
                INNER JOIN cart.account ac
                INNER JOIN cd.product p 
                LEFT JOIN p.promotionDetails prod WITH prod.status = 'ONGOING'
                LEFT JOIN prod.promotion pro WITH pro.status = 'ONGOING'
                WHERE ac.id = :idAccount AND p.status = 'ACTIVE'
            """)
    List<CartDetailProductDetailResponse> findCartDetailByIdAccount(@Param("idAccount") Long idAccount);

    @Query("""
            SELECT new org.example.datn_website_be.dto.response.CartDetailProductDetailResponse(
                        cart.id, cd.id, ac.id, cd.quantity, p.id, p.name,
                        p.quantity, p.pricePerBaseUnit,p.baseUnit, 
                        COALESCE(pro.id, NULL), COALESCE(pro.codePromotion, NULL),
                        COALESCE(pro.value, NULL), COALESCE(pro.endAt, NULL), 
                        COALESCE(prod.id, NULL), COALESCE(prod.quantity, 0.0))
                        FROM CartDetail cd 
                        INNER JOIN cd.cart cart
                        INNER JOIN cart.account ac
                        INNER JOIN cd.product p 
                        LEFT JOIN p.promotionDetails prod ON prod.status = 'ONGOING' 
                        LEFT JOIN prod.promotion pro
                        WHERE ac.id =:idAccount 
                        AND  cd.id IN (:idCartDetail)
                        AND p.status = 'ACTIVE' 
            """)
    List<CartDetailProductDetailResponse> findCartDetailByIdAccountAndIdCartDetail(@Param("idAccount") Long idAccount, @Param("idCartDetail") List<Long> idCartDetail);

    @Query(value = """
            SELECT * 
            FROM cart_detail 
            WHERE created_at < DATE_SUB(NOW(), INTERVAL 1 DAY)
            """, nativeQuery = true)
    List<CartDetail> findCartDetailOlderThanOneDay();
}
