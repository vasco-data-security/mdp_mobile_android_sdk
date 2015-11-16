package com.vasco.mydigipass.sdk;

import com.vasco.mydigipass.sdk.oauth.models.Error;
import com.vasco.mydigipass.sdk.oauth.models.Parameters;
import com.vasco.mydigipass.sdk.oauth.models.Response;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, emulateSdk = 21, manifest = "MYDIGIPASS_SDK/src/main/AndroidManifest.xml", resourceDir = Config.DEFAULT_RES_FOLDER, assetDir = Config.DEFAULT_ASSET_FOLDER)
public class MDPResponseTest {

    private MDPResponse successMdpResponse;
    private MDPResponse errorMdpResponse;

    @Before
    public void setup() {
        Parameters successParameters = new Parameters();
        successParameters.put("code", "test");
        successParameters.put("state", "st");
        successParameters.put("redirect-uri", "http://");
        successParameters.put("passthrough", "test");

        Response successResponse = new Response();

        successResponse.setParameters(successParameters);

        this.successMdpResponse = new MDPResponse(successResponse);

        com.vasco.mydigipass.sdk.oauth.models.Error error = new Error();

        error.setKey("invalid_request");
        error.setTitle("wrong client id");
        error.setDescription("wrong client id");

        Response errorResponse = new Response();

        errorResponse.setError(error);

        this.errorMdpResponse = new MDPResponse(errorResponse);
    }

    @Test
    public void testGetState() {
        assertEquals("st", this.successMdpResponse.getState());
    }

    @Test
    public void testGetAuthorizationCode() {
        assertEquals("test", this.successMdpResponse.getAuthorizationCode());
    }

    @Test
    public void testGetRedirectUrl() {
        assertEquals("http://", this.successMdpResponse.getRedirectUri());
    }

    @Test
    public void testGetScopeNull() {
        assertNull(this.successMdpResponse.getScope());
    }

    @Test
    public void testGetScopeValue() {
        this.successMdpResponse.getResponse().getParameters().put("scope", "eid");
        assertEquals("eid", this.successMdpResponse.getScope());
    }

    @Test
    public void testGetPassthroughParameters() {
        Map<String, String> map = this.successMdpResponse.getPassthroughParams();
        assertEquals("test", map.get("passthrough"));
    }

    @Test
    public void testGetError() {
        assertEquals("invalid_request", this.errorMdpResponse.getError().getKey());
    }
}
