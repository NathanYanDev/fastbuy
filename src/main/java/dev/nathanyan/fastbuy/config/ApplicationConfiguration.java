package dev.nathanyan.fastbuy.config;

import dev.nathanyan.fastbuy.shared.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.client.RestClient;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfiguration {
  private final CustomerRepository customerRepository;

  @Bean
  public RestClient restClient() {
    return RestClient.builder().baseUrl("https://brasilapi.com.br/api").build();
  }

  @Bean
  public UserDetailsService userDetailsService() {
    return username ->
        customerRepository
            .findByEmail(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
  }

  @Bean
  public BCryptPasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
