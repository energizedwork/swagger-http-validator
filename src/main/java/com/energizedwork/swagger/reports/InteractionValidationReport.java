package com.energizedwork.swagger.reports;

public class InteractionValidationReport {

    private final RequestValidationReport requestValidationReport;
    private final ResponseValidationReport responseValidationReport;

    public InteractionValidationReport(RequestValidationReport requestValidationReport, ResponseValidationReport responseValidationReport) {
        this.requestValidationReport = requestValidationReport;
        this.responseValidationReport = responseValidationReport;
    }

    public RequestValidationReport getRequestValidationReport() {
        return requestValidationReport;
    }

    public ResponseValidationReport getResponseValidationReport() {
        return responseValidationReport;
    }
}
