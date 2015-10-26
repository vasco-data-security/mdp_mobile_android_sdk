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

1. Download the [binary distribution](https://github.com/vasco-data-security/mdp_mobile_android_sdk/releases/download/1.0/MYDIGIPASS_SDK-1.0.aar) of our SDK
2. Move this binary file into your app's *libs* folder
3. Add the `compile(name: 'MYDIGIPASS_SDK-1.0', ext: 'aar')` dependency to the app's *build.gradle* file
4. Rebuild your application

## Using the MYDIGIPASS SDK

### Initialization

Initialize the SDK in the `onCreate` method of your *Activity*. For example:

```java
public class Activity extends ApplicationContext {
  private static MDPMobile mdpMobile;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    // SDK initialization
    mdpMobile = new MDPMobile(this);
  }
}
```

### Configuration

#### OAuth

To configure the `MDPMobile` instance you need to use a MYDIGIPASS production *client id* and registered *mobile app redirect URI*. See [https://developer.mydigipass.com](https://developer.mydigipass.com) for more info. For example:

```java
public class Activity extends ApplicationContext {
  private static MDPMobile mdpMobile;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    // SDK initialization
    mdpMobile = new MDPMobile(this);
    
    // OAuth configuration
    mdpMobile.setClientId("your-mdp-client-id");
    mdpMobile.setRedirectUri("yourcompany-app://mydigipass-sdk-callback");
    
    // When a user doesn't have the MYDIGIPASS app we can fallback to the website.
    mdpMobile.webFlow();
  }
}
```

#### Callbacks

Implement the `OnMDPAuthenticationListener` interface in your *Activity* and override the `onMDPAuthenticationFail` and `onMDPAuthenticationSuccess` methods. For example:

```java
public class Activity extends ApplicationContext implements OnMDPAuthenticationListener {
  private static MDPMobile mdpMobile;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    // SDK initialization
    mdpMobile = new MDPMobile(this);
    
    // OAuth configuration
    mdpMobile.setClientId("your-mdp-client-id");
    mdpMobile.setRedirectUri("yourcompany-app://mydigipass-sdk-callback");
    
    // Authentication callback initialization
    mdpMobile.setMDPAuthenticationListener(this);
  }
  
  @Override
  public void onMDPAuthenticationSuccess(MDPResponse mdpResponse) {
    String authorizationCode = response.getAuthorizationCode();
    String state = response.getState();
  
    // Pass authorizationCode to your server-side implementation of the Secure Connect API
    // See https://developer.mydigipass.com/mobile_integration#mobile_app_sdk
  }

  @Override
  public void onMDPAuthenticationFail(MDPResponse mdpResponse) {
    // Handle errors
  }
}
```

### Authentication

#### State (required)

To perform the actual authentication, pass the mandatory server-side generated OAuth *state*:

```java
mdpMobile.authenticate("xyzabc1234567");
```

Review [this sequence diagram](https://developer.mydigipass.com/mobile_integration) and the [OAuth 2.0 spec](http://tools.ietf.org/html/rfc6749#section-10.12) for more info about the *state* parameter.

#### Scopes (optional)

We use scopes as a mechanism to determine which data your oauth token can consume. To define what data you want to consume, you must define a *scope* value.
It's possible to define multiple scopes:

```java
// This method overloads the default authenticate method and accepts three parameters instead of one.
// 1. State
// 2. Scopes
// 3. Passthrough parameters
mdpMobile.authenticate("xyzabc1234567", "email phone", null);
```

The [valid scopes](https://developer.mydigipass.com/reference_guide_button#_user_data_authorization_scope_values) are listed on [https://developer.mydigipass.com](https://developer.mydigipass.com).

Review [this sequence diagram](https://developer.mydigipass.com/mobile_integration) and the [OAuth 2.0 spec](http://tools.ietf.org/html/rfc6749#section-3.3) for more info about the *scope* parameter.

#### Passthrough parameters (optional)

It's also possible to pass additional parameters through the OAuth request as a key value map. These parameters will be available again to consume after the request has been made:

```java
// This method overloads the default authenticate method and accepts three parameters instead of one.
mdpMobile.authenticate("xyzabc1234567", null, Map<String, String> mdpPassthroughParams);
```