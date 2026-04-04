package dev.nathanyan.fastbuy.config;

import dev.nathanyan.fastbuy.shared.entity.CartEntity;
import dev.nathanyan.fastbuy.shared.entity.CustomerEntity;
import dev.nathanyan.fastbuy.shared.entity.enums.UserRole;
import dev.nathanyan.fastbuy.shared.repository.CartRepository;
import dev.nathanyan.fastbuy.shared.repository.CustomerRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminSeeder implements ApplicationRunner {
  private final CustomerRepository customerRepository;
  private final PasswordEncoder passwordEncoder;
  private final CartRepository cartRepository;

  @Value("${admin.email}")
  private String adminEmail;

  @Value("${admin.password}")
  private String adminPassword;

  @Override
  public void run(ApplicationArguments args) throws Exception {
    if (customerRepository.findByEmail(adminEmail).isPresent()) return;

    CustomerEntity admin =
        CustomerEntity.builder()
            .email(adminEmail)
            .password(passwordEncoder.encode(adminPassword))
            .name("Admin")
            .role(UserRole.ADMIN)
            .document("123.123.123-12")
            .phone("(11) 99999-9999")
            .birthDate(LocalDate.now())
            .addresses(List.of())
            .build();

    CustomerEntity savedAdmin = customerRepository.save(admin);

    cartRepository.save(CartEntity.builder().customer(savedAdmin).build());
  }
}
