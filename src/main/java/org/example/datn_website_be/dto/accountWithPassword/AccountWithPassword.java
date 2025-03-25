package org.example.datn_website_be.dto.accountWithPassword;


import org.example.datn_website_be.model.Account;

public class AccountWithPassword {
    private Account account;
    private String password;

    public AccountWithPassword(Account account, String randomPassword) {
        this.account = account;
        this.password = randomPassword;
    }

    public Account getAccount() {
        return account;
    }

    public String getPassword() {
        return password;
    }
}
