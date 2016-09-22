package com.energizedwork.swagger;

public class MutableCheckableResponse implements CheckableResponse {

    private int status;

    public MutableCheckableResponse status(int status) {
        this.status = status;
        return this;
    }

    @Override
    public int getStatus() {
        return status;
    }
}
