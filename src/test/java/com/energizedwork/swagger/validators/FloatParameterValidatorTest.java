package com.energizedwork.swagger.validators;

import com.energizedwork.swagger.ValidationError;
import io.swagger.models.parameters.AbstractSerializableParameter;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static testsupport.CustomMatchers.validationError;
import static testsupport.TestUtils.createParameterDef;
import static testsupport.TestUtils.validator;

public class FloatParameterValidatorTest {

    private AbstractSerializableParameter<? extends AbstractSerializableParameter<?>> param;

    @Test
    public void reports_error_when_value_is_not_a_number() {
        param = createParameterDef("number", "float");

        List<ValidationError> errors = validator(param).validateParam(param, "not_a_number");

        assertThat(errors.size(), is(1));
        assertThat(errors, hasItem(validationError("myParam", "input string \"not_a_number\" is not a valid float")));
    }



}
