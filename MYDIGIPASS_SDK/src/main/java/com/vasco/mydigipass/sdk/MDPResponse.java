/*
 * Copyright (c) 2014  VASCO Data Security International GmbH. All rights reserved.
 */

package com.vasco.mydigipass.sdk;

import java.util.Map;

public class MDPResponse {
    private String state;
    private String authorizationCode;
    private String redirectUri;
    private String scope;
    private Map<String, String> passthroughParams;
    private boolean success;

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getAuthorizationCode() {
        return authorizationCode;
    }

    public void setAuthorizationCode(String authorizationCode) {
        this.authorizationCode = authorizationCode;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public Map<String, String> getPassthroughParams() {
        return passthroughParams;
    }

    public void setPassthroughParams(Map<String, String> passthroughParams) {
        this.passthroughParams = passthroughParams;
    }
}
