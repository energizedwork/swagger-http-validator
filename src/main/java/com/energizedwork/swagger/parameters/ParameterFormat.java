package com.energizedwork.swagger.parameters;

import com.energizedwork.swagger.validators.ParameterValidator;
import com.energizedwork.swagger.validators.*;
import io.swagger.models.parameters.AbstractSerializableParameter;

import static com.google.common.base.MoreObjects.firstNonNull;

public enum ParameterFormat {

    INT32(ParameterType.NUMBER, new Int32ParameterValidator()),
    INT64(ParameterType.NUMBER, new Int64ParameterValidator()),
    FLOAT(ParameterType.NUMBER, new FloatParameterValidator()),
    DOUBLE(ParameterType.NUMBER, new DoubleParameterValidator()),
    STRING(ParameterType.STRING, new StringParameterValidator()),
    NUMBER(ParameterType.STRING, new DoubleParameterValidator()),
    BYTE(ParameterType.STRING, new StringParameterValidator()),
    BINARY(ParameterType.STRING, new StringParameterValidator()),
    BOOLEAN(ParameterType.BOOLEAN, new BooleanParameterValidator()),
    DATE(ParameterType.STRING, new DateParameterValidator()),
    DATE_TIME(ParameterType.STRING, new DateTimeParameterValidator()),
    PASSWORD(ParameterType.STRING, new StringParameterValidator());

    private final ParameterType type;
    private final ParameterValidator validator;

    ParameterFormat(ParameterType type, ParameterValidator validator) {
        this.type = type;
        this.validator = validator;
    }

    public ParameterValidator getValidator() {
        return validator;
    }

    public static ParameterFormat fromParameterDef(AbstractSerializableParameter<? extends AbstractSerializableParameter<?>> parameter) {
        String format =
            firstNonNull(parameter.getFormat(), parameter.getType())
            .replace('-', '_');
        try {
            return valueOf(format.toUpperCase());
        } catch (IllegalArgumentException e) {
            return valueOf(parameter.getType().toUpperCase());
        }
    }
}
