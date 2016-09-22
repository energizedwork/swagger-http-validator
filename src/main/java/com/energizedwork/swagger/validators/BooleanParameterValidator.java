package com.energizedwork.swagger.validators;

import com.energizedwork.swagger.ValidationError;
import io.swagger.models.parameters.AbstractSerializableParameter;

import java.util.Collections;
import java.util.List;

import static java.util.Collections.emptyList;

public class BooleanParameterValidator extends BaseParameterValidator {

    @Override
    public <T extends AbstractSerializableParameter<T>> List<ValidationError> validate(AbstractSerializableParameter<T> parameterDef, String value) {
        if (!value.equals("true") && !value.equals("false")) {
            return Collections.singletonList(
                ValidationError.forParameter(parameterDef, String.format("string \"%s\" is not a valid boolean", value))
            );
        }

        return emptyList();
    }
}
