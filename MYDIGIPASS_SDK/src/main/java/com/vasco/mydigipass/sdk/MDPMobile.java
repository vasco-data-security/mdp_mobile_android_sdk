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
import android.view.ContextThemeWrapper;

import com.vasco.mydigipass.sdk.oauth.OauthParameters;
import com.vasco.mydigipass.sdk.oauth.OauthUriBuilder;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

public class MDPMobile {

    private static final String INTENT_NAME = BuildConfig.INTENT_NAME;
    private static final String INTENT_URI = BuildConfig.INTENT_URI;
    private static final String MYDIGIPASS_PACKAGE_NAME = BuildConfig.MYDIGIPASS_PACKAGE_NAME;
    private Activity context;
    private String clientId;
    private String state;
    private String redirectUri;
    private String scope;
    private Map<String, String> passthroughParams;
    private OnMDPAuthenticationListener mOnAuthenticatedListener;

    /**
     * Default constructor.
     * Provide the activity where you are going to implement the MYDIGIPASS login button.
     *
     * @param activity Activity that implements a MYDIGIPASS login button.
     */
    public MDPMobile(Activity activity) {
        setContext(activity);
    }

    /**
     * Constructor
     * Provide your MYDIGIPASS client id and mobile redirect uri.
     *
     * @param activity    Activity that implements a MYDIGIPASS login button.
     * @param clientId    Your MYDIGIPASS client ID registered via developer.mydigipass.com.
     * @param redirectUri Your MYDIGIPASS mobile redirect uri registered via developer.mydigipass.com.
     */
    public MDPMobile(Activity activity, String clientId, String redirectUri) {
        this(activity);
        setClientId(clientId);
        setRedirectUri(redirectUri);
    }

    /**
     * Returns the MYDIGIPASS client ID.
     *
     * @return clientID
     */
    public String getClientId() {
        return clientId;
    }

    /**
     * Sets the MYDIGIPASS client ID.
     *
     * @param clientId Your MYDIGIPASS client ID registered via https://developer.mydigipass.com.
     */
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    /**
     * Returns the OAuth state parameter set in the original authentication request.
     * See http://tools.ietf.org/html/rfc6749#section-10.12 for more information.
     *
     * @return state
     */
    public String getState() {
        return state;
    }

    /**
     * Sets the OAuth state.
     *
     * @param state
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * Returns the set MYDIGIPASS.COM mobile redirect URI registered via developer.mydigipass.com.
     *
     * @return redirect uri
     */
    public String getRedirectUri() {
        return redirectUri;
    }

    /**
     * Explicitly sets the MYDIGIPASS mobile redirect URI.
     *
     * @param redirectUri your MYDIGIPASS redirectURI
     */
    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    /**
     * Space-delimited values used to determine which MYDIGIPASS user data can be consumed with the OAuth Access Token.
     *
     * @return scope
     */
    public String getScope() {
        return scope;
    }

    /**
     * Sets the MYDIGIPASS scope.
     *
     * @param scope
     */
    public void setScope(String scope) {
        this.scope = scope;
    }

    /**
     * Returns the MYDIGIPASS.COM authentication listener.
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
     * Perform an authenticate request. If the app exists on the user's phone, it is going to open the
     * app. If the app is not installed a dialog will ask to install it or to perform the authentication
     * in a mobile browser instead.
     *
     * @param state parameter to validate your request.
     * @throws MDPException
     */
    public void authenticate(String state) throws MDPException {
        authenticate(state, null, null);
    }

    /**
     * Perform an authenticate request. If the app exists on the user's phone, it is going to open the
     * app. If the app is not installed a dialog will ask to install it or to perform the authentication
     * in a mobile browser instead.
     *
     * @param state             An opaque value used by the client to maintain state between the request and callback, not null
     * @param scope             Space-delimited values used to determine which MYDIGIPASS user data can be consumed with the OAuth Access Token, may be null
     * @param passthroughParams Anything extra, may be null
     */
    public void authenticate(String state, String scope, Map<String, String> passthroughParams) {
        setState(state);
        setScope(scope);
        setPassthroughParams(passthroughParams);

        Intent mdp = new Intent(INTENT_NAME, Uri.parse(INTENT_URI));
        mdp.putExtra("clientId", getClientId());
        mdp.putExtra("redirectUri", getRedirectUri());
        mdp.putExtra("state", getState());

        if (getScope() != null) {
            mdp.putExtra("scope", getScope());
        }

        if (getPassthroughParams() != null) {
            for (Map.Entry<String, String> entry : getPassthroughParams().entrySet()) {
                mdp.putExtra(entry.getKey(), entry.getValue());
            }
        }

        openAppOrDialogForIntent(mdp);
    }

    public void openStore() {
        // Open the play store with the MDP app.
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=com.vasco.mydigipass"));
        context.startActivity(intent);
    }

    /**
     * Returns the configured MYDIGIPASS OAuth url.
     *
     * @return oauth url
     */
    protected String getOAuthUrl() {
        OauthParameters parameters = new OauthParameters();
        OauthUriBuilder uriBuilder = new OauthUriBuilder(BuildConfig.MDP_FALLBACK_URL);

        // Add anything extra to the URI
        parameters.addParameters(getPassthroughParams());

        parameters.addParameter("bundle_identifier", getPackageName());
        parameters.addParameter("client_id", getClientId());
        parameters.addParameter("redirect_uri", getRedirectUri());
        parameters.addParameter("state", getState());
        parameters.addParameter("scope", getScope());

        uriBuilder.setParameters(parameters);

        return uriBuilder.getUri(true).toString();
    }

