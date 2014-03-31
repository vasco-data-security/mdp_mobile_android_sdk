/*
 * Copyright (c) 2014  VASCO Data Security International GmbH. All rights reserved.
 */

package com.vasco.mydigipass.sdk;

public class MDPResponse {
  private String state;
  private String authorizationCode;
  private String response;
  private String redirectUri;
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

  public String getResponse() {
    return response;
  }

  public void setResponse(String response) {
    this.response = response;
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

  @Override
  public String toString() {
    return "MDPResponse{" +
      "state='" + state + '\'' +
      ", authorizationCode='" + authorizationCode + '\'' +
      ", response='" + response + '\'' +
      ", redirectUri='" + redirectUri + '\'' +
      ", success=" + success +
      '}';
  }
}
