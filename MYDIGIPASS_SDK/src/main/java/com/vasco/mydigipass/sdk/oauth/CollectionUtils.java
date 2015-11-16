package com.vasco.mydigipass.sdk.oauth;


import android.os.Bundle;

import java.util.HashMap;
import java.util.Map;

public final class CollectionUtils {

    private CollectionUtils() {
    }

    /**
     * Converts a bundle to a map of strings.
     *
     * @param bundle android bundle with parameters
     * @return Map<String, String>
     */
    public static Map<String, String> bundle2map(Bundle bundle) {
        Map<String, String> map = new HashMap<>();

        for (String key : bundle.keySet()) {
            map.put(key, bundle.getString(key));
        }

        return map;
    }

    /**
     * Converts a map of strings to a bundle.
     *
     * @param map Map<String, String>
     * @return Bundle bundle with parameters
     */
    public static Bundle map2bundle(Map<String, String> map) {
        Bundle bundle = new Bundle();

        for (Map.Entry<String, String> entry : map.entrySet()) {
            bundle.putString(entry.getKey(), entry.getValue());
        }

        return bundle;
    }

}
