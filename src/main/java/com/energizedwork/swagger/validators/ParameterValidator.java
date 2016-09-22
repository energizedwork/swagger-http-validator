package com.energizedwork.swagger.validators;

import com.energizedwork.swagger.ValidationError;
import io.swagger.models.parameters.AbstractSerializableParameter;

import java.util.List;

public interface ParameterValidator {

    <T extends AbstractSerializableParameter<T>> List<ValidationError> validateParam(AbstractSerializableParameter<?> parameterDef, String value);
}
