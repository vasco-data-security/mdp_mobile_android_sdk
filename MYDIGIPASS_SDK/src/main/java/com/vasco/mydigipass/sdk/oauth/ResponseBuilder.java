package com.vasco.mydigipass.sdk.oauth;

import android.net.Uri;
import android.os.Bundle;

import com.vasco.mydigipass.sdk.oauth.models.Error;
import com.vasco.mydigipass.sdk.oauth.models.Parameters;
import com.vasco.mydigipass.sdk.oauth.models.Response;

import java.util.Map;

public class ResponseBuilder {

    private Bundle mdpResponse;
    private Response response;

    protected ResponseBuilder(Bundle apiResponse) {
        this.mdpResponse = apiResponse;
        this.response = new Response();
    }

    /**
     * Creates a Response object based on a bundle.
     *
     * @param mdpResponse Bundle
     * @return Response
     */
    public static Response build(Bundle mdpResponse) {
        ResponseBuilder responseBuilder = new ResponseBuilder(mdpResponse);

        if (mdpResponse.containsKey("error")) {
            responseBuilder.parseErrors();
        } else {
            responseBuilder.parseParameters();
        }

        return responseBuilder.build();
    }

    /**
     * Creates a Response object based on a uri.
     *
     * @param uri MYDIGIPASS uri
     * @return Response
     */
    public static Response build(Uri uri) {
        if (uri.getQueryParameterNames().size() > 0) {
            Bundle bundle = new Bundle();

            for (String key : uri.getQueryParameterNames()) {
                bundle.putString(key, uri.getQueryParameter(key));
            }

            return build(bundle);
        } else {
            return null;
        }
    }

    public void parseErrors() {
        com.vasco.mydigipass.sdk.oauth.models.Error error = new Error();

        error.setKey(this.mdpResponse.getString("error"));
        error.setDescription(this.mdpResponse.getString("error_description"));
        error.setTitle(this.mdpResponse.getString("error_title"));

        this.response.setError(error);
    }

    public void parseParameters() {
        Map<String, String> map = CollectionUtils.bundle2map(this.mdpResponse);
        Parameters parameters = Parameters.fromMap(map);
        this.response.setParameters(parameters);
    }

    public Response build() {
        return this.response;
    }
}