    /**
     * Returns the MYDIGIPASS OAuth url in URI format.
     *
     * @return Uri of the Oauth url.
     */
    public Uri getOauthUri() {
        return Uri.parse(getOAuthUrl());
    }

    /**
     * Lifecycle call. Call it in your onCreate of your activity. It enables authentication
     * via the user's browser when the MYDIGIPASS Authenticator For Mobile app is not installed.
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
                    try {

                        String code = data.getQueryParameter("code");
                        String state = data.getQueryParameter("state");
                        MDPResponse response;

                        if (code != null && state != null) {
                            response = buildResponse(true, code, state, getRedirectUri(), getScope(), getPassthroughParams());
                            mOnAuthenticatedListener.onMDPAuthenticationSuccess(response);
                        } else {
                            response = buildResponse(false, code, state, getRedirectUri(), getScope(), getPassthroughParams());
                            mOnAuthenticatedListener.onMDPAuthenticationFail(response);
                        }

                    } catch (UnsupportedOperationException e) {
                        mOnAuthenticatedListener.onMDPAuthenticationFail(buildResponse(false, null, null, getRedirectUri(), getScope(), getPassthroughParams()));
                        throw new MDPException("We could not parse the code and/or state parameter from your uri.", e);
                    }
                }

            }
        }

    }

    /**
     * Lifecycle call. Override the onActivityResult method of your activity and call this method. It
     * handles the intent from the MYDIGIPASS app and does the callbacks on the interface.
     *
     * @param requestCode requestCode
     * @param resultCode  resultCode
     * @param data        Intent from the MYDIGIPASS app
     */
    public void handleResult(int requestCode, int resultCode, Intent data) {

        if (canHandleResult(data)) {

            MDPResponse response;

            String code = data.getStringExtra("auth-code");
            String state = data.getStringExtra("state");

            if (isValidRequest(code, state)) {
                response = buildResponse(true, code, state, getRedirectUri(), getScope(), getPassthroughParams());
                mOnAuthenticatedListener.onMDPAuthenticationSuccess(response);
            } else {
                response = buildResponse(false, code, state, getRedirectUri(), getScope(), getPassthroughParams());
                mOnAuthenticatedListener.onMDPAuthenticationFail(response);
            }
        }

    }

    /**
     * Check if the MYDIGIPASS app is installed on the user's phone.
     *
     * @return MYDIGIPASS app installed.
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
     * Check if the MYDIGIPASS app version installed can handle the oauth intent.
     *
     * @return MYDIGIPASS can handle intent?.
     */
    public boolean canMdpHandleIntent(Intent intent) {
        PackageManager manager = context.getPackageManager();
        List<ResolveInfo> infos = manager.queryIntentActivities(intent, 0);
        return infos.size() > 0;
    }

    /**
     * Check if the intent comes from the MYDIGIPASS App.
     *
     * @param data data intent coming from the onActivityResult method.
     * @return can handle the result?
     */
    public boolean canHandleResult(Intent data) {
        if (data != null) {
            String uri = data.getStringExtra("redirect-uri");
            return uri != null && uri.equals(getRedirectUri());
        }
        return false;
    }

    /**
     * Anything extra you want/need to be passed back to the redirect URI.
     *
     * @return
     */
    public Map<String, String> getPassthroughParams() {
        return passthroughParams;
    }

    /**
     * Sets passthrough parameters.
     *
     * @param passthroughParams
     */
    public void setPassthroughParams(Map<String, String> passthroughParams) {
        this.passthroughParams = passthroughParams;
    }

    public void setContext(Activity context) {
        this.context = context;
    }

    /**
     * Open the browser to the MYDIGIPASS oauth url.
     */
    protected void openBrowser() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, getOauthUri());
        context.startActivity(browserIntent);
    }

    protected Activity getContext() {
        return this.context;
    }

    /**
     * Returns the package name of the user his app.
     *
     * @return user's packagename.
     */
    private String getPackageName() {
        return context.getPackageName();
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
     * Builds a response object to send to the user.
     *
     * @param success     authentication successful?
     * @param code        authorization code
     * @param state       user's state parameter
     * @param redirectUri redirectUri.
     * @return MDPResponse
     */
    private MDPResponse buildResponse(boolean success, String code, String state, String redirectUri, String scope, Map<String, String> passthroughParams) {
        MDPResponse response = new MDPResponse();
        response.setAuthorizationCode(code);
        response.setState(state);
        response.setSuccess(success);
        response.setRedirectUri(redirectUri);
        response.setScope(scope);
        response.setPassthroughParams(passthroughParams);
        return response;
    }

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
     * Check if the code and state parameters are provided.
     *
     * @param code  intent code
     * @param state intent state
     * @return does the request have a code and state?
     */
    private boolean isValidRequest(String code, String state) {
        return code != null && state != null && !code.trim().equals("") && !state.trim().equals("");
    }
}
