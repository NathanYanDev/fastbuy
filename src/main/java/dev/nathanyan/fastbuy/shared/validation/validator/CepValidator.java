package dev.nathanyan.fastbuy.shared.validation.validator;

import dev.nathanyan.fastbuy.shared.validation.annotation.CEP;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CepValidator implements ConstraintValidator<CEP, String> {

  @Override
  public void initialize(CEP constraintAnnotation) {
    ConstraintValidator.super.initialize(constraintAnnotation);
  }

  @Override
  public boolean isValid(String cep, ConstraintValidatorContext context) {
    if (cep == null || cep.isEmpty()) {
      return true;
    }

    return validateCep(cep);
  }

  private boolean validateCep(String cep) {
    Pattern pattern =
        Pattern.compile(
            "^(([0-9]{2}\\.[0-9]{3}-[0-9]{3})|([0-9]{2}[0-9]{3}-[0-9]{3})|([0-9]{8}))$");
    Matcher matcher = pattern.matcher(cep);

    return matcher.find();
  }
}
