package com.vasco.mydigipass.sdk;

import android.content.DialogInterface;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, emulateSdk = 21, manifest = "MYDIGIPASS_SDK/src/main/AndroidManifest.xml", resourceDir = Config.DEFAULT_RES_FOLDER, assetDir = Config.DEFAULT_ASSET_FOLDER)
public class CancelButtonListenerTest {

    @Mock
    private MDPMobile mydigipass;

    @Mock
    private DialogInterface dialogInterface;
    private CancelButtonListener cancelButtonListener;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        cancelButtonListener = new CancelButtonListener(mydigipass);
        cancelButtonListener.onClick(dialogInterface, 0);
    }

    @Test
    public void testCancelClosesDialog() {
        verify(dialogInterface).dismiss();
    }

    @Test
    public void testCancelOpensBrowser() {
        verify(mydigipass).openBrowser();
    }
}
