package com.vasco.mydigipass.sdk.oauth.models;

public class Response {

    private Parameters parameters;
    private Error error;

    public Parameters getParameters() {
        return parameters;
    }

    public Error getError() {
        return error;
    }

    public void setParameters(Parameters parameters) {
        this.parameters = parameters;
    }

    public void setError(Error error) {
        this.error = error;
    }

    public boolean containsError() {
        return getError() != null;
    }
}
