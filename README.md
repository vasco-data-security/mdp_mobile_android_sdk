# MYDIGIPASS Mobile App Authentication SDK for Android

## About the SDK

The SDK connects your mobile application with the MYDIGIPASS Authenticator allowing you to reuse your server integration of the Secure Connect API in your mobile app.

* Learn more about MYDIGIPASS:
    * [https://www.mydigipass.com/](https://www.mydigipass.com/)
    * [https://developer.mydigipass.com/](https://developer.mydigipass.com/)
* Learn more about the Secure Connect API and the mobile SDK:
    * [https://developer.mydigipass.com/introduction](https://developer.mydigipass.com/introduction)
    * [https://developer.mydigipass.com/mobile_integration](https://developer.mydigipass.com/mobile_integration)

## Installation

1. Download the latest version of our SDK in [the release section](https://github.com/vasco-data-security/mdp_mobile_android_sdk/releases)
2. Move the binary file into your app's *libs* folder; The file extension is **.aar**.
3. Add the `compile(name: 'mydigipass-sdk-file-name-in-the-libs-folder', ext: 'aar')` dependency to the app's *build.gradle* file
4. Rebuild your application

## Getting started

### Client ID, bundle identifier and redirect URI for your mobile app

To configure the `MDPMobile` instance you need to use a MYDIGIPASS **client ID** and register your **bundle identifier** and a **redirect URI** specific to your mobile app:

0. Create an application at [https://developer.mydigipass.com/](https://developer.mydigipass.com/) to get a **client ID**
0. Register the **bundle identifier** of your mobile app and the **redirect URI** by editing the *OAuth URIs* of your application

Example:

* **bundle identifier**: `com.yourcompany.com.your-app`
* **redirect URI**: `your-app://mydigipass-login`

### Configuring the mobile app redirect URI

Once you have done the above, it's time to configure your Android project by registering your mobile **redirect URI** so that we can redirect the user back to your mobile app on successful login.

In the example below we register `MyActivity` in the *AndroidManifest.xml* file of your app to open when a person gets redirected to `your-app://mydigipass-login`:

```xml
  <activity android:name="MyActivity">
    <intent-filter>
      <action android:name="android.intent.action.VIEW"/>

      <category android:name="android.intent.category.DEFAULT"/>
      <category android:name="android.intent.category.BROWSABLE"/>

      <data
          android:host="mydigipass-login"
          android:scheme="your-app"
          />
    </intent-filter>
  </activity>
```

*Note:*

If you know nothing about *Intents* and *Intent Filters* we suggest you to read the documentation at [http://developer.android.com/guide/components/intents-filters.html](http://developer.android.com/guide/components/intents-filters.html).

More information about how the example above works can be found at [http://developer.android.com/training/app-indexing/deep-linking.html](http://developer.android.com/training/app-indexing/deep-linking.html).

### Initialization of the SDK

Initialize the SDK in the `onCreate` callback method of your *Activity*:

```java
public class MyActivity extends Activity {

  private MDPMobile mydigipass;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // Initialize the MYDIGIPASS SDK.
    mydigipass = new MDPMobile(this);
  }
}
```

### Using your Client ID and mobile app redirect URI with the SDK

Configuring your mobile **redirect URI** and **client ID** in your *Activity*:

```java
public class MyActivity extends Activity {

  private MDPMobile mydigipass;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // Initialize the MYDIGIPASS SDK.
    mydigipass = new MDPMobile(this);

    // Configure your MYDIGIPASS client ID.
    mydigipass.setClientId("your-mdp-client-id");
    // Configure your MYDIGIPASS redirect URI.
    mydigipass.setRedirectUri("your-app://mydigipass-login");

    // Detects when a redirect is coming back into the app.
    mydigipass.webFlow();
  }
}
```

### Performing an authentication request

To perform an authentication request you need to call the `authenticate` method on the `MDPMobile` instance.

This method accepts three parameters:

0. `state`, **mandatory**, may not be `null`
0. `scope`, **optional**, may be `null`
0. `passthroughParams`, **optional**, may be `null`

A brief description about these parameters, more information can be found at [https://developer.mydigipass.com/](https://developer.mydigipass.com/):

* **State:** To track state, e.g. to remember that a user pressed the *Secure Login Button* on your application’s user profile page and to prevent [CSRF](https://www.owasp.org/index.php/Cross-Site_Request_Forgery_%28CSRF%29) attacks.
* **Scope:** The scope of the user data you want to retrieve (e.g. email).
* **Passthrough parameters:** Any other parameters you want use to pass information and/or state to your server's redirect endpoint.

*Example:*

```java
public class MyActivity extends Activity implements OnMDPAuthenticationListener {

  private MDPMobile mydigipass;

  ...

  public void myButtonClick(View view) {
      Map<String, String> passthroughParameters = new HashMap<>();
      passthroughParameters.put("redirect_to", "dashboard");
      passthroughParameters.put("new_user", "yes");
      
      mydigipass.authenticate("xyzabc1234567", "email phone", passthroughParameters);
  }
}
```

*Notes:*

* Review [this sequence diagram](https://developer.mydigipass.com/mobile_integration) and the [OAuth 2.0 spec](http://tools.ietf.org/html/rfc6749#section-10.12) for more info about the `state` parameter.
* Review [this sequence diagram](https://developer.mydigipass.com/mobile_integration) and the [OAuth 2.0 spec](http://tools.ietf.org/html/rfc6749#section-3.3) for more info about the `scope` parameter.
* The [valid scopes](https://developer.mydigipass.com/reference_guide_button#_user_data_authorization_scope_values) are listed at [https://developer.mydigipass.com](https://developer.mydigipass.com).

#### Callbacks

When an authentication has been performed by the SDK it will trigger a callback.

Use the `OnMDPAuthenticationListener` listener in your *Activity* and implement the `onMDPAuthenticationSuccess` and `onMDPAuthenticationFail` methods to execute code on authentication success or failure:

```java
public class MyActivity extends Activity implements OnMDPAuthenticationListener {

  ...

  @Override
  public void onMDPAuthenticationSuccess(MDPResponse response) {
    String authorizationCode = response.getAuthorizationCode();
    String state = response.getState();
  }

  @Override
  public void onMDPAuthenticationFail(MDPResponse response) {
    if(response != null) {
      Error error = response.getError();
    }
  }
}
```

*Note:* More information about the MDPResponse object can be found in the attached javadoc.