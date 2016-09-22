package com.energizedwork.swagger;

import com.energizedwork.swagger.reports.InteractionValidationReport;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class ResponseValidatorTest {

    private SwaggerHttpValidator validator = SwaggerHttpValidator.fromClasspath("example-spec.json");

    @Test
    public void reports_error_when_response_status_is_not_present_in_spec() {
        CheckableRequest request = new MutableCheckableRequest()
            .url("/ping")
            .method("POST");
        CheckableResponse response = new MutableCheckableResponse().status(201);

        InteractionValidationReport report = validator.validateInteraction(request, response);

        List<String> globalErrors = report.getResponseValidationReport().getGlobalErrors();
        assertThat(globalErrors.size(), is(1));
        assertThat(globalErrors, hasItem(equalTo("POST /ping has no associated response with status 201")));
    }
}
