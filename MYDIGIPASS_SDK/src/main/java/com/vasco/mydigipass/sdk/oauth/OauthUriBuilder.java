package com.vasco.mydigipass.sdk.oauth;

import android.net.Uri;

import java.util.Map;

public class OauthUriBuilder {

    private String baseUrl;
    private OauthParameters parameters;

    public OauthUriBuilder(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public void setParameters(OauthParameters parameters) {
        this.parameters = parameters;
    }

    public Uri getUri() {
        return getUri(true);
    }

    public Uri getUri(boolean request) {
        if (request) {
            this.parameters.addParameter("response_type", "code");
        }

        Uri baseUri = Uri.parse(this.baseUrl);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        for (Map.Entry<String, String> entry : this.parameters.getParameters().entrySet()) {
            uriBuilder.appendQueryParameter(entry.getKey(), entry.getValue());
        }

        return uriBuilder.build();
    }
}
