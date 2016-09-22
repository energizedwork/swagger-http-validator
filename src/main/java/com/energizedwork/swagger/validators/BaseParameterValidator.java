package com.energizedwork.swagger.validators;

import com.energizedwork.swagger.utils.Messages;
import com.energizedwork.swagger.ValidationError;
import com.google.common.collect.ImmutableList;
import io.swagger.models.parameters.AbstractSerializableParameter;

import java.util.Collections;
import java.util.List;

import static java.util.Collections.emptyList;

public abstract class BaseParameterValidator implements ParameterValidator {

    @Override
    public <T extends AbstractSerializableParameter<T>> List<ValidationError> validateParam(AbstractSerializableParameter<?> parameterDef, String value) {
        ImmutableList.Builder<ValidationError> errorsBuilder = ImmutableList.builder();

        errorsBuilder.addAll(validateGenericRules(parameterDef, value));
        errorsBuilder.addAll(validate(parameterDef, value));

        return errorsBuilder.build();
    }

    public abstract <T extends AbstractSerializableParameter<T>> List<ValidationError> validate(AbstractSerializableParameter<T> parameterDef, String value);

    private <T extends AbstractSerializableParameter<T>> List<ValidationError> validateGenericRules(AbstractSerializableParameter<T> parameterDef, String value) {
        if (parameterDef.getEnum() != null && !parameterDef.getEnum().contains(value)) {
            return Collections.singletonList(
                ValidationError.forParameter(parameterDef, Messages.validationMessage("err.common.enum.notInEnum", value, parameterDef.getEnum()))
            );
        }

        return emptyList();
    }
}
