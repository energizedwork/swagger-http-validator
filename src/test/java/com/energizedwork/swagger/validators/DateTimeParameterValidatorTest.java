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

public class DateTimeParameterValidatorTest {

    private AbstractSerializableParameter<? extends AbstractSerializableParameter<?>> param =
        createParameterDef("string", "date-time");

    @Test
    public void reports_error_when_not_a_valid_ISO_date_time() {
        List<ValidationError> errors = validator(param).validateParam(param, "2016--22T123");

        assertThat(errors.size(), is(1));
        assertThat(errors, hasItem(validationError("myParam", "string \"2016--22T123\" is invalid against requested date format(s) yyyy-MM-ddTHH:mm:ss.SSS")));
    }

    @Test
    public void reports_no_error_when_a_valid_ISO_date_time() {
        List<ValidationError> errors = validator(param).validateParam(param, "2016-09-22T10:17:01");

        assertThat(errors.size(), is(0));
    }

    @Test
    public void reports_no_error_when_a_valid_ISO_date_time_with_milliseconds() {
        List<ValidationError> errors = validator(param).validateParam(param, "2016-09-22T10:17:01.321");

        assertThat(errors.size(), is(0));
    }

    @Test
    public void reports_no_error_when_a_valid_zoned_ISO_date_time() {
        List<ValidationError> errors = validator(param).validateParam(param, "2016-09-22T10:17:01.321+01:00");

        assertThat(errors.size(), is(0));
    }

    @Test
    public void reports_no_error_when_a_valid_UTC_ISO_date_time() {
        List<ValidationError> errors = validator(param).validateParam(param, "2016-09-22T10:17:01.321Z");

        assertThat(errors.size(), is(0));
    }
}
