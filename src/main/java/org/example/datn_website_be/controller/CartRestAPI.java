package org.example.datn_website_be.controller;

import org.example.datn_website_be.dto.response.CartResponse;
import org.example.datn_website_be.dto.response.Response;
import org.example.datn_website_be.model.Cart;
import org.example.datn_website_be.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart")
public class CartRestAPI {

    @Autowired
    CartService cartService;
    @GetMapping("/find")
    public ResponseEntity<?> findByAccountId() {
        try {
            CartResponse cart = cartService.getCartResponseByAccountId();
            return ResponseEntity.ok(cart);
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Response.builder()
                            .status(HttpStatus.CONFLICT.toString())
                            .mess(e.getMessage())
                            .build()
                    );
        }
    }

    @GetMapping("/get-cart-by-account/{accountId}")
    public ResponseEntity<?> getCart(@PathVariable("accountId") long accountId){
        try {
            Cart cart = cartService.getCartByAccountId(accountId);
            return ResponseEntity.ok(cart);
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Response.builder()
                            .status(HttpStatus.CONFLICT.toString())
                            .mess(e.getMessage())
                            .build()
                    );
        }
    }




    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        try {
            cartService.deleteCartById(id);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(Response.builder()
                            .status(HttpStatus.OK.toString())
                            .mess("Delete success")
                            .build()
                    );
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Response.builder()
                            .status(HttpStatus.NOT_FOUND.toString())
                            .mess(e.getMessage())
                            .build()
                    );
        }
    }
}
