package dev.nathanyan.fastbuy.shared.validation.annotation;

import dev.nathanyan.fastbuy.shared.validation.validator.CpfValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = CpfValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CPF {
  String message() default "Invalid CPF";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
