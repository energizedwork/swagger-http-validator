package com.energizedwork.swagger;

import com.energizedwork.swagger.*;
import org.junit.Test;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static testsupport.CustomMatchers.validationError;

public class RequestValidatorTest {

    private SwaggerHttpValidator validator = SwaggerHttpValidator.fromClasspath("example-spec.json");

    @Test
    public void reports_error_when_request_body_does_not_match_schema() {
        CheckableRequest request = new MutableCheckableRequest()
            .url("/users")
            .method("POST")
            .body("{                                                    \n" +
                "    \"id\": \"6CB466B5-BBF7-40AB-AC49-F3E3F00CA86C\",  \n" +
                "    \"username\": \"££\",                              \n" +
                "    \"displayName\": \"Far too long to pass\"          \n" +
                "}"
            );

        RequestValidationReport report = validator.validateRequest(request);
        List<ValidationError> bodyErrors = report.getBodyErrors();

        assertThat(bodyErrors.size(), is(3));
        assertThat(bodyErrors, hasItem(validationError("/username", "string \"££\" is too short (length: 2, required minimum: 3)")));
        assertThat(bodyErrors, hasItem(validationError("/username", "ECMA 262 regex \"^[a-zA-Z0-9_]*$\" does not match input string \"££\"")));
        assertThat(bodyErrors, hasItem(validationError("/displayName", "string \"Far too long to pass\" is too long (length: 20, maximum allowed: 5)")));
    }

    @Test
    public void reports_error_when_url_not_found() {
        CheckableRequest request = new MutableCheckableRequest()
            .url("/something/wrong")
            .method("GET");

        RequestValidationReport report = validator.validateRequest(request);
        List<String> globalErrors = report.getGlobalErrors();

        assertThat(globalErrors.size(), is(1));
        assertThat(globalErrors, hasItem(equalTo("Path '/something/wrong' was not found in the spec")));
    }

    @Test
    public void reports_error_when_url_found_but_no_matching_method() {
        CheckableRequest request = new MutableCheckableRequest()
            .url("/users")
            .method("PUT");

        RequestValidationReport report = validator.validateRequest(request);
        List<String> globalErrors = report.getGlobalErrors();

        assertThat(globalErrors.size(), is(1));
        assertThat(globalErrors, hasItem(equalTo("Path '/users' does not support method PUT")));
    }

    @Test
    public void reports_all_valid_when_request_matches_url_with_path_parameters() {
        CheckableRequest request = new MutableCheckableRequest()
            .url("/users/12345") // Expecting int32
            .method("GET");

        RequestValidationReport report = validator.validateRequest(request);

        assertTrue("Expected no validation errors. Got:\n" + report, report.isAllValid());
    }

    @Test
    public void reports_validation_error_when_path_parameter_is_incorrect_type() {
        CheckableRequest request = new MutableCheckableRequest()
            .url("/users/abcdef") // Expecting int32
            .method("GET");

        RequestValidationReport report = validator.validateRequest(request);

        assertThat(report.getParameterErrors().size(), is(1));
        assertThat(report.getParameterErrors(), hasItem(validationError("id", "input string \"abcdef\" is not a valid int32")));
    }

    @Test
    public void reports_validation_error_when_path_parameters_are_incorrect() {
        CheckableRequest request = new MutableCheckableRequest()
            .url("/users/abcdef/avatars/my_avatar_name_is_too_long")
            .method("GET");

        RequestValidationReport report = validator.validateRequest(request);

        assertThat(report.getParameterErrors().size(), is(2));
        assertThat(report.getParameterErrors(), hasItem(validationError("id", "input string \"abcdef\" is not a valid int32")));
        assertThat(report.getParameterErrors(), hasItem(validationError("avatarName", "string \"my_avatar_name_is_too_long\" is too long (length: 26, maximum allowed: 10)")));
    }

    @Test
    public void reports_error_when_query_parameter_violates_constraint() {
        CheckableRequest request = new MutableCheckableRequest()
            .url("/users?limit=200&offset=37")
            .method("GET");

        RequestValidationReport report = validator.validateRequest(request);

        assertThat(report.getParameterErrors().size(), is(1));
        assertThat(report.getParameterErrors(), hasItem(validationError("limit", "numeric instance is not strictly lower than the required maximum 50.0")));
    }

    @Test
    public void reports_errors_when_query_parameters_violate_constraints() {
        CheckableRequest request = new MutableCheckableRequest()
            .url("/users?limit=200&offset=80000000")
            .method("GET");

        RequestValidationReport report = validator.validateRequest(request);

        assertThat(
            "Expected a report with 2 errors but got: " + report,
            report.getParameterErrors().size(), is(2)
        );
        assertThat(report.getParameterErrors(), hasItem(validationError("limit", "numeric instance is not strictly lower than the required maximum 50.0")));
        assertThat(report.getParameterErrors(), hasItem(validationError("offset", "numeric instance is greater than the required maximum (maximum: 2000.0, found: 8.0E7)")));
    }

    @Test
    public void reports_errors_when_required_query_parameter_is_absent() {
        CheckableRequest request = new MutableCheckableRequest()
            .url("/users?offset=10")
            .method("GET");

        RequestValidationReport report = validator.validateRequest(request);

        assertThat(
            "Expected a report with 1 errors but got: " + report,
            report.getParameterErrors().size(), is(1)
        );
        assertThat(report.getParameterErrors(), hasItem(validationError("limit", "required query parameter \"limit\" not found")));
    }

    @Test
    public void reports_error_when_required_header_is_absent() {
        CheckableRequest request = new MutableCheckableRequest()
            .url("/ping")
            .method("POST");

        RequestValidationReport report = validator.validateRequest(request);

        assertThat(
            "Expected a report with 1 errors but got: " + report,
            report.getParameterErrors().size(), is(1)
        );
        assertThat(report.getParameterErrors(), hasItem(validationError("X-Ping-Date", "required header parameter \"X-Ping-Date\" not found")));
    }

    @Test
    public void reports_no_errors_when_required_header_is_present() {
        CheckableRequest request = new MutableCheckableRequest()
            .url("/ping")
            .header("X-Ping-Date", "2016-09-22T11:12:13.123")
            .method("POST");

        RequestValidationReport report = validator.validateRequest(request);

        assertThat(
            "Expected a report with no errors but got: " + report,
            report.getParameterErrors().size(), is(0)
        );
    }

    @Test
    public void reports_error_when_required_cookie_is_absent() {
        CheckableRequest request = new MutableCheckableRequest()
            .url("/session")
            .method("GET");

        RequestValidationReport report = validator.validateRequest(request);

        assertThat(
            "Expected a report with 1 errors but got: " + report,
            report.getParameterErrors().size(), is(1)
        );
        assertThat(report.getParameterErrors(), hasItem(validationError("SessionId", "required cookie parameter \"SessionId\" not found")));
    }

    @Test
    public void reports_no_errors_when_required_cookie_is_present() {
        CheckableRequest request = new MutableCheckableRequest()
            .url("/session")
            .cookie("SessionId", UUID.randomUUID().toString())
            .method("GET");

        RequestValidationReport report = validator.validateRequest(request);

        assertThat(
            "Expected a report with no errors but got: " + report,
            report.getParameterErrors().size(), is(0)
        );
    }

}
