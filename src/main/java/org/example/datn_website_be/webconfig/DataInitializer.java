package org.example.datn_website_be.webconfig;


import org.example.datn_website_be.Enum.Role;
import org.example.datn_website_be.Enum.Status;
import org.example.datn_website_be.model.Account;
import org.example.datn_website_be.model.PaymentMethod;
import org.example.datn_website_be.repository.AccountRepository;
import org.example.datn_website_be.repository.PaymentMethodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

@Component
public class DataInitializer implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private PaymentMethodRepository paymentMethodRepository;
    @Autowired
    private AccountRepository accountRepository;
    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    Calendar calendar = Calendar.getInstance();

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        initializePaymentMethods();
    }

    private void initializePaymentMethods() {
        calendar.set(2004, Calendar.JULY, 6);
        Date birthday = calendar.getTime();
        addPaymentMethodIfNotExist("QR payment", "Thanh toán QR", 1);
        addPaymentMethodIfNotExist("Cash payment", "Thanh toán tiền mặt", 2);
        addAccountAdminIfNotExist("Nguyễn Văn A","admin@gmail.com","0983729351","1",birthday, Role.ADMIN.toString());
        addAccountAdminIfNotExist("Nguyễn Văn E","employee@gmail.com","0983729351","1",birthday,Role.EMPLOYEE.toString());
        addAccountAdminIfNotExist("Nguyễn Văn C","customer@gmail.com","0983729351","1",birthday,Role.CUSTOMER.toString());
    }

    private void addPaymentMethodIfNotExist(String methodName, String note, Integer type) {
        if (paymentMethodRepository.findByMethodNameAndType(methodName,type).isEmpty()) {
            PaymentMethod paymentMethod = new PaymentMethod();
            paymentMethod.setMethodName(methodName);
            paymentMethod.setNote(note);
            paymentMethod.setStatus("ACTIVE");
            paymentMethod.setType(type);
            paymentMethodRepository.save(paymentMethod);
        }
    }
    private void addAccountAdminIfNotExist(String name, String email, String phoneNumber, String password, Date birthday, String role){
        Optional<Account> accountOP = accountRepository.findByEmail(email);
        if (accountOP.isEmpty()) {
            Account account = Account.builder()
                    .name(name)
                    .email(email)
                    .phoneNumber(phoneNumber)
                    .password(passwordEncoder.encode(password))
                    .role(role)
                    .gender(1)
                    .birthday(birthday)
                    .rewards(0)
                    .build();
            account.setStatus(Status.ACTIVE.toString());
            accountRepository.save(account);
        }
    }
}
