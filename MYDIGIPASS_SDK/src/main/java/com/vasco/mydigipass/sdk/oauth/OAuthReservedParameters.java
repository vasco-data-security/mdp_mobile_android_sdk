package com.vasco.mydigipass.sdk.oauth;


import java.util.Arrays;

public class OAuthReservedParameters {

    private static final String[] RESERVED_PARAMETERS = {
            "client_id",
            "scope",
            "code",
            "bundle_identifier",
            "state",
            "serial",
            "redirect-uri",
            "message",
            "otp",
            "error",
            "error_title",
            "error_description",
            "response_type",

            // Old school compatibility
            "auth-code",
            "clientId",
            "redirectUri",
    };

    private OAuthReservedParameters() {
    }

    public static boolean isReservedParameter(String parameter) {
        return Arrays.asList(RESERVED_PARAMETERS).contains(parameter);
    }

}
