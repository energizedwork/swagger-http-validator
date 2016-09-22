package com.energizedwork.swagger;

import com.energizedwork.swagger.parameters.PathTemplate;
import io.swagger.models.Operation;

public class SwaggerOperationDef {

    private final PathTemplate pathTemplate;
    private final String method;
    private final Operation operation;

    public SwaggerOperationDef(PathTemplate pathTemplate, String method, Operation operation) {
        this.pathTemplate = pathTemplate;
        this.method = method;
        this.operation = operation;
    }

    public PathTemplate getPathTemplate() {
        return pathTemplate;
    }

    public String getMethod() {
        return method;
    }

    public Operation getOperation() {
        return operation;
    }
}
