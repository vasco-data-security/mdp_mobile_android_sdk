/*
 * Copyright (c) 2014  VASCO Data Security International GmbH. All rights reserved.
 */

package com.vasco.mydigipass.sdk;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextThemeWrapper;

import com.vasco.mydigipass.sdk.oauth.CollectionUtils;
import com.vasco.mydigipass.sdk.oauth.OauthUriBuilder;
import com.vasco.mydigipass.sdk.oauth.ResponseBuilder;
import com.vasco.mydigipass.sdk.oauth.models.Parameters;
import com.vasco.mydigipass.sdk.oauth.models.Response;

import java.util.List;
import java.util.Map;

public class MDPMobile {

    private static final String INTENT_NAME = BuildConfig.INTENT_NAME;
    private static final String INTENT_URI = BuildConfig.INTENT_URI;
    private static final String MYDIGIPASS_PACKAGE_NAME = BuildConfig.MYDIGIPASS_PACKAGE_NAME;
    private Activity context;
    private Parameters incomingParameters;
    private OnMDPAuthenticationListener mOnAuthenticatedListener;

    /**
     * Default constructor.
     *
     * @param activity Activity that implements a MYDIGIPASS login button.
     */
    public MDPMobile(Activity activity) {
        setContext(activity);
        this.incomingParameters = new Parameters();
    }

    /**
     * Alternative constructor.
     *
     * @param activity    Activity that implements a MYDIGIPASS login button.
     * @param clientId    Your MYDIGIPASS client ID registered via developer.mydigipass.com.
     * @param redirectUri Your MYDIGIPASS mobile redirect URI registered via developer.mydigipass.com.
     */
    public MDPMobile(Activity activity, String clientId, String redirectUri) {
        this(activity);
        setClientId(clientId);
        setRedirectUri(redirectUri);
    }

    /**
     * Returns the client ID.
     *
     * @return clientID
     */
    public String getClientId() {
        return this.incomingParameters.get("client_id");
    }

    /**
     * Sets the client ID.
     *
     * @param clientId Your MYDIGIPASS client ID registered via https://developer.mydigipass.com.
     */
    public void setClientId(String clientId) throws MDPException {
        if (clientId != null) {
            this.incomingParameters.put("client_id", clientId);
        } else {
            throw new MDPException("The client ID cannot be null");
        }
    }

    /**
     * Returns the configured redirect uri.
     *
     * @return redirect uri
     */
    public String getRedirectUri() {
        return this.incomingParameters.get("redirect_uri");
    }

    /**
     * Configures the redirect uri for your authentication request.
     *
     * @param redirectUri your MYDIGIPASS redirectURI
     */
    public void setRedirectUri(String redirectUri) throws MDPException {
        if (redirectUri != null) {
            this.incomingParameters.put("redirect_uri", redirectUri);
        } else {
            throw new MDPException("The redirect URI cannot be null");
        }
    }

    /**
     * Returns the listener.
     *
     * @return MYDIGIPASS authentication listener.
     * @see com.vasco.mydigipass.sdk.OnMDPAuthenticationListener
     */
    public OnMDPAuthenticationListener getMDPAuthenticationListener() {
        return this.mOnAuthenticatedListener;
    }

    /**
     * Sets the MYDIGIPASS authentication listener. This is used to do authentication callbacks.
     *
     * @param mdpAuthenticationListener Class that implements the OnMDPAuthenticationListener.
     * @see com.vasco.mydigipass.sdk.OnMDPAuthenticationListener
     */
    public void setMDPAuthenticationListener(OnMDPAuthenticationListener mdpAuthenticationListener) {
        this.mOnAuthenticatedListener = mdpAuthenticationListener;
    }

    /**
     * Performs the authentication. If the app exists on the user's phone, it's going to open the
     * app. If the app is not installed a dialog will ask to install it or to perform the authentication
     * in a mobile browser instead.
     *
     * @param state An opaque value used by the client to maintain state between the request and callback, not null
     */
    public void authenticate(String state) {
        authenticate(state, null, null);
    }

    /**
     * Performs the authentication. If the app exists on the user's phone, it's going to open the
     * app. If the app is not installed a dialog will ask to install it or to perform the authentication
     * in a mobile browser instead.
     *
     * @param state             An opaque value used by the client to maintain state between the request and callback, not null
     * @param scope             Space-delimited values used to determine which MYDIGIPASS user data can be consumed with the OAuth Access Token, may be null
     * @param passthroughParams Anything extra, may be null
     */
    public void authenticate(String state, String scope, Map<String, String> passthroughParams) {
        this.incomingParameters.put("state", state);
        this.incomingParameters.put("scope", scope);

        setPassthroughParams(passthroughParams);

        Intent mdp = new Intent(INTENT_NAME, Uri.parse(INTENT_URI));

        Bundle bundle = CollectionUtils.map2bundle(this.incomingParameters);
        mdp.putExtras(bundle);

        openAppOrDialogForIntent(mdp);
    }

    /**
     * Opens the marketplace place and redirects the user to the MYDIGIPASS app.
     */
    public void openStore() {
        // Open the play store with the MDP app.
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=com.vasco.mydigipass"));
        context.startActivity(intent);
    }

    /**
     * Detects and handles an incoming intent triggered by the MYDIGIPASS website.
     *
     * It enables authentication via the user's browser when the MYDIGIPASS App is not installed.
     * If it's not an authentication request, this method does nothing.
     */
    public void webFlow() {
        Intent intent = context.getIntent();

        // Not sure if this condition is needed, but a bit more safety never hurts no one.
        if (intent != null) {
            Uri data = intent.getData();

            if (intent.getAction() != null && intent.getAction().equals(Intent.ACTION_VIEW) && data != null) {

                // We don't want this fired when users have multiple browser intents.
                if (data.toString() != null && isValidResponseRedirectUri(data.toString())) {
                    Response response = ResponseBuilder.build(data);
                    MDPResponse mdpResponse = new MDPResponse(response);
                    callback(mdpResponse);
                }

            }
        }
    }

