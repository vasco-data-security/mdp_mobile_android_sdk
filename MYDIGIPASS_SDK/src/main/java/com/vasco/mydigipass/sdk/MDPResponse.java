/*
 * Copyright (c) 2014  VASCO Data Security International GmbH. All rights reserved.
 */

package com.vasco.mydigipass.sdk;

import com.vasco.mydigipass.sdk.oauth.models.Response;

import java.util.Map;

/**
 * This is a wrapper class around the response object.
 */
public class MDPResponse {
    private Response response;

    /**
     * Default constructor.
     *
     * @param response Response coming from MYDIGIPASS.
     */
    public MDPResponse(Response response) {
        this.response = response;
    }

    public String getState() {
        return this.response.getParameters().get("state");
    }

    public String getAuthorizationCode() {
        return this.response.getParameters().get("code");
    }

    public String getRedirectUri() {
        return this.response.getParameters().get("redirect-uri");
    }

    public String getScope() {
        return this.response.getParameters().get("scope");
    }

    public Map<String, String> getPassthroughParams() {
        return this.response.getParameters().getPassthroughParams();
    }

    /**
     * Checks if the response object contains an error.
     *
     * @return boolean
     */
    public boolean containsError() {
        return this.response.containsError();
    }

    /**
     * Returns an error object when there is an error.
     *
     * @return com.vasco.mydigipass.sdk.oauth.models.Error
     */
    public com.vasco.mydigipass.sdk.oauth.models.Error getError() {
        return this.response.getError();
    }

    /**
     * Returns the response object coming from MYDIGIPASS.
     * @return Response
     */
    public Response getResponse() {
        return response;
    }
}
