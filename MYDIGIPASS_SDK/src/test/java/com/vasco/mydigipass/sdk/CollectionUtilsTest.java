package com.vasco.mydigipass.sdk;

import android.os.Bundle;

import com.vasco.mydigipass.sdk.oauth.CollectionUtils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, emulateSdk = 21, manifest = "MYDIGIPASS_SDK/src/main/AndroidManifest.xml", resourceDir = Config.DEFAULT_RES_FOLDER, assetDir = Config.DEFAULT_ASSET_FOLDER)
public class CollectionUtilsTest {

    @Test
    public void testBundle2Map() {
        Bundle bundle = new Bundle();
        bundle.putString("test", "lol");

        Map<String, String> map = CollectionUtils.bundle2map(bundle);

        assertEquals("lol", map.get("test"));
    }

    @Test
    public void testMap2Bundle() {
        Map<String, String> map = new HashMap<>();

        map.put("test", "lol");

        Bundle bundle = CollectionUtils.map2bundle(map);

        assertEquals("lol", bundle.getString("test"));
    }

}
