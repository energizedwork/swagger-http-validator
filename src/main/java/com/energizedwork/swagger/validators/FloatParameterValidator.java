package com.energizedwork.swagger.validators;

public class FloatParameterValidator extends NumberParameterValidator {

    @Override
    protected Number convert(String value) throws NumberFormatException {
        return Float.valueOf(value);
    }
}
