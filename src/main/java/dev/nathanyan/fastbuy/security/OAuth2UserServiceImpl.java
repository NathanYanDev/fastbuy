package dev.nathanyan.fastbuy.security;

import dev.nathanyan.fastbuy.shared.entity.CartEntity;
import dev.nathanyan.fastbuy.shared.entity.CustomerEntity;
import dev.nathanyan.fastbuy.shared.entity.enums.UserRole;
import dev.nathanyan.fastbuy.shared.repository.CartRepository;
import dev.nathanyan.fastbuy.shared.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OAuth2UserServiceImpl extends DefaultOAuth2UserService {

  private final CustomerRepository customerRepository;
  private final CartRepository cartRepository;

  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    OAuth2User oAuth2User = super.loadUser(userRequest);

    String email = oAuth2User.getAttribute("email");
    String name = oAuth2User.getAttribute("name");

    CustomerEntity customer = customerRepository.findByEmail(email)
        .orElseGet(() -> createNewCustomer(email, name));

    return oAuth2User;
  }

  private CustomerEntity createNewCustomer(String email, String name) {
    CustomerEntity customer = CustomerEntity.builder()
        .email(email)
        .name(name)
        .role(UserRole.CUSTOMER)
        .build();

    CustomerEntity savedUser = customerRepository.save(customer);

    CartEntity cart = CartEntity.builder()
        .customer(customer)
        .build();

    cartRepository.save(cart);

    return savedUser;
  }
}