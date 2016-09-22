package com.energizedwork.swagger.reports;

import java.util.List;

public abstract class BaseValidationReport {

    protected final List<String> globalErrors;

    protected BaseValidationReport(List<String> globalErrors) {
        this.globalErrors = globalErrors;
    }

    public List<String> getGlobalErrors() {
        return globalErrors;
    }
}
