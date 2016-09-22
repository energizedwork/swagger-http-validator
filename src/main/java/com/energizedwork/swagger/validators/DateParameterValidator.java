package com.energizedwork.swagger.validators;

import java.time.format.DateTimeFormatter;

public class DateParameterValidator extends BaseDateParameterValidator {

    @Override
    protected String getFormatString() {
        return "yyyy-MM-dd";
    }

    @Override
    protected DateTimeFormatter getFormatter() {
        return DateTimeFormatter.ISO_DATE;
    }
}
