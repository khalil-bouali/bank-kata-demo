package com.kbouali.demo;

import com.kbouali.demo.repository.AccountRepository;
import com.kbouali.demo.service.AuthenticationService;
import com.kbouali.demo.dto.request.RegisterRequest;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import static com.kbouali.demo.util.enums.Role.*;

@SpringBootApplication
public class SkypayTechnicalTestBankKataApplication {

    public static void main(String[] args) {
        SpringApplication.run(SkypayTechnicalTestBankKataApplication.class, args);
    }

    @Bean
    CommandLineRunner commandLineRunner(AuthenticationService service,
                                        PasswordEncoder passwordEncoder) {
        return args -> {
            RegisterRequest admin = RegisterRequest.builder()
                    .firstname("Admin")
                    .lastname("Admin")
                    .cin("AA000000")
                    .phoneNumber("0000000000")
                    .password(passwordEncoder.encode("password"))
                    .role(ADMIN)
                    .build();
            System.out.println("Admin token: " + service.register(admin).getAccessToken());

            RegisterRequest manager = RegisterRequest.builder()
                    .firstname("Manager")
                    .lastname("Manager")
                    .cin("MM000000")
                    .password(passwordEncoder.encode("password"))
                    .phoneNumber("0000000001")
                    .role(MANAGER)
                    .build();
            System.out.println("Manager token: " + service.register(manager).getAccessToken());
        };
    }

}
