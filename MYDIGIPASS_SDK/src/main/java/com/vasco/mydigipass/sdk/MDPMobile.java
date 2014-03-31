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
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

public class MDPMobile {

  private Activity context;
  private String clientId;
  private String state;
  private String redirectUri;
  private OnMDPAuthenticationListener mOnAuthenticatedListener;

  private static final String INTENT_NAME = BuildConfig.INTENT_NAME;
  private static final String INTENT_URI = BuildConfig.INTENT_URI;
  private static final String MYDIGIPASS_PACKAGE_NAME = BuildConfig.MYDIGIPASS_PACKAGE_NAME;
  public static final String MDP_FALLBACK_URL = BuildConfig.MDP_FALLBACK_URL;

  /**
   * Default constructor.
   * Provide the activity where you are going to implement the MYDIGIPASS.COM login button.
   * @param activity Activity that implements a MYDIGIPASS.COM login button.
   */
  public MDPMobile(Activity activity) {
    this.context = activity;
  }

  /**
   * Constructor
   * Provide your MYDIGIPASS.COM client id and mobile redirect uri.
   * @param activity Activity that implements a MYDIGIPASS.COM login button.
   * @param clientId Your MYDIGIPASS.COM client ID registered via developer.mydigipass.com.
   * @param redirectUri Your MYDIGIPASS.COM mobile redirect uri registered via developer.mydigipass.com.
   */
  public MDPMobile(Activity activity, String clientId, String redirectUri) {
    this(activity);
    setClientId(clientId);
    setRedirectUri(redirectUri);
  }

  /**
   * Returns the MYDIGIPASS.COM client ID.
   * @return clientID
   */
  public String getClientId() {
    return clientId;
  }

  /**
   * Sets the MYDIGIPASS.COM client ID.
   * @param clientId Your MYDIGIPASS.COM client ID registered via developer.mydigipass.com.
   */
  public void setClientId(String clientId) {
    this.clientId = clientId;
  }

  /**
   * Returns the OAuth state parameter set in the original authentication request.
   * See http://tools.ietf.org/html/rfc6749#section-10.12 for more information.
   * @return state
   */
  public String getState() {
    return state;
  }

  /**
   * Returns the set MYDIGIPASS.COM mobile redirect URI registered via developer.mydigipass.com.
   * @return redirect uri
   */
  public String getRedirectUri() {
    return redirectUri;
  }

  /**
   * Explicitly sets the MYDIGIPASS.COM mobile redirect URI.
   * @param redirectUri your MYDIGIPASS.COM redirectURI
   */
  public void setRedirectUri(String redirectUri) {
    this.redirectUri = redirectUri;
  }

  /**
   * Sets the MYDIGIPASS.COM authentication listener. This is used to do authentication callbacks.
   * @param mdpAuthenticationListener Class that implements the OnMDPAuthenticationListener.
   * @see com.vasco.mydigipass.sdk.OnMDPAuthenticationListener
   */
  public void setMDPAuthenticationListener(OnMDPAuthenticationListener mdpAuthenticationListener) {
    this.mOnAuthenticatedListener = mdpAuthenticationListener;
  }

  /**
   * Returns the MYDIGIPASS.COM authentication listener.
   * @return MYDIGIPASS.COM authentication listener.
   * @see com.vasco.mydigipass.sdk.OnMDPAuthenticationListener
   */
  public OnMDPAuthenticationListener getMDPAuthenticationListener() {
    return this.mOnAuthenticatedListener;
  }

  /**
   * Perform an authenticate request. If the app exists on the user's phone, it is going to open the
   * app. If the app is not installed a dialog will ask to install it or to perform the authentication
   * in a mobile browser instead.
   * @param state parameter to validate your request.
   */
  public void authenticate(String state) {
    this.state = state;
    Intent mdp = new Intent(INTENT_NAME, Uri.parse(INTENT_URI));
    mdp.putExtra("clientId", getClientId());
    mdp.putExtra("state", getState());
    mdp.putExtra("redirectUri", getRedirectUri());

    if(isMdpInstalled() && canMdpHandleIntent(mdp)) {
      context.startActivityForResult(mdp, 1);
    } else {
      try {
        buildDialog().show();
      } catch (IllegalArgumentException e) {
        throw new MDPException(e.getMessage(), e);
      }
    }
  }

  public void openStore() {
    // Open the play store with the MDP app.
    Intent intent = new Intent(Intent.ACTION_VIEW);
    intent.setData(Uri.parse("market://details?id=com.vasco.mydigipass"));
    context.startActivity(intent);
  }

  /**
   * Returns the configured MYDIGIPASS.COM OAuth url.
   * @return oauth url
   */
  public String getOAuthUrl() {
    try {

      String parameters = "response_type=code";
      parameters += "&client_id=" +  URLEncoder.encode(getClientId(), "utf-8");
      parameters += "&redirect_uri=" + URLEncoder.encode(getRedirectUri(), "utf-8");
      parameters += "&state=" + URLEncoder.encode(getState(), "utf-8");
      parameters += "&bundle_identifier=" + URLEncoder.encode(getPackageName(), "utf-8");
      return MDP_FALLBACK_URL + "/oauth/authenticate?" + parameters;

    }
    catch (UnsupportedEncodingException e) {
      throw new IllegalArgumentException("Special characters in your parameters are not allowed.", e);
    }
    catch (NullPointerException e) {
      throw new IllegalArgumentException("Not all parameters are properly filled in.", e);
    }
  }

