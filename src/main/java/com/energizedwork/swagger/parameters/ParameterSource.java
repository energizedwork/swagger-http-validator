package com.energizedwork.swagger.parameters;

import io.swagger.models.parameters.Parameter;

public enum ParameterSource {

    path,
    query,
    header,
    cookie,
    form,
    body;

    public static ParameterSource from(Parameter parameter) {
        return valueOf(parameter.getIn());
    }
}
