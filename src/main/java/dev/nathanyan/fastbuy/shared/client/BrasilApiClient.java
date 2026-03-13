package dev.nathanyan.fastbuy.shared.client;

import dev.nathanyan.fastbuy.shared.client.dto.BrasilApiCepResponse;
import dev.nathanyan.fastbuy.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@RequiredArgsConstructor
@Component
public class BrasilApiClient {

  private final RestClient restClient;

  public BrasilApiCepResponse findAddressByCep(String cep) {
    String sanitizedCep = cep.replaceAll("[^0-9]", "");

    try {
      return restClient
          .get()
          .uri("/cep/v1/" + sanitizedCep)
          .retrieve()
          .body(BrasilApiCepResponse.class);
    } catch (HttpClientErrorException.NotFound e) {
      throw new ResourceNotFoundException("Cep not found");
    }
  }
}
