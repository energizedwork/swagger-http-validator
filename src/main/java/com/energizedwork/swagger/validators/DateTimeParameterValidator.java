package com.energizedwork.swagger.validators;

import java.time.format.DateTimeFormatter;

public class DateTimeParameterValidator extends BaseDateParameterValidator {

    @Override
    protected String getFormatString() {
        return "yyyy-MM-ddTHH:mm:ss.SSS";
    }

    @Override
    protected DateTimeFormatter getFormatter() {
        return DateTimeFormatter.ISO_DATE_TIME;
    }
}
