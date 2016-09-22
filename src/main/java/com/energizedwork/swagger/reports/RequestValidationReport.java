package com.energizedwork.swagger.reports;

import com.energizedwork.swagger.ValidationError;

import java.util.List;

public class RequestValidationReport extends BaseValidationReport {

    private final List<ValidationError> parameterErrors;
    private final List<ValidationError> bodyErrors;

    public RequestValidationReport(List<String> globalErrors, List<ValidationError> parameterErrors, List<ValidationError> bodyErrors) {
        super(globalErrors);
        this.parameterErrors = parameterErrors;
        this.bodyErrors = bodyErrors;
    }

    public List<ValidationError> getParameterErrors() {
        return parameterErrors;
    }

    public List<ValidationError> getBodyErrors() {
        return bodyErrors;
    }

    public boolean isAllValid() {
        return parameterErrors.isEmpty() && bodyErrors.isEmpty() && globalErrors.isEmpty();
    }

    @Override
    public String toString() {
        if (isAllValid()) {
            return "All valid";
        }

        StringBuilder sb = new StringBuilder();
        if (!globalErrors.isEmpty()) {
            sb.append("Global errors\n");
            sb.append("=============\n\n");
            for (String error: globalErrors) {
                sb.append(error).append('\n');
            }
        }

        if (!parameterErrors.isEmpty()) {
            sb.append("Parameter errors\n");
            sb.append("================\n\n");
            for (ValidationError error: parameterErrors) {
                sb
                    .append("[")
                    .append(error.getParameterSource())
                    .append("] ")
                    .append(error.getFieldPath())
                    .append(": ")
                    .append(error)
                    .append('\n');
            }
        }

        if (!bodyErrors.isEmpty()) {
            sb.append("Body errors\n");
            sb.append("===========\n\n");
            for (ValidationError error: bodyErrors) {
                sb.append(error.getFieldPath()).append(": ").append(error).append('\n');
            }
        }

        return sb.toString();
    }
}
