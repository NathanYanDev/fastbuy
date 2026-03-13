package dev.nathanyan.fastbuy.auth.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import dev.nathanyan.fastbuy.shared.dto.address.AddressDTO;
import dev.nathanyan.fastbuy.shared.validation.annotation.CPF;
import dev.nathanyan.fastbuy.shared.validation.annotation.PhoneNumber;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

public record RegisterRequest(
    @Email(message = "Invalid email format") @NotBlank(message = "Email is required")
        String username,
    @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must be at least 8 characters long")
        String password,
    @NotBlank(message = "Name is required")
        @Size(min = 2, message = "Name must be at least 2 characters long")
        String name,
    @NotBlank(message = "Document is required") @CPF String document,
    @NotBlank @PhoneNumber String phone,
    @NotNull
        @JsonDeserialize(using = LocalDateDeserializer.class)
        @JsonSerialize(using = LocalDateSerializer.class)
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate birthDate,
    @Valid List<AddressDTO> addresses)
    implements Serializable {
  @Serial private static final long serialVersionUID = 1002L;
}
