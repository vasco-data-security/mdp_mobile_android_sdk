/*
 * Copyright (c) 2014  VASCO Data Security International GmbH. All rights reserved.
 */

package com.vasco.mydigipass.test;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

import com.vasco.mydigipass.sdk.BuildConfig;
import com.vasco.mydigipass.sdk.MDPException;
import com.vasco.mydigipass.sdk.MDPMobile;
import com.vasco.mydigipass.sdk.MDPResponse;
import com.vasco.mydigipass.sdk.OnMDPAuthenticationListener;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doThrow;
import static org.powermock.api.mockito.PowerMockito.spy;

@RunWith(RobolectricTestRunner.class)
@PrepareForTest({MDPMobile.class, Intent.class})
public class MDPMobileTest {

  private MDPMobile mydigipass;
  final Logger logger = LoggerFactory.getLogger(MDPMobileTest.class);

  @Mock
  private PackageManager packageManager;
  private TestActivity mockActivity;

  @Before
  public void setUp() throws Exception {
    TestActivity testActivity = Robolectric.buildActivity(TestActivity.class).create().get();
    System.setProperty("dexmaker.dexcache", testActivity.getCacheDir().toString());
    mockActivity = spy(testActivity);
    this.mydigipass = new MDPMobile(mockActivity);
  }

  @Test
  public void testPowerMockito() throws Exception {
    MDPMobile spy = spy(mydigipass);
    doReturn(true).when(spy).isMdpInstalled();
    assertTrue(spy.isMdpInstalled());
  }

  @Test
  public void testAlternativeConstructor() {
    mydigipass = new MDPMobile(mockActivity, "testclientID", "mdp://testuri");
    assertEquals("testclientID", mydigipass.getClientId());
    assertEquals("mdp://testuri", mydigipass.getRedirectUri());
  }

  @Test
  public void testSetAuthenticationListener() {
    this.mydigipass.setMDPAuthenticationListener(new OnMDPAuthenticationListener() {
      @Override
      public void onMDPAuthenticationSuccess(MDPResponse response) {

      }

      @Override
      public void onMDPAuthenticationFail(MDPResponse response) {

      }
    });
    assertNotNull(this.mydigipass.getMDPAuthenticationListener());
  }

  @Test(expected = MDPException.class)
  public void testThrowMDPException() {
    mydigipass.authenticate("123456789qwerty");
    assertEquals("123456789qwerty", mydigipass.getState());
  }

  @Test
  public void testMdpNotInstalledNullPointer() throws Exception {
    doThrow(new NullPointerException()).when(mockActivity, "getPackageManager");
    assertFalse(mydigipass.isMdpInstalled());
  }

  @Test
  public void testMdpNotInstalledPackageNullPointer() throws Exception {
    PackageManager test = mock(PackageManager.class);
    PowerMockito.doReturn(test).when(mockActivity).getPackageManager();
    logger.info("Buildconfig mydigipass package name: " + BuildConfig.MYDIGIPASS_PACKAGE_NAME);
    Mockito.doThrow(new NullPointerException()).when(test).getPackageInfo(BuildConfig.MYDIGIPASS_PACKAGE_NAME, PackageManager.GET_ACTIVITIES);
    assertFalse(mydigipass.isMdpInstalled());
  }

  @Test
  public void testMdpNotInstalledPackageNotFound() throws Exception {
    PackageManager test = mock(PackageManager.class);
    PowerMockito.doReturn(test).when(mockActivity).getPackageManager();
    Mockito.doThrow(new PackageManager.NameNotFoundException()).when(test).getPackageInfo(BuildConfig.MYDIGIPASS_PACKAGE_NAME, PackageManager.GET_ACTIVITIES);
    assertFalse(mydigipass.isMdpInstalled());
  }

  @Test
  public void testMdpInstalled() throws Exception {
    PackageManager test = mock(PackageManager.class);
    PowerMockito.doReturn(test).when(mockActivity).getPackageManager();
    PowerMockito.doReturn(null).when(test).getPackageInfo(BuildConfig.MYDIGIPASS_PACKAGE_NAME, PackageManager.GET_ACTIVITIES);
    assertTrue(mydigipass.isMdpInstalled());
  }

