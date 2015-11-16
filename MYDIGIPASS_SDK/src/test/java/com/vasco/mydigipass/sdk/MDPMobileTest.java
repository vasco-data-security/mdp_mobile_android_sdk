/*
 * Copyright (c) 2014  VASCO Data Security International GmbH. All rights reserved.
 */

package com.vasco.mydigipass.sdk;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, emulateSdk = 21, manifest = "MYDIGIPASS_SDK/src/main/AndroidManifest.xml", resourceDir = Config.DEFAULT_RES_FOLDER, assetDir = Config.DEFAULT_ASSET_FOLDER)
public class MDPMobileTest {

    private MDPMobile mydigipass;

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

    @Test
    public void testMdpNotInstalledNullPointer() throws Exception {
        doThrow(new NullPointerException()).when(mockActivity).getPackageManager();
        assertFalse(mydigipass.isMdpInstalled());
    }

    @Test
    public void testMdpNotInstalledPackageNullPointer() throws Exception {
        PackageManager test = mock(PackageManager.class);
        doReturn(test).when(mockActivity).getPackageManager();
        doThrow(new NullPointerException()).when(test).getPackageInfo(BuildConfig.MYDIGIPASS_PACKAGE_NAME, PackageManager.GET_ACTIVITIES);
        assertFalse(mydigipass.isMdpInstalled());
    }

    @Test
    public void testMdpNotInstalledPackageNotFound() throws Exception {
        PackageManager test = mock(PackageManager.class);
        doReturn(test).when(mockActivity).getPackageManager();
        doThrow(new PackageManager.NameNotFoundException()).when(test).getPackageInfo(BuildConfig.MYDIGIPASS_PACKAGE_NAME, PackageManager.GET_ACTIVITIES);
        assertFalse(mydigipass.isMdpInstalled());
    }

    @Test
    public void testMdpInstalled() throws Exception {
        PackageManager test = mock(PackageManager.class);
        doReturn(test).when(mockActivity).getPackageManager();
        doReturn(null).when(test).getPackageInfo(BuildConfig.MYDIGIPASS_PACKAGE_NAME, PackageManager.GET_ACTIVITIES);
        assertTrue(mydigipass.isMdpInstalled());
    }

    @Test
    public void testPassthroughParameters() {
        Map<String, String> extraParams = new HashMap<>();
        extraParams.put("test", "fubar");

        mydigipass.authenticate("1234", "eid_profile", extraParams);

        assertEquals("fubar", mydigipass.getIncomingParameters().get("test"));
    }

    @Test
    public void testSetClientId() {
        this.mydigipass.setClientId("myclientid111222333444555666778899");
        assertEquals("myclientid111222333444555666778899", this.mydigipass.getClientId());
    }

    @Test(expected = MDPException.class)
    public void testSetClientIdException() {
        this.mydigipass.setClientId(null);
    }

    @Test
    public void testSetRedirectUri() {
        this.mydigipass.setRedirectUri("mdp://this.is.test");
        assertEquals("mdp://this.is.test", this.mydigipass.getRedirectUri());
    }

    @Test(expected = MDPException.class)
    public void testSetRedirectUriException() {
        this.mydigipass.setRedirectUri(null);
    }

    @Test
    public void testWebFlowNoIntent() {
        verify(mockActivity, never()).onMDPAuthenticationSuccess(any(MDPResponse.class));
        doReturn(null).when(mockActivity).getIntent();
        mydigipass.webFlow();
    }

    @Test
    public void testWebFlowUnknownIntent() {
        verify(mockActivity, never()).onMDPAuthenticationSuccess(any(MDPResponse.class));
        mydigipass.webFlow();
    }

