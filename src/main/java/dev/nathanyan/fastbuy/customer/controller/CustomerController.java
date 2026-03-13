package dev.nathanyan.fastbuy.customer.controller;

import dev.nathanyan.fastbuy.customer.dto.CustomerResponse;
import dev.nathanyan.fastbuy.customer.service.CustomerService;
import dev.nathanyan.fastbuy.security.ApiConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping(ApiConstants.CUSTOMER_PREFIX)
public class CustomerController {
  private final CustomerService customerService;

  @GetMapping("/me")
  public ResponseEntity<CustomerResponse> getCustomer(
      @RequestHeader("Authorization") String authHeader) {
    String token = authHeader.substring(7);
    CustomerResponse response = customerService.getCustomer(token);

    return ResponseEntity.ok(response);
  }
}
