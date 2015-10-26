/*
 * Copyright (c) 2014  VASCO Data Security International GmbH. All rights reserved.
 */

package com.vasco.mydigipass.sdk;

public class MDPException extends RuntimeException {

    public MDPException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }
}
