# MYDIGIPASS.COM Mobile App Authentication SDK for Android

## About the SDK

The SDK connects your mobile application with the MYDIGIPASS.COM Authenticator for Mobile
allowing you to reuse your server integration of the Secure Connect API in your mobile app.

* Learn more about MYDIGIPASS.COM:
    * https://www.mydigipass.com/
    * https://developer.mydigipass.com/
* Learn more about the SDK and the Secure Connect API:
    * https://developer.mydigipass.com/getting_started
    * https://developer.mydigipass.com/mobile_app_authentication

## Installation

1. Download the aar file in the release section.
2. put the aar file in your app's lib folder.
3. Add following dependency into your gradle file: compile(name: 'MYDIGIPASS_SDK-1.0', ext: 'aar')
4. Rebuild your application.

## Using the MYDIGIPASS SDK

In the `onCreate` LifeCycle callback of your activity:

	mydigipass = new MDPMobile(this);

To configure the `MDPMobile` instance you need to use a MYDIGIPASS.COM production _client id_ and
registered _mobile app redirect URI_. See https://developer.mydigipass.com for more info.

	mydigipass.setClientId("your-mdp-client-id");
	mydigipass.setRedirectUri("yourcompany-app://mydigipass-sdk-callback");

More LifeCycle configuration:

	mydigipass.setMDPAuthenticationListener(this);
	mydigipass.webFlow();

Performing the actual authentication, also passing server-side generated OAuth state:

	mydigipass.authenticate("xyzabc1234567");

Review [this sequence diagram](https://developer.mydigipass.com/mobile_integration) and
the [OAuth 2.0 spec](http://tools.ietf.org/html/rfc6749#section-10.12) for more info about the state parameter.

### Callbacks

Implement the `OnMDPAuthenticationListener` interface in your activity and override `onMDPAuthenticationFail` and `onMDPAuthenticationSucces`:

	public class YourActivity extends Activity implements OnMDPAuthenticationListener

	@Override
	public void onMDPAuthenticationSuccess(MDPResponse response) {
	    String authorizationCode = response.getAuthorizationCode();
	    String state = response.getState();

	    // Pass authorizationCode to your server-side implementation of the Secure Connect API
	    // See https://developer.mydigipass.com/mobile_app_authentication
	}

	@Override
	public void onMDPAuthenticationFail(MDPResponse response) {
        // Handle errors
	}

Override `onActivityResult`, see if the SDK can handle the result by calling `canHandleResult`
and call `handleResult` with `requestCode`, `resultCode` and `data` parameters.

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if(mydigipass.canHandleResult(data)) {
	      mydigipass.handleResult(requestCode, resultCode, data);
	    }
	}

