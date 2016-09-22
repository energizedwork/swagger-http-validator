package com.energizedwork.swagger;

import com.energizedwork.swagger.utils.Urls;
import com.energizedwork.swagger.parameters.ParameterValue;

import java.net.URI;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public class MutableCheckableRequest implements CheckableRequest {

    private URI url;
    private String method;
    private byte[] body;
    private List<ParameterValue> headers = newArrayList();
    private List<ParameterValue> cookies = newArrayList();

    public MutableCheckableRequest url(String url) {
        this.url = URI.create(url);
        return this;
    }

    public MutableCheckableRequest method(String method) {
        this.method = method;
        return this;
    }

    public MutableCheckableRequest header(String key, String value) {
        headers.add(new ParameterValue(key, value));
        return this;
    }

    public MutableCheckableRequest cookie(String key, String value) {
        cookies.add(new ParameterValue(key, value));
        return this;
    }

    public MutableCheckableRequest body(byte[] body) {
        this.body = body;
        return this;
    }

    public MutableCheckableRequest body(String body) {
        this.body = body.getBytes();
        return this;
    }

    @Override
    public String getPath() {
        return url.getPath();
    }

    @Override
    public String getMethod() {
        return method;
    }

    @Override
    public byte[] getBody() {
        return body;
    }

    @Override
    public List<ParameterValue> getQueryParameters() {
        return Urls.splitQuery(url);
    }

    @Override
    public List<ParameterValue> getHeaders() {
        return headers;
    }

    @Override
    public List<ParameterValue> getCookies() {
        return cookies;
    }
}
