package dev.nathanyan.fastbuy.shared.validation.annotation;

import dev.nathanyan.fastbuy.shared.validation.validator.PhoneNumberValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = {PhoneNumberValidator.class})
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface PhoneNumber {
  String message() default "Invalid Phone Number";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
