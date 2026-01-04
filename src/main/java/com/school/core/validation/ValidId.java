package com.school.core.validation;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidId.IdValidator.class)
public @interface ValidId {
    String message() default "ID inválido";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    class IdValidator implements ConstraintValidator<ValidId, String> {
        @Override
        public boolean isValid(String value, ConstraintValidatorContext context) {
            if (value == null || "not-set".equals(value) || "null".equals(value) || value.trim().isEmpty()) {
                return false;
            }
            try {
                long id = Long.parseLong(value);
                return id > 0; // IDs deben ser positivos
            } catch (NumberFormatException e) {
                return false;
            }
        }
    }
}