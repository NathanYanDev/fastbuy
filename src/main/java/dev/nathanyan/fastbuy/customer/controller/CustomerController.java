package dev.nathanyan.fastbuy.customer.controller;

import dev.nathanyan.fastbuy.customer.dto.CustomerResponse;
import dev.nathanyan.fastbuy.customer.dto.UpdateCustomerRequest;
import dev.nathanyan.fastbuy.customer.dto.UpdateEmailRequest;
import dev.nathanyan.fastbuy.customer.dto.UpdatePasswordRequest;
import dev.nathanyan.fastbuy.customer.service.CustomerService;
import dev.nathanyan.fastbuy.security.ApiConstants;
import dev.nathanyan.fastbuy.shared.dto.address.AddressRequest;
import dev.nathanyan.fastbuy.shared.dto.address.AddressResponse;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping(ApiConstants.CUSTOMER_PREFIX)
public class CustomerController {
  private final CustomerService customerService;

  @GetMapping("/me")
  public ResponseEntity<CustomerResponse> getCustomer(Authentication authentication) {
    return ResponseEntity.ok(customerService.getCustomer(authentication.getName()));
  }

  @PutMapping("/me")
  public ResponseEntity<CustomerResponse> updateCustomer(
      Authentication authentication, @RequestBody @Valid UpdateCustomerRequest request) {
    return ResponseEntity.ok(customerService.updateCustomer(authentication.getName(), request));
  }

  @PatchMapping("/me/email")
  public ResponseEntity<CustomerResponse> updateEmail(
      Authentication authentication, @RequestBody @Valid UpdateEmailRequest request) {
    return ResponseEntity.ok(
        customerService.updateEmail(authentication.getName(), request.email()));
  }

  @PatchMapping("/me/password")
  public ResponseEntity<CustomerResponse> updatePassword(
      Authentication authentication, @RequestBody @Valid UpdatePasswordRequest request) {
    return ResponseEntity.ok(customerService.updatePassword(authentication.getName(), request));
  }

  @PostMapping("/me/addresses")
  public ResponseEntity<AddressResponse> addAddress(
      Authentication authentication, @RequestBody @Valid AddressRequest request) {
    AddressResponse response = customerService.addAddress(authentication.getName(), request);
    return ResponseEntity.created(
            URI.create(ApiConstants.CUSTOMER_PREFIX + "/me/addresses/" + response.id()))
        .body(response);
  }

  @PutMapping("/me/addresses/{addressId}")
  public ResponseEntity<CustomerResponse> updateAddress(
      Authentication authentication,
      @PathVariable String addressId,
      @RequestBody @Valid AddressRequest request) {
    return ResponseEntity.ok(
        customerService.updateAddress(authentication.getName(), addressId, request));
  }

  @DeleteMapping("/me/addresses/{addressId}")
  public ResponseEntity<CustomerResponse> deleteAddress(
      Authentication authentication, @PathVariable String addressId) {
    customerService.deleteAddress(authentication.getName(), addressId);
    return ResponseEntity.noContent().build();
  }

  @PatchMapping("/me/addresses/{addressId}")
  public ResponseEntity<CustomerResponse> setDefaultAddress(
      Authentication authentication, @PathVariable String addressId) {
    return ResponseEntity.ok(
        customerService.setDefaultAddress(authentication.getName(), addressId));
  }
}
