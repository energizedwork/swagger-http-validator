package com.energizedwork.swagger;

import com.energizedwork.swagger.parameters.ParameterSource;
import io.swagger.models.parameters.Parameter;

public class ValidationError {

    private final ParameterSource parameterSource;
    private final String fieldPath;
    private final String errorMessage;

    public static ValidationError bodyError(String fieldPath, String errorMessage) {
        return new ValidationError(ParameterSource.body, fieldPath, errorMessage);
    }

    public static ValidationError forParameter(Parameter parameterDef, String errorMessage) {
        return new ValidationError(ParameterSource.from(parameterDef), parameterDef.getName(), errorMessage);
    }

    public ValidationError(ParameterSource parameterSource, String fieldPath, String errorMessage) {
        this.parameterSource = parameterSource;
        this.fieldPath = fieldPath;
        this.errorMessage = errorMessage;
    }

    public ParameterSource getParameterSource() {
        return parameterSource;
    }

    public String getFieldPath() {
        return fieldPath;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public String toString() {
        return "Parameter " + fieldPath + " of type '" + parameterSource + "' failed validation. Reason: " + errorMessage;
    }
}
