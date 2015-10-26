/*
 * Copyright (c) 2014  VASCO Data Security International GmbH. All rights reserved.
 */

package com.vasco.mydigipass.sdk;

/**
 * Implements a callback interface.
 * When a request fails is calls onMDPAuthenticationFail.
 * When the request succeeds it calls onMDPAuthenticationSuccess.
 */
public interface OnMDPAuthenticationListener {
    void onMDPAuthenticationSuccess(MDPResponse response);

    void onMDPAuthenticationFail(MDPResponse response);
}
