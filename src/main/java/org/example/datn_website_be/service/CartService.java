package org.example.datn_website_be.service;

import org.example.datn_website_be.Enum.Status;
import org.example.datn_website_be.dto.response.CartResponse;
import org.example.datn_website_be.model.Account;
import org.example.datn_website_be.model.Cart;
import org.example.datn_website_be.repository.AccountRepository;
import org.example.datn_website_be.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CartService {

    @Autowired
    CartRepository cartRepository;
    @Autowired
    AccountRepository accountRepository;

    public Account getUseLogin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Account user = accountRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Lỗi liên quan đến đăng nhập vui lòng thử lại"));
        return user;
    }
    public CartResponse getCartResponseByAccountId() {
        Long idEmployees = getUseLogin().getId();
        if(idEmployees == null){
            new RuntimeException("Lỗi đăng nhập!");
        }
        Optional<CartResponse> cartResponse = cartRepository.CartResponse(idEmployees);
        if (cartResponse.isEmpty()) {
            Cart cart = cartRepository.save(convertCartRequestDTO(idEmployees));
            CartResponse newCartResponse = new CartResponse();
            newCartResponse.setId(cart.getId());
            newCartResponse.setIdAccount(cart.getAccount().getId());
            return newCartResponse;
        } else {
            return cartResponse.get();
        }
    }

    public Cart getCartByAccountId(long accountId) {
        Optional<Cart> cartOptional = cartRepository.findByAccount_Id(accountId);
        if (cartOptional.isEmpty()) {
            return cartRepository.save(convertCartRequestDTO(accountId));
        } else {
            return cartOptional.get();
        }
    }

    public void deleteCartById(Long id) {
        if (cartRepository.existsById(id)) {
            cartRepository.deleteById(id);
        } else {
            throw new RuntimeException("Tài nguyên giỏ hàng không tồn tại trong hệ thống.");
        }
    }

    public Cart convertCartRequestDTO(Long idEmployees) {
        Account account = accountRepository.findById(idEmployees)
                .orElseThrow(() -> new RuntimeException("Không tìm tài khoản này!"));
        Cart cart = Cart.builder()
                .account(account)
                .build();
        cart.setCreatedBy(account.getName());
        cart.setUpdatedBy(account.getName());
        cart.setStatus(Status.ACTIVE.toString());
        return cart;
    }
}

