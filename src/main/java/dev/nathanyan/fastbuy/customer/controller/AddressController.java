package dev.nathanyan.fastbuy.customer.controller;

import dev.nathanyan.fastbuy.customer.service.AddressService;
import dev.nathanyan.fastbuy.security.ApiConstants;
import dev.nathanyan.fastbuy.shared.client.dto.BrasilApiCepResponse;
import dev.nathanyan.fastbuy.shared.validation.annotation.CEP;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiConstants.CUSTOMER_PREFIX)
public class AddressController {
  private final AddressService addressService;

  @GetMapping("/address/cep/{cep}")
  public ResponseEntity<BrasilApiCepResponse> getAddressByCep(@PathVariable @CEP String cep) {
    return ResponseEntity.ok(addressService.findAddressByCep(cep));
  }
}
