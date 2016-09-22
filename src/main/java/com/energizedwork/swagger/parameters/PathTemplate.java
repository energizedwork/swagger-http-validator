package com.energizedwork.swagger.parameters;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkArgument;

public class PathTemplate {

    public static final Pattern PATH_VARIABLE_REGEX = Pattern.compile("^\\{(.*)\\}$");

    private final String templateString;
    private final String[] templateParts;

    public PathTemplate(String templateString) {
        this.templateString = templateString;
        templateParts = templateString.split("/");
    }

    public boolean matches(String url) {
        String[] urlParts = url.split("/");

        if (templateParts.length != urlParts.length) {
            return false;
        }

        for (int i = 0; i < templateParts.length; i++) {
            boolean isVariable = isVariable(templateParts[i]);
            boolean areEqual = templateParts[i].equals(urlParts[i]);

            if (!isVariable && !areEqual) {
                return false;
            }
        }

        return true;
    }

    public List<ParameterValue> parse(String url) {
        ImmutableList.Builder<ParameterValue> builder = ImmutableList.builder();
        String[] urlParts = url.split("/");

        if (templateParts.length != urlParts.length) {
            throw new IllegalArgumentException(url + " does not match " + templateString);
        }

        for (int i = 0; i < templateParts.length; i++) {
            Matcher matcher = PATH_VARIABLE_REGEX.matcher(templateParts[i]);
            boolean areEqual = templateParts[i].equals(urlParts[i]);

            checkArgument(areEqual || matcher.matches(), url + " does not match " + templateString);

            if (matcher.matches()) {
                String variableName = getVariableName(templateParts[i]);
                builder.add(new ParameterValue(variableName, urlParts[i]));
            }
        }

        return builder.build();
    }

    public String render(PathParams pathParams) {
        StringBuilder sb = new StringBuilder();
        for (String templatePart: templateParts) {
            sb.append('/');
            if (isVariable(templatePart)) {
                String variableName = getVariableName(templatePart);
                String variableValue = pathParams.get(variableName);
                if (variableValue == null) {
                    throw new IllegalArgumentException("Path parameter " + variableName + " was not bound");
                }
                sb.append(variableValue);
            } else {
                sb.append(templatePart);
            }
        }

        sb.deleteCharAt(0);

        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PathTemplate that = (PathTemplate) o;
        return Objects.equal(templateString, that.templateString);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(templateString);
    }

    @Override
    public String toString() {
        return templateString;
    }

    private static String getVariableName(String templatePart) {
        return templatePart.substring(1, templatePart.length() - 1);
    }

    private static boolean isVariable(String templatePart) {
        return PATH_VARIABLE_REGEX.matcher(templatePart).matches();
    }
}