  @Test
  public void testStateParameter() throws Exception {
    mydigipass.setClientId("client");
    mydigipass.setRedirectUri("http://test.com");
    mydigipass.authenticate("1234");

    assertEquals("1234", mydigipass.getState());
  }

  @Test
  public void testSetClientId() {
    this.mydigipass.setClientId("myclientid111222333444555666778899");
    assertEquals("myclientid111222333444555666778899", this.mydigipass.getClientId());
  }

  @Test
  public void testSetRedirectUri() {
    this.mydigipass.setRedirectUri("mdp://this.is.test");
    assertEquals("mdp://this.is.test", this.mydigipass.getRedirectUri());
  }

  @Test
  public void testWebFlowNoIntent() {
    verify(mockActivity,never()).onMDPAuthenticationSuccess(any(MDPResponse.class));
    PowerMockito.doReturn(null).when(mockActivity).getIntent();
    mydigipass.webFlow();
  }

  @Test
  public void testWebFlowUnknownIntent() {
    verify(mockActivity,never()).onMDPAuthenticationSuccess(any(MDPResponse.class));
    mydigipass.webFlow();
  }

  @Test
  public void testWebFlowNonMdpIntent() {
    Intent browserIntent = mock(Intent.class);
    Uri foreignUri = Uri.parse("http://notthemydigipassredirecturi.com?code=test&state=1234");

    mydigipass.setMDPAuthenticationListener(mockActivity);
    mydigipass.setRedirectUri("my-redirect-uri");

    PowerMockito.doReturn(Intent.ACTION_VIEW).when(browserIntent).getAction();
    PowerMockito.doReturn(foreignUri).when(browserIntent).getData();
    PowerMockito.doReturn(browserIntent).when(mockActivity).getIntent();

    logger.info("Set uri: "+foreignUri.toString());
    mydigipass.webFlow();
    verify(mockActivity,never()).onMDPAuthenticationSuccess(any(MDPResponse.class));
  }

  @Test
  public void testWebFlowMdpIntent() {
    Intent browserIntent = mock(Intent.class);
    Uri foreignUri = Uri.parse("http://mydigipass-redirect-uri.com?code=test&state=1234");

    mydigipass.setMDPAuthenticationListener(mockActivity);
    mydigipass.setRedirectUri("http://mydigipass-redirect-uri.com");

    PowerMockito.doReturn(Intent.ACTION_VIEW).when(browserIntent).getAction();
    PowerMockito.doReturn(foreignUri).when(browserIntent).getData();
    PowerMockito.doReturn(browserIntent).when(mockActivity).getIntent();

    logger.info("Set uri: "+foreignUri.toString());
    mydigipass.webFlow();
    verify(mockActivity,atLeastOnce()).getIntent();
    verify(mockActivity,atLeastOnce()).onMDPAuthenticationSuccess(any(MDPResponse.class));
  }

  @Test
  public void testWebFlowMdpIntentNoQueryParameter() {
    Intent browserIntent = mock(Intent.class);
    Uri foreignUri = Uri.parse("http://mydigipass-redirect-uri.com");

    mydigipass.setMDPAuthenticationListener(mockActivity);
    mydigipass.setRedirectUri("http://mydigipass-redirect-uri.com");

    PowerMockito.doReturn(Intent.ACTION_VIEW).when(browserIntent).getAction();
    PowerMockito.doReturn(foreignUri).when(browserIntent).getData();
    PowerMockito.doReturn(browserIntent).when(mockActivity).getIntent();

    logger.info("Set uri: "+foreignUri.toString());
    mydigipass.webFlow();

    // This may never happen, as the user can have other quite similar intents.
    verify(mockActivity,never()).onMDPAuthenticationFail(any(MDPResponse.class));
  }

