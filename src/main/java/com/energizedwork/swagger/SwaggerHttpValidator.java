package com.energizedwork.swagger;

import com.energizedwork.swagger.parameters.ParameterFormat;
import com.energizedwork.swagger.parameters.PathTemplate;
import com.energizedwork.swagger.reports.InteractionValidationReport;
import com.energizedwork.swagger.reports.RequestValidationReport;
import com.energizedwork.swagger.reports.ResponseValidationReport;
import com.energizedwork.swagger.validators.ParameterValidator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.LogLevel;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Resources;
import io.swagger.models.*;
import io.swagger.models.parameters.*;
import io.swagger.parser.SwaggerParser;
import com.energizedwork.swagger.parameters.ParameterValue;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

public class SwaggerHttpValidator {

    private final Swagger swagger;
    private final Map<String, JsonSchema> schemas;
    private final ObjectMapper mapper;
    private final JsonSchemaFactory jsonSchemaFactory;

    public SwaggerHttpValidator(String swaggerSpecJson) {
        mapper = new ObjectMapper();
        jsonSchemaFactory = JsonSchemaFactory.byDefault();

        JsonNode swaggerSpecRootNode = parseJson(swaggerSpecJson);
        swagger = new SwaggerParser().read(swaggerSpecRootNode);

        ImmutableMap.Builder<String, JsonSchema> builder = ImmutableMap.builder();
        swaggerSpecRootNode.findValue("definitions").fields().forEachRemaining((entry) -> {
            JsonSchema schema = parseJsonSchema(entry.getValue());
            builder.put(entry.getKey(), schema);
        });

        schemas = builder.build();
    }

