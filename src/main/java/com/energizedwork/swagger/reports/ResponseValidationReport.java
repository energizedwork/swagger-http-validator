package com.energizedwork.swagger.reports;

import java.util.Collections;
import java.util.List;

public class ResponseValidationReport extends BaseValidationReport {

    public static final ResponseValidationReport NOT_MATCHED = new ResponseValidationReport(
        Collections.singletonList("The HTTP request was not matched by any operation in the spec")
    );

    public ResponseValidationReport(List<String> globalErrors) {
        super(globalErrors);
    }

}
