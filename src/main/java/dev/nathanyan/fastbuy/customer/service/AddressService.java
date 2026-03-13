package dev.nathanyan.fastbuy.customer.service;

import dev.nathanyan.fastbuy.shared.client.BrasilApiClient;
import dev.nathanyan.fastbuy.shared.client.dto.BrasilApiCepResponse;
import dev.nathanyan.fastbuy.shared.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AddressService {
  private final BrasilApiClient brasilApiClient;
  private final AddressRepository addressRepository;

  public BrasilApiCepResponse findAddressByCep(String cep) {
    return brasilApiClient.findAddressByCep(cep);
  }
}
