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

Build:

	./gradlew -q assembleRelease

Run the following Maven command in this project, giving it a handle to your project via `DlocalRepositoryPath`

	mvn install:install-file \
	    -DgroupId=com.vasco.mydigipass.sdk \
	    -DartifactId=MYDIGIPASS_SDK \
	    -Dversion=1.0 \
	    -DgeneratePom=true \
	    -Dpackaging=aar \
	    -Dfile=MYDIGIPASS_SDK/build/libs/MYDIGIPASS_SDK.aar \
	    -DlocalRepositoryPath=/path/to/your/project_root/module/libs

Configure your project:

	repositories {
	  mavenCentral()
	  maven {
	    url "libs"
	  }
	}

	dependencies {
	  compile 'com.vasco.mydigipass.sdk:MYDIGIPASS_SDK:+'
	}

## Code

### Configuration and authentication request

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

Review [this sequence diagram](https://developer.mydigipass.com/mobile_app_authentication) and
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
