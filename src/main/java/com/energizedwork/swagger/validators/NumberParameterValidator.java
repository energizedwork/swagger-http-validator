package com.energizedwork.swagger.validators;

import com.energizedwork.swagger.ValidationError;
import com.google.common.collect.ImmutableList;
import io.swagger.models.parameters.AbstractSerializableParameter;

import java.util.List;

import static com.google.common.base.MoreObjects.firstNonNull;
import static java.util.Collections.max;
import static com.energizedwork.swagger.utils.Messages.validationMessage;

public abstract class NumberParameterValidator<N extends Number> extends BaseParameterValidator {

    @Override
    public <T extends AbstractSerializableParameter<T>> List<ValidationError> validate(AbstractSerializableParameter<T> parameterDef, String value) {
        ImmutableList.Builder<ValidationError> errorsBuilder = ImmutableList.builder();

        try {
            N convertedValue = convert(value);
            errorsBuilder.addAll(validateRange(parameterDef, convertedValue.doubleValue()));
        } catch (NumberFormatException ce) {
            errorsBuilder.add(
                ValidationError.forParameter(parameterDef, "input string \"" + value + "\" is not a valid " + parameterDef.getFormat())
            );
        }

        return errorsBuilder.build();
    }

    protected abstract N convert(String value) throws NumberFormatException;

    private <T extends AbstractSerializableParameter<T>> List<ValidationError> validateRange(AbstractSerializableParameter<T> parameterDef, double value) {
        ImmutableList.Builder<ValidationError> errorsBuilder = ImmutableList.builder();

        Double maximum = parameterDef.getMaximum();
        boolean exclusiveMaximum = firstNonNull(parameterDef.isExclusiveMaximum(), false);
        Double minimum = parameterDef.getMinimum();
        boolean exclusiveMinimum = firstNonNull(parameterDef.isExclusiveMinimum(), false);

        if (maximum != null) {
            if (!exclusiveMaximum && value > maximum) {
                errorsBuilder.add(ValidationError.forParameter(parameterDef,
                    validationMessage("err.common.maximum.notExclusive", maximum, value))
                );
            } else if (exclusiveMaximum && value >= maximum) {
                errorsBuilder.add(ValidationError.forParameter(parameterDef,
                    validationMessage("err.common.maximum.tooLarge", maximum, value))
                );
            }
        }

        if (minimum != null) {
            if (!exclusiveMinimum && value < minimum) {
                errorsBuilder.add(ValidationError.forParameter(parameterDef,
                    validationMessage("err.common.minimum.notExclusive", minimum, value))
                );
            } else if (exclusiveMinimum && value <= minimum) {
                errorsBuilder.add(ValidationError.forParameter(parameterDef,
                    validationMessage("err.common.minimum.tooSmall", minimum, value))
                );
            }
        }


        return errorsBuilder.build();
    }
}
