package dev.nathanyan.fastbuy.shared.client.dto;

import java.io.Serial;
import java.io.Serializable;

public record BrasilApiCepResponse(
    String cep, String state, String city, String neighborhood, String street)
    implements Serializable {
  @Serial private static final long serialVersionUID = 1002L;
}
