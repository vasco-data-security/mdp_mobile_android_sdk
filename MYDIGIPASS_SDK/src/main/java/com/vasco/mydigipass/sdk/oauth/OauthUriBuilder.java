package com.vasco.mydigipass.sdk.oauth;

import android.net.Uri;

import com.vasco.mydigipass.sdk.oauth.models.Parameters;

import java.util.Map;

public class OauthUriBuilder {

    private String baseUrl;
    private Parameters parameters;

    public OauthUriBuilder(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public void setParameters(Parameters parameters) {
        this.parameters = parameters;
    }

    /**
     * Creates the full oauth uri based on the base url and parameters.
     *
     * @return Uri
     */
    public Uri getUri() {
        this.parameters.put("response_type", "code");

        Uri baseUri = Uri.parse(this.baseUrl);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        for (Map.Entry<String, String> entry : this.parameters.entrySet()) {
            uriBuilder.appendQueryParameter(entry.getKey(), entry.getValue());
        }

        return uriBuilder.build();
    }
}