  /**
   * Returns the MYDIGIPASS.COM OAuth url in URI format.
   * @return Uri of the Oauth url.
   */
  public Uri getOauthUri() {
    return Uri.parse(getOAuthUrl());
  }

  /**
   * Lifecycle call. Call it in your onCreate of your activity. It enables authentication
   * via the user's browser when the MYDIGIPASS.COM Authenticator For Mobile app is not installed.
   * If it's not an authentication request, this method does nothing.
   */
  public void webFlow() {
    Intent intent = context.getIntent();

    // Not sure if this condition is needed, but a bit more safety never hurts no one.
    if (intent != null) {
      Uri data = intent.getData();

      if (intent.getAction() != null && intent.getAction().equals(Intent.ACTION_VIEW) && data != null) {

        // We don't want this fired when users have multiple browser intents.
        if(data.toString() != null && isValidResponseRedirectUri(data.toString())) {
          try {

            String code = data.getQueryParameter("code");
            String state = data.getQueryParameter("state");
            MDPResponse response;

            if(code != null && state != null) {
              response = buildResponse(true, code, state, getRedirectUri());
              mOnAuthenticatedListener.onMDPAuthenticationSuccess(response);
            }
            else {
              response = buildResponse(false, code, state, getRedirectUri());
              mOnAuthenticatedListener.onMDPAuthenticationFail(response);
            }

          }
          catch(UnsupportedOperationException e) {
            mOnAuthenticatedListener.onMDPAuthenticationFail(buildResponse(false,null,null,getRedirectUri()));
            throw new MDPException("We could not parse the code and/or state parameter from your uri.", e);
          }
        }

      }
    }

  }

  public void setContext(Activity context) {
    this.context = context;
  }

  /**
   * Lifecycle call. Override the onActivityResult method of your activity and call this method. It
   * handles the intent from the MYDIGIPASS.COM app and does the callbacks on the interface.
   * @param requestCode requestCode
   * @param resultCode resultCode
   * @param data Intent from MYDIGIPASS.COM app
   */
  public void handleResult(int requestCode, int resultCode, Intent data) {

    if(canHandleResult(data)) {

      MDPResponse response;

      String code = data.getStringExtra("auth-code");
      String state = data.getStringExtra("state");

      if(isValidRequest(code, state)) {
        response = buildResponse(true,code, state, getRedirectUri());
        mOnAuthenticatedListener.onMDPAuthenticationSuccess(response);
      }
      else {
        response = buildResponse(false,code, state, getRedirectUri());
        mOnAuthenticatedListener.onMDPAuthenticationFail(response);
      }
    }

  }

  /**
   * Check if the MYDIGIPASS.COM app is installed on the user's phone.
   * @return MYDIGIPASS.COM app installed.
   */
  public boolean isMdpInstalled() {
    try {
      PackageManager manager = context.getPackageManager();

      if (manager != null) {
        manager.getPackageInfo(MYDIGIPASS_PACKAGE_NAME, PackageManager.GET_ACTIVITIES);
      }
      return true;
    } catch( PackageManager.NameNotFoundException e ) {
      return false;
    } catch (NullPointerException e) {
      return false;
    }
  }

  /**
   * Check if the MYDIGIPASS.COM app version installed can handle the oauth intent.
   * @return MYDIGIPASS.COM can handle intent?.
   */
  public boolean canMdpHandleIntent(Intent intent) {
    PackageManager manager = context.getPackageManager();
    List<ResolveInfo> infos = manager.queryIntentActivities(intent, 0);
    if (infos.size() > 0) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * Check if the intent comes from the MYDIGIPASS.COM App.
   * @param data data intent coming from the onActivityResult method.
   * @return can handle the result?
   */
  public boolean canHandleResult(Intent data) {
    if(data != null) {
      String uri = data.getStringExtra("redirect-uri");
      return uri != null && uri.equals(getRedirectUri());
    }
    return false;
  }

  /**
   * Check if the code and state parameters are provided.
   * @param code intent code
   * @param state intent state
   * @return does the request have a code and state?
   */
  private boolean isValidRequest(String code, String state) {
    return code != null && state != null && !code.trim().equals("") && !state.trim().equals("");
  }

  /**
   * Open the browser to the MYDIGIPASS.COM oauth url.
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
   * @return user's packagename.
   */
  private String getPackageName() {
    return context.getPackageName();
  }

  /**
   * Checks if the provided uri is a valid redirect uri.
   * @param foreignUri possible redirect uri
   * @return is valid redirectUri?
   */
  private boolean isValidResponseRedirectUri(String foreignUri) {
    String queryString = foreignUri.replace(getRedirectUri(), "");
    return queryString.startsWith("?");
  }

  /**
   * Builds a response object to send to the user.
   * @param success authentication successful?
   * @param code authorization code
   * @param state user's state parameter
   * @param redirectUri redirectUri.
   * @return MDPResponse
   */
  private MDPResponse buildResponse(boolean success, String code, String state, String redirectUri) {
    MDPResponse response = new MDPResponse();
    response.setAuthorizationCode(code);
    response.setState(state);
    response.setSuccess(success);
    response.setRedirectUri(redirectUri);
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

}
