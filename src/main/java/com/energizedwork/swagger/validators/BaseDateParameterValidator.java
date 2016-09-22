package com.energizedwork.swagger.validators;

import com.energizedwork.swagger.utils.Messages;
import com.energizedwork.swagger.ValidationError;
import io.swagger.models.parameters.AbstractSerializableParameter;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

public abstract class BaseDateParameterValidator implements ParameterValidator {

    @Override
    public <T extends AbstractSerializableParameter<T>> List<ValidationError> validateParam(AbstractSerializableParameter<?> parameterDef, String value) {
        DateTimeFormatter dateFormatter = getFormatter();
        try {
            dateFormatter.parse(value);
        } catch (DateTimeParseException e) {
            return singletonList(
                ValidationError.forParameter(parameterDef, Messages.validationMessage("err.format.invalidDate", value, getFormatString()))
            );
        }

        return emptyList();
    }

    protected abstract String getFormatString();
    protected abstract DateTimeFormatter getFormatter();
}