    @Test
    public void testWebFlowNonMdpIntent() {
        Intent browserIntent = mock(Intent.class);
        Uri foreignUri = Uri.parse("http://notthemydigipassredirecturi.com?code=test&state=1234");

        mydigipass.setMDPAuthenticationListener(mockActivity);
        mydigipass.setRedirectUri("my-redirect-uri");

        doReturn(Intent.ACTION_VIEW).when(browserIntent).getAction();
        doReturn(foreignUri).when(browserIntent).getData();
        doReturn(browserIntent).when(mockActivity).getIntent();

        mydigipass.webFlow();
        verify(mockActivity, never()).onMDPAuthenticationSuccess(any(MDPResponse.class));
    }

    @Test
    public void testWebFlowMdpIntent() {
        Intent browserIntent = mock(Intent.class);
        Uri foreignUri = Uri.parse("http://mydigipass-redirect-uri.com?code=test&state=1234");

        mydigipass.setMDPAuthenticationListener(mockActivity);
        mydigipass.setRedirectUri("http://mydigipass-redirect-uri.com");

        doReturn(Intent.ACTION_VIEW).when(browserIntent).getAction();
        doReturn(foreignUri).when(browserIntent).getData();
        doReturn(browserIntent).when(mockActivity).getIntent();

        mydigipass.webFlow();
        verify(mockActivity, atLeastOnce()).getIntent();
        verify(mockActivity, atLeastOnce()).onMDPAuthenticationSuccess(any(MDPResponse.class));
    }

    @Test
    public void testWebFlowMdpIntentNoQueryParameter() {
        Intent browserIntent = mock(Intent.class);
        Uri foreignUri = Uri.parse("http://mydigipass-redirect-uri.com");

        mydigipass.setMDPAuthenticationListener(mockActivity);
        mydigipass.setRedirectUri("http://mydigipass-redirect-uri.com");

        doReturn(Intent.ACTION_VIEW).when(browserIntent).getAction();
        doReturn(foreignUri).when(browserIntent).getData();
        doReturn(browserIntent).when(mockActivity).getIntent();

        mydigipass.webFlow();

        // This may never happen, as the user can have other quite similar intents.
        verify(mockActivity, never()).onMDPAuthenticationFail(any(MDPResponse.class));
    }

    @Test
    public void testWebFlowMdpIntentWrongParameter() {
        Intent browserIntent = mock(Intent.class);

        Uri foreignUri = Uri.parse("http://mydigipass-redirect-uri.com?test=foo&error=test&error_title=fubar&error_description=djfkkjsd");

        mydigipass.setMDPAuthenticationListener(mockActivity);
        mydigipass.setRedirectUri("http://mydigipass-redirect-uri.com");

        doReturn(Intent.ACTION_VIEW).when(browserIntent).getAction();
        doReturn(foreignUri).when(browserIntent).getData();
        doReturn(browserIntent).when(mockActivity).getIntent();

        mydigipass.webFlow();

        // This may never happen, as the user can have other quite similar intents.
        verify(mockActivity, atLeastOnce()).onMDPAuthenticationFail(any(MDPResponse.class));
    }

    @Test
    public void testAuthenticateWebFlow() throws Exception {
        MDPMobile test = spy(mydigipass);
        doReturn(false).when(test).isMdpInstalled();
        Uri uri = mock(Uri.class);

        doReturn(uri).when(test).getOAuthUrl();

        test.setClientId("client");
        test.setRedirectUri("http://test.com");
        test.authenticate("1234");
    }

    @Test
    public void testHandleResultNoMdpIntent() throws Exception {
        Intent appIntent = mock(Intent.class);
        doReturn(null).when(appIntent).getStringExtra("redirect-uri");
        mydigipass.handleResult(1, 1, appIntent);
        verify(mockActivity, never()).onMDPAuthenticationSuccess(any(MDPResponse.class));
        verify(mockActivity, never()).onMDPAuthenticationFail(any(MDPResponse.class));
    }

    @Test
    public void testHandleResultNoCorrectUri() throws Exception {
        Intent appIntent = mock(Intent.class);
        doReturn("wrong-uri").when(appIntent).getStringExtra("redirect-uri");
        mydigipass.setRedirectUri("correct-uri");
        mydigipass.handleResult(1, 1, appIntent);
        verify(mockActivity, never()).onMDPAuthenticationSuccess(any(MDPResponse.class));
        verify(mockActivity, never()).onMDPAuthenticationFail(any(MDPResponse.class));
    }

