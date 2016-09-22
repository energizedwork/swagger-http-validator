package com.energizedwork.swagger.validators;

public class Int32ParameterValidator extends NumberParameterValidator {

    @Override
    protected Number convert(String value) {
        return Integer.valueOf(value);
    }
}
