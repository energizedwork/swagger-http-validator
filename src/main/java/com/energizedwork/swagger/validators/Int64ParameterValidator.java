package com.energizedwork.swagger.validators;

public class Int64ParameterValidator extends NumberParameterValidator {

    @Override
    protected Number convert(String value) throws NumberFormatException {
        return Long.valueOf(value);
    }
}
