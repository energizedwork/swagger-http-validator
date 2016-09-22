package com.energizedwork.swagger.validators;

import com.energizedwork.swagger.ValidationError;
import com.google.common.collect.ImmutableList;
import io.swagger.models.parameters.AbstractSerializableParameter;
import com.energizedwork.swagger.utils.Messages;
import com.energizedwork.swagger.parameters.ParameterSource;

import java.util.List;

public class StringParameterValidator extends BaseParameterValidator {

    @Override
    public <T extends AbstractSerializableParameter<T>> List<ValidationError> validate(AbstractSerializableParameter<T> parameterDef, String value) {
        ImmutableList.Builder<ValidationError> errorsBuilder = ImmutableList.builder();
        ParameterSource parameterSource = ParameterSource.valueOf(parameterDef.getIn());
        String key = parameterDef.getName();

        if (parameterDef.getPattern() != null && !value.matches(parameterDef.getPattern())) {
            errorsBuilder.add(new ValidationError(parameterSource,
                key,
                Messages.validationMessage("err.common.pattern.noMatch", parameterDef.getPattern(), value))
            );
        }

        if (parameterDef.getMinLength() != null && value.length() < parameterDef.getMinLength()) {
            errorsBuilder.add(new ValidationError(parameterSource,
                key,
                Messages.validationMessage("err.common.minLength.tooShort", value, value.length(), parameterDef.getMinLength()))
            );
        }

        if (parameterDef.getMaxLength() != null && value.length() > parameterDef.getMaxLength()) {
            errorsBuilder.add(new ValidationError(parameterSource,
                key,
                Messages.validationMessage("err.common.maxLength.tooLong", value, value.length(), parameterDef.getMaxLength()))
            );
        }

        return errorsBuilder.build();
    }
}
