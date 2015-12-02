package com.vasco.mydigipass.sdk.oauth.models;

import com.vasco.mydigipass.sdk.oauth.enums.ErrorType;

import java.util.HashMap;
import java.util.Map;

public class Error {

    private String key;
    private String description;
    private String title;
    private Map<String, ErrorType> errors;

    public Error() {
        this.errors = new HashMap<>();
        this.errors.put("not_authenticated", ErrorType.NOT_AUTHENTICATED);
        this.errors.put("account_disabled", ErrorType.ACCOUNT_DISABLED);
        this.errors.put("account_no_permissions", ErrorType.NO_PERMISSIONS);
        this.errors.put("access_denied", ErrorType.ACCESS_DENIED);
        this.errors.put("invalid_request", ErrorType.INVALID_REQUEST);
        this.errors.put("authentication_method_not_allowed", ErrorType.AUTHENTICATION_METHOD_NOT_ALLOWED);
        this.errors.put("eid_scopes_present_but_not_allowed", ErrorType.EID_SCOPE_NOT_ALLOWED);
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ErrorType getType() {
        ErrorType errorType = this.errors.get(this.getKey());
        return errorType == null ? ErrorType.UNKNOWN_ERROR_KEY : errorType;
    }
}
