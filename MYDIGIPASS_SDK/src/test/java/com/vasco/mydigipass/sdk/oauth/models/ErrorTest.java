package com.vasco.mydigipass.sdk.oauth.models;


import com.vasco.mydigipass.sdk.BuildConfig;
import com.vasco.mydigipass.sdk.oauth.enums.ErrorType;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static junit.framework.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, emulateSdk = 21, manifest = "MYDIGIPASS_SDK/src/main/AndroidManifest.xml", resourceDir = Config.DEFAULT_RES_FOLDER, assetDir = Config.DEFAULT_ASSET_FOLDER)
public class ErrorTest {

    private Error error;

    @Before
    public void setup() {
        this.error = new Error();
    }

    @Test
    public void testSetGetKey() {
        error.setKey("test");
        assertEquals("test", error.getKey());
    }

    @Test
    public void testSetGetDescription() {
        error.setDescription("lol");
        assertEquals("lol", error.getDescription());
    }

    @Test
    public void testSetGetTitle() {
        error.setTitle("fubar");
        assertEquals("fubar", error.getTitle());
    }

    @Test
    public void testGetTypeInvalidRequest() {
        error.setKey("invalid_request");
        assertEquals(ErrorType.INVALID_REQUEST, error.getType());
    }

    @Test
    public void testGetTypeNotAuthenticated() {
        error.setKey("not_authenticated");
        assertEquals(ErrorType.NOT_AUTHENTICATED, error.getType());
    }

    @Test
    public void testGetTypeAccountDisabled() {
        error.setKey("account_disabled");
        assertEquals(ErrorType.ACCOUNT_DISABLED, error.getType());
    }

    @Test
    public void testGetTypeAccountNoPermissions() {
        error.setKey("account_no_permissions");
        assertEquals(ErrorType.NO_PERMISSIONS, error.getType());
    }

    @Test
    public void testGetTypeAuthenticationMethodNotAllowed() {
        error.setKey("authentication_method_not_allowed");
        assertEquals(ErrorType.AUTHENTICATION_METHOD_NOT_ALLOWED, error.getType());
    }

    @Test
    public void testGetTypeEidNotAllowed() {
        error.setKey("eid_scopes_present_but_not_allowed");
        assertEquals(ErrorType.EID_SCOPE_NOT_ALLOWED, error.getType());
    }

    @Test
    public void testGetUnknownKey() {
        error.setKey("fubar");
        assertEquals(ErrorType.UNKNOWN_ERROR_KEY, error.getType());
    }
}
