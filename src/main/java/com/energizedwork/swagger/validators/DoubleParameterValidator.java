package com.energizedwork.swagger.validators;

public class DoubleParameterValidator extends NumberParameterValidator {
    @Override
    protected Number convert(String value) throws NumberFormatException {
        return Double.valueOf(value);
    }
}