  @Test
  public void testWebFlowMdpIntentWrongParameter() {
    Intent browserIntent = mock(Intent.class);
    Uri foreignUri = Uri.parse("http://mydigipass-redirect-uri.com?test=foo");

    mydigipass.setMDPAuthenticationListener(mockActivity);
    mydigipass.setRedirectUri("http://mydigipass-redirect-uri.com");

    PowerMockito.doReturn(Intent.ACTION_VIEW).when(browserIntent).getAction();
    PowerMockito.doReturn(foreignUri).when(browserIntent).getData();
    PowerMockito.doReturn(browserIntent).when(mockActivity).getIntent();

    logger.info("Set uri: "+foreignUri.toString());
    mydigipass.webFlow();

    // This may never happen, as the user can have other quite similar intents.
    verify(mockActivity,atLeastOnce()).onMDPAuthenticationFail(any(MDPResponse.class));
  }

  @Test
  public void testAuthenticateWebFlow() throws Exception {
    MDPMobile test = spy(mydigipass);
    PowerMockito.doReturn(false).when(test).isMdpInstalled();
    Uri uri = mock(Uri.class);

    PowerMockito.doReturn(uri).when(test).getOauthUri();

    test.setClientId("client");
    test.setRedirectUri("http://test.com");
    test.authenticate("1234");

    verify(mockActivity).startActivity(any(Intent.class));
  }

  @Test
  public void testHandleResultNoMdpIntent() throws Exception {
    Intent appIntent = mock(Intent.class);
    PowerMockito.doReturn(null).when(appIntent).getStringExtra("redirect-uri");
    mydigipass.handleResult(1, 1, appIntent);
    verify(mockActivity,never()).onMDPAuthenticationSuccess(any(MDPResponse.class));
    verify(mockActivity,never()).onMDPAuthenticationFail(any(MDPResponse.class));
  }

  @Test
  public void testHandleResultNoCorrectUri() throws Exception {
    Intent appIntent = mock(Intent.class);
    PowerMockito.doReturn("wrong-uri").when(appIntent).getStringExtra("redirect-uri");
    mydigipass.setRedirectUri("correct-uri");
    mydigipass.handleResult(1, 1, appIntent);
    verify(mockActivity,never()).onMDPAuthenticationSuccess(any(MDPResponse.class));
    verify(mockActivity,never()).onMDPAuthenticationFail(any(MDPResponse.class));
  }

  @Test
  public void testHandleResultSuccess() throws Exception {
    Intent appIntent = mock(Intent.class);

    PowerMockito.doReturn("correct-uri").when(appIntent).getStringExtra("redirect-uri");
    PowerMockito.doReturn("correct1234").when(appIntent).getStringExtra("auth-code");
    PowerMockito.doReturn("state1234").when(appIntent).getStringExtra("state");

    mydigipass.setMDPAuthenticationListener(mockActivity);
    mydigipass.setRedirectUri("correct-uri");
    mydigipass.handleResult(1, 1, appIntent);

    verify(mockActivity,atLeastOnce()).onMDPAuthenticationSuccess(any(MDPResponse.class));
    verify(mockActivity,never()).onMDPAuthenticationFail(any(MDPResponse.class));
  }

  @Test
  public void testHandleResultNoCodeAndState() throws Exception {
    Intent appIntent = mock(Intent.class);

    PowerMockito.doReturn("correct-uri").when(appIntent).getStringExtra("redirect-uri");
    PowerMockito.doReturn(null).when(appIntent).getStringExtra("auth-code");
    PowerMockito.doReturn(null).when(appIntent).getStringExtra("state");

    mydigipass.setMDPAuthenticationListener(mockActivity);
    mydigipass.setRedirectUri("correct-uri");
    mydigipass.handleResult(1, 1, appIntent);

    verify(mockActivity,never()).onMDPAuthenticationSuccess(any(MDPResponse.class));
    verify(mockActivity,atLeastOnce()).onMDPAuthenticationFail(any(MDPResponse.class));
  }

}