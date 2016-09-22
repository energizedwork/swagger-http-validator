package com.energizedwork.swagger.validators;

import com.energizedwork.swagger.ValidationError;
import io.swagger.models.parameters.AbstractSerializableParameter;
import io.swagger.models.parameters.QueryParameter;
import org.junit.Before;
import org.junit.Test;
import testsupport.TestUtils;

import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static testsupport.CustomMatchers.validationError;

public class StringParameterValidatorTest {

    private AbstractSerializableParameter<? extends AbstractSerializableParameter<?>> param;

    @Before
    public void init() {
        param = param();
    }

    @Test
    public void reportsNoErrorsWhenAllConstraintsMet() {
        param.setPattern("[a-z\\s]*");
        param.setMinLength(3);
        param.setMaxLength(20);

        List<ValidationError> errors = TestUtils.validator(param).validateParam(param, "all fine");

        assertThat(errors.size(), is(0));
    }

    @Test
    public void reportsErrorWhenValueDoesNotMatchPattern() {
        param.setPattern("[a-z]*");

        List<ValidationError> errors = TestUtils.validator(param).validateParam(param, "WRONG_CASE!");

        assertThat(errors.size(), is(1));
        assertThat(
            errors.get(0).getErrorMessage(),
            is("ECMA 262 regex \"[a-z]*\" does not match input string \"WRONG_CASE!\"")
        );
    }

    @Test
    public void reportsErrorWhenValueIsTooShort() {
        param.setMinLength(10);

        List<ValidationError> errors = TestUtils.validator(param).validateParam(param, "short");

        assertThat(errors.size(), is(1));
        assertThat(
            errors.get(0).getErrorMessage(),
            is("string \"short\" is too short (length: 5, required minimum: 10)")
        );
    }

    @Test
    public void reportsErrorWhenValueIsTooLong() {
        param.setMaxLength(5);

        List<ValidationError> errors = TestUtils.validator(param).validateParam(param, "far too long");

        assertThat(errors.size(), is(1));
        assertThat(
            errors.get(0).getErrorMessage(),
            is("string \"far too long\" is too long (length: 12, maximum allowed: 5)")
        );
    }

    @Test
    public void reports_no_error_when_value_is_in_enum_list() {
        param.setEnum(asList(
            "one",
            "two",
            "three"
        ));

        List<ValidationError> errors = TestUtils.validator(param).validateParam(param, "two");

        assertThat(errors.size(), is(0));
    }

    @Test
    public void reports_error_when_value_is_not_in_enum_list() {
        param.setEnum(asList(
            "one",
            "two",
            "three"
        ));

        List<ValidationError> errors = TestUtils.validator(param).validateParam(param, "not_present");

        assertThat(errors.size(), is(1));
        assertThat(errors, hasItem(validationError("myField", "instance value (not_present) not found in enum (possible values: [one, two, three])")));
    }

    private static AbstractSerializableParameter<? extends AbstractSerializableParameter<?>> param() {
        QueryParameter param = new QueryParameter();
        param.setType("string");
        param.setName("myField");
        return param;
    }
}
