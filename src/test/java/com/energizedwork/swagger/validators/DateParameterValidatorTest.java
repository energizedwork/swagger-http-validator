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

public class DateParameterValidatorTest {

    private AbstractSerializableParameter<? extends AbstractSerializableParameter<?>> param =
        createParameterDef("string", "date");

    @Test
    public void reports_error_when_not_a_valid_ISO_date() {
        List<ValidationError> errors = validator(param).validateParam(param, "2016-typo-22");

        assertThat(errors.size(), is(1));
        assertThat(errors, hasItem(validationError("myParam", "string \"2016-typo-22\" is invalid against requested date format(s) yyyy-MM-dd")));
    }

    @Test
    public void reports_no_error_when_a_valid_ISO_date() {
        List<ValidationError> errors = validator(param).validateParam(param, "2016-09-22");

        assertThat(errors.size(), is(0));
    }
}
