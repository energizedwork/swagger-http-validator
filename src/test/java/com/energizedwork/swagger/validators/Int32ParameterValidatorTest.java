package com.energizedwork.swagger.validators;

import com.energizedwork.swagger.ValidationError;
import io.swagger.models.parameters.AbstractSerializableParameter;
import io.swagger.models.parameters.PathParameter;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static testsupport.CustomMatchers.validationError;
import static testsupport.TestUtils.validator;

public class Int32ParameterValidatorTest {

    private AbstractSerializableParameter<? extends AbstractSerializableParameter<?>> param;

    @Before
    public void init() {
        param = param();
    }

    @Test
    public void reports_no_error_when_all_constraints_met() {
        param.setMaximum(99d);
        param.setMinimum(50d);

        List<ValidationError> errors = validator(param).validateParam(param, "77");

        assertThat(
            "Expected no errors. Got: " + errors,
            errors.size(), is(0)
        );
    }

    @Test
    public void reports_error_when_value_is_not_a_number() {
        List<ValidationError> errors = validator(param).validateParam(param, "not_a_number");

        assertThat(errors.size(), is(1));
        assertThat(errors, hasItem(validationError("myParam", "input string \"not_a_number\" is not a valid int32")));
    }

    @Test
    public void reports_error_when_value_exceeds_non_exclusive_max() {
        param.setMaximum(99d);
        param.setExclusiveMaximum(false);

        List<ValidationError> errors = validator(param).validateParam(param, "100");

        assertThat(errors.size(), is(1));
        assertThat(errors, hasItem(validationError("myParam", "numeric instance is not strictly lower than the required maximum 99.0")));
    }

    @Test
    public void reports_error_when_value_exceeds_exclusive_max() {
        param.setMaximum(99d);
        param.setExclusiveMaximum(true);

        List<ValidationError> errors = validator(param).validateParam(param, "99");

        assertThat(errors.size(), is(1));
        assertThat(errors, hasItem(validationError("myParam", "numeric instance is greater than the required maximum (maximum: 99.0, found: 99.0)")));
    }

    @Test
    public void reports_no_error_when_value_at_non_exclusive_max() {
        param.setMaximum(99d);
        param.setExclusiveMaximum(false);

        List<ValidationError> errors = validator(param).validateParam(param, "99");

        assertThat(errors.size(), is(0));
    }

    @Test
    public void reports_no_error_when_value_is_slightly_below_exclusive_max() {
        param.setMaximum(99d);
        param.setExclusiveMaximum(true);

        List<ValidationError> errors = validator(param).validateParam(param, "98");

        assertThat(errors.size(), is(0));
    }

    @Test
    public void reports_error_when_value_is_below_non_exclusive_min() {
        param.setMinimum(20d);
        param.setExclusiveMinimum(false);

        List<ValidationError> errors = validator(param).validateParam(param, "19");

        assertThat(errors.size(), is(1));
        assertThat(errors, hasItem(validationError("myParam", "numeric instance is not strictly greater than the required minimum 20.0")));
    }

    @Test
    public void reports_no_error_when_value_is_at_non_exclusive_min() {
        param.setMinimum(20d);
        param.setExclusiveMinimum(false);

        List<ValidationError> errors = validator(param).validateParam(param, "20");

        assertThat(errors.size(), is(0));
    }

    @Test
    public void reports_no_error_when_value_is_slightly_over_exclusive_min() {
        param.setMinimum(20d);
        param.setExclusiveMinimum(true);

        List<ValidationError> errors = validator(param).validateParam(param, "21");

        assertThat(errors.size(), is(0));
    }

    @Test
    public void reports_error_when_value_is_below_exclusive_min() {
        param.setMinimum(20d);
        param.setExclusiveMinimum(true);

        List<ValidationError> errors = validator(param).validateParam(param, "20");

        assertThat(errors.size(), is(1));
        assertThat(errors, hasItem(validationError("myParam", "numeric instance is lower than the required minimum (minimum: 20.0, found: 20.0)")));
    }

    private static AbstractSerializableParameter<? extends AbstractSerializableParameter<?>> param() {
        PathParameter param = new PathParameter();
        param.setType("integer");
        param.setFormat("int32");
        param.setName("myParam");
        return param;
    }
}
