package com.vasco.mydigipass.sdk.oauth;

import android.net.Uri;

import com.vasco.mydigipass.sdk.BuildConfig;
import com.vasco.mydigipass.sdk.oauth.models.Response;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, emulateSdk = 21, manifest = "MYDIGIPASS_SDK/src/main/AndroidManifest.xml", resourceDir = Config.DEFAULT_RES_FOLDER, assetDir = Config.DEFAULT_ASSET_FOLDER)
public class ResponseBuilderTest {

    @Test
    public void testBuildByIncorrectUri() {
        Uri uri = Uri.parse("http://no-prams.com");
        Response response = ResponseBuilder.build(uri);

        assertNull(response);
    }

    @Test
    public void testBuildByCorrectUriWithErrors() {
        Uri uri = Uri.parse("http://test.com?error=fubar&error_description=test&error_title=lol");
        Response response = ResponseBuilder.build(uri);

        assertEquals("fubar", response.getError().getKey());
    }

    @Test
    public void testBuildByCorrectUriNoErrors() {
        Uri uri = Uri.parse("http://test.com?code=fubar");
        Response response = ResponseBuilder.build(uri);

        assertEquals("fubar", response.getParameters().get("code"));
        assertNull(response.getError());
    }

}
