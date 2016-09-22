package com.energizedwork.swagger.utils;

import com.energizedwork.swagger.parameters.ParameterValue;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;

public class Urls {

    public static List<ParameterValue> splitQuery(URI uri) {
        if (uri.getQuery() == null || uri.getQuery().isEmpty()) {
            return emptyList();
        }

        String[] queryParts = uri.getQuery().split("&");
        return Arrays.stream(queryParts).map(part -> {
            String[] parts = part.split("=");
            return new ParameterValue(parts[0], parts[1]);
        }).collect(Collectors.toList());
    }

    public static List<ParameterValue> splitQuery(String uri) {
        return splitQuery(URI.create(uri));
    }
}