    public static SwaggerHttpValidator fromClasspath(String specFilePath) {
        try {
            return new SwaggerHttpValidator(
                Resources.toString(Resources.getResource(specFilePath), Charsets.UTF_8)
            );
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public RequestValidationReport validateRequest(CheckableRequest request) {
        ImmutableList.Builder<String> globalErrorsBuilder = ImmutableList.builder();

        Optional<SwaggerOperationDef> maybeOperationDef = findOperation(request, globalErrorsBuilder);

        List<ValidationError> parameterValidationErrors = maybeOperationDef.map(operationDef -> {
            ImmutableList.Builder<ValidationError> parameterErrorsBuilder = ImmutableList.builder();
            parameterErrorsBuilder.addAll(validatePathParameters(request, operationDef.getPathTemplate(), operationDef.getOperation()));
            parameterErrorsBuilder.addAll(validateQueryParameters(request, operationDef.getOperation()));
            parameterErrorsBuilder.addAll(validateHeaderParameters(request, operationDef.getOperation()));
            parameterErrorsBuilder.addAll(validateCookieParameters(request, operationDef.getOperation()));
            return parameterErrorsBuilder.build();
        }).orElse(ImmutableList.of());

        List<ValidationError> bodyErrors = maybeOperationDef.map(operationDef -> {
            try {
                return validateBodyIfPresent(request, operationDef.getOperation());
            } catch (ProcessingException e) {
                globalErrorsBuilder.add("Failed to validate request body. Reason: " + e.getMessage());
                return Collections.<ValidationError>emptyList();
            }
        }).orElse(ImmutableList.of());

        return new RequestValidationReport(
            globalErrorsBuilder.build(),
            parameterValidationErrors,
            bodyErrors
        );
    }

    private Optional<SwaggerOperationDef> findOperation(CheckableRequest request, ImmutableList.Builder<String> errors) {
        String path = request.getPath();
        HttpMethod method = HttpMethod.valueOf(request.getMethod());

        Optional<Map.Entry<String, Path>> maybeMatchingPath = swagger.getPaths()
            .entrySet().stream()
            .filter((entry) -> new PathTemplate(entry.getKey()).matches(path))
            .findFirst();

        if (!maybeMatchingPath.isPresent()) {
            errors.add(String.format("Path '%s' was not found in the spec", path));
        }

        return maybeMatchingPath.flatMap(entry -> {
            Path swaggerPathDef = entry.getValue();
            PathTemplate pathTemplate = new PathTemplate(entry.getKey());
            Operation operation = swaggerPathDef.getOperationMap().get(method);

            Optional<SwaggerOperationDef> maybeOperationDef = Optional.ofNullable(operation).map(swaggerOperation ->
                new SwaggerOperationDef(pathTemplate, method.name(), swaggerOperation)
            );

            if (!maybeOperationDef.isPresent()) {
                errors.add(String.format("Path '%s' does not support method %s", path, method));
            }

            return maybeOperationDef;
        });
    }

    private List<ValidationError> validatePathParameters(CheckableRequest request, PathTemplate pathTemplate, Operation operation) {
        List<ParameterValue> pathParams = pathTemplate.parse(request.getPath());
        return validateParameters(PathParameter.class, operation, pathParams);
    }

    private List<ValidationError> validateQueryParameters(CheckableRequest request, Operation operation) {
        return validateParameters(QueryParameter.class, operation, request.getQueryParameters());
    }

    private List<ValidationError> validateHeaderParameters(CheckableRequest request, Operation operation) {
        return validateParameters(HeaderParameter.class, operation, request.getHeaders());
    }

    private List<ValidationError> validateCookieParameters(CheckableRequest request, Operation operation) {
        return validateParameters(CookieParameter.class, operation, request.getCookies());
    }

    private List<ValidationError> validateParameters(Class<? extends AbstractSerializableParameter<? extends AbstractSerializableParameter<?>>> type,
                                                     Operation operation,
                                                     List<ParameterValue> paramValues) {
        return getParametersOfType(type, operation).flatMap(queryParamDef -> {
            ParameterValidator validator = ParameterFormat.fromParameterDef(queryParamDef).getValidator();
            return paramValues.stream()
                .filter(queryParam -> queryParam.getKey().equals(queryParamDef.getName()))
                .findFirst()
                .map(queryParam -> validator.validateParam(queryParamDef, queryParam.getValue()).stream())
                .orElse(singletonList(
                    ValidationError.forParameter(
                        queryParamDef,
                        String.format("required %s parameter \"%s\" not found", queryParamDef.getIn(), queryParamDef.getName())
                    )
                ).stream());
        }).collect(Collectors.toList());
    }

    private List<ValidationError> validateBodyIfPresent(CheckableRequest request, Operation operation) throws ProcessingException {
        Optional<BodyParameter> bodyParameter = getBodyParameter(operation);

        ImmutableList.Builder<ValidationError> bodyErrors = ImmutableList.builder();
        if (bodyParameter.isPresent()) {
            RefModel schemaRef = (RefModel) bodyParameter.get().getSchema();
            JsonSchema schema = schemas.get(schemaRef.getSimpleRef());

            JsonNode jsonNode = parseJson(request.getBody());

            ProcessingReport processingReport = schema.validate(jsonNode);
            for (ProcessingMessage processingMessage : processingReport) {
                if (processingMessage.getLogLevel().compareTo(LogLevel.INFO) > 0) {
                    JsonNode detailsNode = processingMessage.asJson();
                    String propertyPath = detailsNode.findValue("instance").findValue("pointer").textValue();
                    bodyErrors.add(ValidationError.bodyError(propertyPath, processingMessage.getMessage()));
                }
            }
        }

        return bodyErrors.build();
    }

    public InteractionValidationReport validateInteraction(CheckableRequest request, CheckableResponse response) {
        RequestValidationReport requestValidationReport = validateRequest(request);

        ImmutableList.Builder<String> responseGlobalErrors = ImmutableList.builder();
        Optional<SwaggerOperationDef> maybeOperationDef = findOperation(request, responseGlobalErrors);
        ResponseValidationReport responseValidationReport = maybeOperationDef.map(operationDef -> {
            responseGlobalErrors.addAll(validateResponseStatus(response, operationDef));
            return new ResponseValidationReport(responseGlobalErrors.build());
        }).orElse(ResponseValidationReport.NOT_MATCHED);

        return new InteractionValidationReport(requestValidationReport, responseValidationReport);
    }

    private List<String> validateResponseStatus(CheckableResponse response, SwaggerOperationDef operationDef) {
        String status = String.valueOf(response.getStatus());
        return operationDef.getOperation().getResponses().containsKey(status) ?
            emptyList() :
            singletonList(String.format("%s %s has no associated response with status %s",
                operationDef.getMethod(),
                operationDef.getPathTemplate(),
                response.getStatus())
            );
    }

    @SuppressWarnings("unchecked")
    private <T extends AbstractSerializableParameter<? extends AbstractSerializableParameter<?>>> Stream<T> getParametersOfType(Class<T> paramClass, Operation operation) {
        return operation.getParameters().stream()
            .filter((parameter) -> paramClass.isAssignableFrom(parameter.getClass()))
            .map((parameter) -> (T) parameter);
    }

    private Optional<BodyParameter> getBodyParameter(Operation operation) {
        return operation.getParameters().stream()
            .filter((parameter) -> BodyParameter.class.isAssignableFrom(parameter.getClass()))
            .map((parameter) -> (BodyParameter) parameter)
            .findFirst();
    }

    private JsonNode parseJson(String json) {
        try {
            return mapper.readValue(json, JsonNode.class);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private JsonNode parseJson(byte[] json) {
        try {
            return mapper.readValue(json, JsonNode.class);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private JsonSchema parseJsonSchema(JsonNode json) {
        try {
            return jsonSchemaFactory.getJsonSchema(json);
        } catch (ProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
