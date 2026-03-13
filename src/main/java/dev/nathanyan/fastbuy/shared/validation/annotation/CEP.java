package dev.nathanyan.fastbuy.shared.validation.annotation;

import dev.nathanyan.fastbuy.shared.validation.validator.CepValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = {CepValidator.class})
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface CEP {
  String message() default "Invalid Zip Code";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
