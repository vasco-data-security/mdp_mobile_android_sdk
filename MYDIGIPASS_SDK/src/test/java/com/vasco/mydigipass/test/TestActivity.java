/*
 * Copyright (c) 2014  VASCO Data Security International GmbH. All rights reserved.
 */

package com.vasco.mydigipass.test;

import android.app.Activity;
import android.os.Bundle;

import com.vasco.mydigipass.sdk.MDPResponse;
import com.vasco.mydigipass.sdk.OnMDPAuthenticationListener;

public class TestActivity extends Activity implements OnMDPAuthenticationListener {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public void onMDPAuthenticationSuccess(MDPResponse response) {

  }

  @Override
  public void onMDPAuthenticationFail(MDPResponse response) {

  }
}
