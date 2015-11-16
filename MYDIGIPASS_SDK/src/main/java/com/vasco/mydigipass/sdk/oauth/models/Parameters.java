package com.vasco.mydigipass.sdk.oauth.models;

import com.vasco.mydigipass.sdk.MDPException;
import com.vasco.mydigipass.sdk.oauth.OAuthReservedParameters;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class Parameters extends LinkedHashMap<String, String> implements Serializable {

    public Parameters() {
        super();
    }

    public Parameters(Map<? extends String, ? extends String> map) {
        super(map);
    }

    /**
     * Creates a shallow copy of the map and returns it back as a parameters object.
     *
     * @param map map of strings
     * @return Parameters
     */
    public static Parameters fromMap(Map<String, String> map) {
        return new Parameters(map);
    }

    @Override
    public String put(String key, String value) {
        return value != null ? super.put(key, value) : null;
    }

    /**
     * Adds passthrough parameters to the parameters object. Reserved keys will throw an exception.
     *
     * @param map keys and values
     */
    public void addPassthroughParameters(Map<String, String> map) {
        if (map != null) {
            for (Entry<String, String> param : map.entrySet()) {
                if (OAuthReservedParameters.isReservedParameter(param.getKey())) {
                    throw new MDPException("This parameter is reserved by MYDIGIPASS.");
                } else {
                    put(param.getKey(), param.getValue());
                }
            }
        }
    }

    /**
     * Filters out the reserved keys that we defined and returns the extra parameters.
     *
     * @return map
     */
    public Map<String, String> getPassthroughParams() {
        Parameters parameters = new Parameters();

        for (Entry<String, String> param : entrySet()) {
            if (!OAuthReservedParameters.isReservedParameter(param.getKey())) {
                parameters.put(param.getKey(), param.getValue());
            }
        }

        return parameters;
    }
}
