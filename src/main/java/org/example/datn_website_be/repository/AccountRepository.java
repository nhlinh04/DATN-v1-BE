package org.example.datn_website_be.repository;

import org.example.datn_website_be.dto.response.AccountResponse;
import org.example.datn_website_be.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByEmail(String email);

    Optional<Account> findById(Long id);

    Optional<Account> findByIdAndStatus(Long id,String status);

    List<Account> findAllByStatus(String status);

    @Query(value = """
            SELECT new org.example.datn_website_be.dto.response.AccountResponse(
                a.id, a.name, a.email,a.phoneNumber, a.role,
                a.gender, a.birthday, a.rewards, a.status)
            FROM Account a
            WHERE a.role = :role
            """)
    List<AccountResponse> listCustomerResponseByStatus(@Param("role") String role);

    @Query(value = """
            SELECT new org.example.datn_website_be.dto.response.AccountResponse(
                a.id, a.name, a.email,a.phoneNumber, a.role,
                a.gender, a.birthday, a.rewards, a.status)
            FROM Account a
            WHERE a.role = :role
            """)
    List<AccountResponse> listEmployeeResponseByStatus(@Param("role") String role);

    @Query("SELECT a.email FROM Account a WHERE a.id IN :customerIds")
    List<String> findEmailsByCustomerIds(@Param("customerIds") List<Long> customerIds);
}
