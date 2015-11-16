package com.vasco.mydigipass.sdk.oauth.models;

import com.vasco.mydigipass.sdk.BuildConfig;
import com.vasco.mydigipass.sdk.MDPException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, emulateSdk = 21, manifest = "MYDIGIPASS_SDK/src/main/AndroidManifest.xml", resourceDir = Config.DEFAULT_RES_FOLDER, assetDir = Config.DEFAULT_ASSET_FOLDER)
public class ParametersTest {

    @Test
    public void testFromMap() {
        Map<String, String> test = new HashMap<>();
        test.put("fu", "bar");

        Parameters parameters = Parameters.fromMap(test);

        assertEquals("bar", parameters.get("fu"));
    }

    @Test
    public void testPutValue() {
        Parameters parameters = new Parameters();
        parameters.put("test", "foo");

        assertEquals("foo", parameters.get("test"));
    }

    @Test
    public void testPutNull() {
        Parameters parameters = new Parameters();
        parameters.put("test", null);

        assertFalse(parameters.containsKey("test"));
    }

    @Test
    public void testAddPassthroughParametersValue() {
        Parameters parameters = new Parameters();
        Map<String, String> map = new HashMap<>();

        map.put("test", "fubar");

        parameters.addPassthroughParameters(map);

        assertEquals("fubar", parameters.get("test"));
    }

    @Test
    public void testAddPassthroughParametersNull() {
        Parameters parameters = new Parameters();
        // Check if the method can handle null values and doesn't crash.
        parameters.addPassthroughParameters(null);
    }

    @Test(expected = MDPException.class)
    public void testAddPassthroughParametersReservedKeyword() {
        Parameters parameters = new Parameters();

        Map<String, String> map = new HashMap<>();
        map.put("is_sandbox", "test");

        parameters.addPassthroughParameters(map);
    }

    @Test
    public void testGetPassthroughParamsWithValues() {
        Parameters parameters = new Parameters();

        parameters.put("test", "foo");
        parameters.put("is_sandbox", "true");

        assertEquals(1, parameters.getPassthroughParams().size());
    }

    @Test
    public void testGetPassthroughParamsWithNoValues() {
        Parameters parameters = new Parameters();
        parameters.put("is_sandbox", "true");

        assertEquals(0, parameters.getPassthroughParams().size());
    }
}
