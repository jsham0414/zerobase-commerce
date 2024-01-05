package com.zerobase.commerce.api.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EnumCheckValidator implements ConstraintValidator<EnumCheck, Object> {
    private Class<? extends Enum<?>> enumClass;

    public void initialize(EnumCheck constraintAnnotation) {
        this.enumClass = constraintAnnotation.check();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        Enum<?>[] enums = enumClass.getEnumConstants();
        for (Enum<?> constant : enums) {
            if (constant.name().equals(String.valueOf(value))) {
                return true;
            }
        }

        return false;
    }

}
