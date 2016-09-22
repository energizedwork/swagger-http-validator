package com.energizedwork.swagger.parameters;

public class ParameterValue {

    private final String key;
    private final String value;

    public ParameterValue(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