    @Test
    public void testHandleResultSuccess() throws Exception {
        Intent appIntent = new Intent();
        Bundle bundle = new Bundle();

        bundle.putString("auth-code", "test");
        bundle.putString("scope", "profile");
        bundle.putString("state", "test");
        bundle.putString("redirect-uri", "correct-uri");
        bundle.putString("code", "test");

        appIntent.putExtras(bundle);

        mydigipass.setMDPAuthenticationListener(mockActivity);
        mydigipass.setRedirectUri("correct-uri");
        mydigipass.handleResult(1, 1, appIntent);

        verify(mockActivity, atLeastOnce()).onMDPAuthenticationSuccess(any(MDPResponse.class));
        verify(mockActivity, never()).onMDPAuthenticationFail(any(MDPResponse.class));
    }

    @Test
    public void testHandleResultError() throws Exception {
        Intent appIntent = new Intent();
        Bundle bundle = new Bundle();

        bundle.putString("redirect-uri", "correct-uri");
        bundle.putString("error", "test");
        bundle.putString("error_title", "doesnt work");
        bundle.putString("error_description", "this doesn't work");

        appIntent.putExtras(bundle);

        mydigipass.setMDPAuthenticationListener(mockActivity);
        mydigipass.setRedirectUri("correct-uri");
        mydigipass.handleResult(1, 1, appIntent);

        verify(mockActivity, never()).onMDPAuthenticationSuccess(any(MDPResponse.class));
        verify(mockActivity, atLeastOnce()).onMDPAuthenticationFail(any(MDPResponse.class));
    }

    @Test
    public void testOpenStore() {
        mydigipass.openStore();
        verify(mockActivity, atLeastOnce()).startActivity(any(Intent.class));
    }

    @Test
    public void testIfGeneratesCorrectOauthUrl() {
        mydigipass.setClientId("client");
        mydigipass.setRedirectUri("http://test.com");

        Map<String, String> extraParams = new HashMap<>();
        extraParams.put("test", "fubar");

        mydigipass.authenticate("1234", "eid_profile", extraParams);

        assertEquals(
                BuildConfig.MDP_FALLBACK_URL + "?client_id=client&redirect_uri=http%3A%2F%2Ftest.com&state=1234&scope=eid_profile&test=fubar&response_type=code",
                mydigipass.getOAuthUrl().toString());
    }

    @Test
    public void testIfGeneratesCorrectOauthUri() {
        mydigipass.setClientId("client");
        mydigipass.setRedirectUri("http://test.com");

        Map<String, String> extraParams = new HashMap<>();
        extraParams.put("test", "fubar");

        mydigipass.authenticate("1234", "eid_profile", extraParams);

        Uri uri = Uri.parse(BuildConfig.MDP_FALLBACK_URL);

        assertEquals(uri.getHost(), mydigipass.getOAuthUrl().getHost());
    }

    @Test
    public void testGetOAuthUrlWithoutScope() {
        mydigipass.setClientId("client");
        mydigipass.setRedirectUri("http://test.com");

        mydigipass.authenticate("1234", null, null);

        assertEquals(BuildConfig.MDP_FALLBACK_URL + "?client_id=client&redirect_uri=http%3A%2F%2Ftest.com&state=1234&response_type=code", mydigipass.getOAuthUrl().toString());
    }

    @Test
    public void testGetContext() {
        mydigipass.setContext(mockActivity);
        assertNotNull(mydigipass.getContext());
    }

    @Test
    public void testNoIntent() {
        assertFalse(mydigipass.canHandleResult(null));
    }

    @Test
    public void testOpenBrowser() {
        mydigipass.openBrowser();
        verify(mockActivity, atMost(1)).startActivity(any(Intent.class));
    }
}