package com.energizedwork.swagger;

import java.util.List;

public class RequestValidationReport {

    private final List<String> globalErrors;
    private final List<ValidationError> parameterErrors;
    private final List<ValidationError> bodyErrors;

    public RequestValidationReport(List<String> globalErrors, List<ValidationError> parameterErrors, List<ValidationError> bodyErrors) {
        this.globalErrors = globalErrors;
        this.parameterErrors = parameterErrors;
        this.bodyErrors = bodyErrors;
    }

    public List<String> getGlobalErrors() {
        return globalErrors;
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