    /**
     * Checks and handles the incoming intent onActivityResult. Use this method in the OnActivityResult callback.
     *
     * @param requestCode requestCode
     * @param resultCode  resultCode
     * @param data        Intent from the MYDIGIPASS app
     */
    public void handleResult(int requestCode, int resultCode, Intent data) {
        if (canHandleResult(data)) {
            Response response = ResponseBuilder.build(data.getExtras());
            MDPResponse mdpResponse = new MDPResponse(response);
            callback(mdpResponse);
        }
    }

    /**
     * Checks if the MYDIGIPASS App is installed on the device.
     *
     * @return boolean
     */
    public boolean isMdpInstalled() {
        try {
            PackageManager manager = context.getPackageManager();

            if (manager != null) {
                manager.getPackageInfo(MYDIGIPASS_PACKAGE_NAME, PackageManager.GET_ACTIVITIES);
            }
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        } catch (NullPointerException e) {
            return false;
        }
    }

    /**
     * Checks if there are activities that can run the specific intent.
     *
     * As most of the MYDIGIPASS users now use at least version 2.0 where app2app is implemented,
     * we don't need to check anymore if the intent is possible.
     *
     * @deprecated
     * @return boolean
     */
    @Deprecated
    public boolean canMdpHandleIntent(Intent intent) {
        PackageManager manager = context.getPackageManager();
        List<ResolveInfo> infos = manager.queryIntentActivities(intent, 0);
        return infos.size() > 0;
    }

    /**
     * Checks if the incoming intent is coming from the MDP app.
     *
     * @param intent data intent coming from the onActivityResult method.
     * @return boolean
     */
    public boolean canHandleResult(Intent intent) {
        if (intent != null) {
            String uri = intent.getStringExtra("redirect-uri");
            return uri != null && uri.equals(getRedirectUri());
        }
        return false;
    }

    /**
     * Takes a map of key/values and adds them to the oauth request. When a reserved keyword is used
     * this throws an MDPException.
     *
     * @see com.vasco.mydigipass.sdk.oauth.OAuthReservedParameters#RESERVED_PARAMETERS for a list of reserved keywords.
     * @param parameters map with key values
     * @throws MDPException
     */
    public void setPassthroughParams(Map<String, String> parameters) {
        this.incomingParameters.addPassthroughParameters(parameters);
    }

    /**
     * Sets the current Activity to participate in lifecycle callbacks.
     * @param activity
     */
    public void setContext(Activity activity) {
        this.context = activity;
    }

    /**
     * Generates the complete OAuth uri.
     *
     * @return Uri
     */
    protected Uri getOAuthUrl() {
        OauthUriBuilder uriBuilder = new OauthUriBuilder(BuildConfig.MDP_FALLBACK_URL);
        uriBuilder.setParameters(this.incomingParameters);
        return uriBuilder.getUri();
    }

    /**
     * Opens a browser and redirects the user to the generated oauth url.
     */
    protected void openBrowser() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, getOAuthUrl());
        context.startActivity(browserIntent);
    }

    /**
     * Returns the given context.
     *
     * @return Activity
     */
    protected Activity getContext() {
        return this.context;
    }

    /**
     * Returns the incoming parameters set by the ASP.
     *
     * @return Parameters
     */
    protected Parameters getIncomingParameters() {
        return incomingParameters;
    }

    /**
     * Checks if the provided uri is a valid redirect uri.
     *
     * @param foreignUri possible redirect uri
     * @return is valid redirectUri?
     */
    private boolean isValidResponseRedirectUri(String foreignUri) {
        String queryString = foreignUri.replace(getRedirectUri(), "");
        return queryString.startsWith("?");
    }

    /**
     * Returns a dialog with 2 options. Install the app or use the webflow.
     *
     * @return AlertDialog
     */
    private AlertDialog buildDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, android.R.style.Theme_Holo_Light));

        if (isMdpInstalled()) {
            builder.setMessage(context.getString(R.string.update_app_text)).setTitle(context.getString(R.string.title_dialog));
            builder.setPositiveButton(context.getString(R.string.update_app), new OkButtonListener(this));
        } else {
            builder.setMessage(context.getString(R.string.install_app_text)).setTitle(context.getString(R.string.title_dialog));
            builder.setPositiveButton(context.getString(R.string.install_app), new OkButtonListener(this));
        }
        builder.setNegativeButton(context.getString(R.string.no_thanks), new CancelButtonListener(this));
        return builder.create();
    }

    private void openAppOrDialogForIntent(Intent mdp) throws MDPException {
        if (isMdpInstalled() && canMdpHandleIntent(mdp)) {
            // Authenticate with MDP app
            context.startActivityForResult(mdp, 1);
        } else {
            try {
                // Authenticate through browser
                buildDialog().show();
            } catch (IllegalArgumentException e) {
                throw new MDPException(e.getMessage(), e);
            }
        }
    }

    /**
     * Handles the callback back to the app that implemented this SDK.
     *
     * @param response can be null or a {@link MDPResponse} object.
     */
    private void callback(MDPResponse response) {
        if (response != null) {
            if (!response.containsError()) {
                mOnAuthenticatedListener.onMDPAuthenticationSuccess(response);
            } else {
                mOnAuthenticatedListener.onMDPAuthenticationFail(response);
            }
        } else {
            mOnAuthenticatedListener.onMDPAuthenticationFail(null);
        }
    }
}
