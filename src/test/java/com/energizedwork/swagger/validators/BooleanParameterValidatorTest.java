package com.energizedwork.swagger.validators;

import com.energizedwork.swagger.ValidationError;
import io.swagger.models.parameters.AbstractSerializableParameter;
import org.junit.Test;
import testsupport.TestUtils;

import java.util.List;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static testsupport.CustomMatchers.validationError;

public class BooleanParameterValidatorTest {

    private AbstractSerializableParameter<? extends AbstractSerializableParameter<?>> param;

    @Test
    public void reports_no_error_if_value_is_boolean() {
        param = TestUtils.createParameterDef("boolean", null);
        param.setName("myBoolean");

        List<ValidationError> errors = TestUtils.validator(param).validateParam(param, "true");

        assertThat(errors.size(), is(0));
    }

    @Test
    public void reports_error_if_value_not_boolean() {
        param = TestUtils.createParameterDef("boolean", null);
        param.setName("myBoolean");

        List<ValidationError> errors = TestUtils.validator(param).validateParam(param, "not a boolean!");

        assertThat(errors.size(), is(1));
        assertThat(errors, hasItem(validationError("myBoolean", "string \"not a boolean!\" is not a valid boolean")));
    }
}
