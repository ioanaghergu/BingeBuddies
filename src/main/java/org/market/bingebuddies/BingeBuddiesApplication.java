package org.market.bingebuddies;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class BingeBuddiesApplication {
    private PasswordEncoder passwordEncoder;

    public static void main(String[] args) {
        SpringApplication.run(BingeBuddiesApplication.class, args);
    }

}
