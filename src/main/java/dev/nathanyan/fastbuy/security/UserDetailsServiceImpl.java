package dev.nathanyan.fastbuy.security;

import dev.nathanyan.fastbuy.shared.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

  private final CustomerRepository customerRepository;

  @NullMarked
  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return customerRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
  }
}
