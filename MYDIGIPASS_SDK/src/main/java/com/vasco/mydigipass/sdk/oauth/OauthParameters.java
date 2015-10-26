package com.vasco.mydigipass.sdk.oauth;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class OauthParameters implements Serializable {
    private Map<String, String> parameters;

    public OauthParameters() {
        this.parameters = new LinkedHashMap<>();
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void addParameter(String key, String value) {
        if (value != null) {
            this.getParameters().put(key, value);
        }
    }

    public void addParameters(Map<String, String> params) {
        if(params != null) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                addParameter(entry.getKey(), entry.getValue());
            }
        }
    }

    public void removeParameter(String parameter) {
        this.getParameters().remove(parameter);
    }
}
