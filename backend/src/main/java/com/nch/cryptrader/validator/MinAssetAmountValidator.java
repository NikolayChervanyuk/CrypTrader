package com.nch.cryptrader.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.constraintvalidation.SupportedValidationTarget;
import jakarta.validation.constraintvalidation.ValidationTarget;

@SupportedValidationTarget(ValidationTarget.ANNOTATED_ELEMENT)
public class MinAssetAmountValidator implements ConstraintValidator<ValidAssetAmount, Double> {

    @Override
    public boolean isValid(Double value, ConstraintValidatorContext context) {
        return value >= .00000001;
    }
}
