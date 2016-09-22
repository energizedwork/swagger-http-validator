package com.energizedwork.swagger;

import com.energizedwork.swagger.parameters.ParameterValue;

import java.util.List;

public interface CheckableRequest {

    String getPath();
    String getMethod();
    byte[] getBody();

    List<ParameterValue> getQueryParameters();
    List<ParameterValue> getHeaders();
    List<ParameterValue> getCookies();
}
