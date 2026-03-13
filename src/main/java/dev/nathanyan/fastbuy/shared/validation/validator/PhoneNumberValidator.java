package dev.nathanyan.fastbuy.shared.validation.validator;

import dev.nathanyan.fastbuy.shared.validation.annotation.PhoneNumber;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class PhoneNumberValidator implements ConstraintValidator<PhoneNumber, String> {

  // Aceita os formatos:
  // (11) 99999-9999 → celular com DDD e espaço
  // (11) 9999-9999  → fixo com DDD e espaço
  // 11999999999     → celular sem formatação
  // 1199999999      → fixo sem formatação
  private static final Pattern PHONE_PATTERN =
      Pattern.compile("^\\(?\\d{2}\\)?[\\s]?\\d{4,5}-?\\d{4}$");

  private static Boolean validatePhoneNumber(String phone) {
    String sanitized = phone.replaceAll("[()\\s-]", "");

    if (!sanitized.matches("\\d+")) {
      return false;
    }

    if (sanitized.length() != 10 && sanitized.length() != 11) {
      return false;
    }

    if (sanitized.length() == 11 && sanitized.charAt(2) != '9') {
      return false;
    }

    return PHONE_PATTERN.matcher(phone).matches();
  }

  @Override
  public void initialize(PhoneNumber constraintAnnotation) {
    ConstraintValidator.super.initialize(constraintAnnotation);
  }

  @Override
  public boolean isValid(String phone, ConstraintValidatorContext context) {
    if (phone == null || phone.isEmpty()) return true;

    return validatePhoneNumber(phone);
  }
}
