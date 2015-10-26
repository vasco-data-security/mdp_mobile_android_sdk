/*
 * Copyright (c) 2014  VASCO Data Security International GmbH. All rights reserved.
 */

package com.vasco.mydigipass.sdk;

import android.content.DialogInterface;

public class CancelButtonListener implements DialogInterface.OnClickListener {

    private MDPMobile mdpMobile;

    public CancelButtonListener(MDPMobile mdpMobile) {
        this.mdpMobile = mdpMobile;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        // Open the play store.
        dialog.dismiss();
        mdpMobile.openBrowser();
    }

}
