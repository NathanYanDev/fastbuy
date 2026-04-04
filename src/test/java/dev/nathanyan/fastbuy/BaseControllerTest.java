package dev.nathanyan.fastbuy;

import dev.nathanyan.fastbuy.security.JwtService;
import dev.nathanyan.fastbuy.security.OAuth2SuccessHandler;
import dev.nathanyan.fastbuy.security.OAuth2UserServiceImpl;
import dev.nathanyan.fastbuy.security.UserDetailsServiceImpl;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@WebMvcTest
public abstract class BaseControllerTest {

  @MockitoBean protected JwtService jwtService;
  @MockitoBean protected UserDetailsServiceImpl userDetailsService;
  @MockitoBean protected OAuth2UserServiceImpl oAuth2UserService;
  @MockitoBean protected OAuth2SuccessHandler oAuth2SuccessHandler;
  @MockitoBean protected AuthenticationProvider authenticationProvider;
}
