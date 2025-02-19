package com.nch.cryptrader.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = MinAssetAmountValidator.class)
@Target({FIELD, PARAMETER})
@Retention(RUNTIME)
@Documented
public @interface ValidAssetAmount {
    String message() default "Min allowed amount is 10^-8 (0.00000001)";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
