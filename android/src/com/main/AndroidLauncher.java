package com.main;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;

import java.util.Arrays;

import data.Statics;
import view.MainView;

public class AndroidLauncher extends AndroidApplication {
	private static Context context;
	/**
	 * ATTENTION: This was auto-generated to implement the App Indexing API.
	 * See https://g.co/AppIndexing/AndroidStudio for more information.
	 */
	//private GoogleApiClient client;

	private static final String[] SCOPES = { CalendarScopes.CALENDAR_READONLY };
	private static final String PREF_ACCOUNT_NAME = "accountName";
	static final int REQUEST_ACCOUNT_PICKER = 1000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();

		if (getContext().checkCallingOrSelfPermission("android.permission.INTERNET") == PackageManager.PERMISSION_GRANTED) {
			//Gdx.app.log("INFO", "PERMISSION GRANTED");
			System.out.println("Permission granted");
			context = getContext();
		} else {
			//Gdx.app.log("ERROR", "PERMISSION DENIED");
			System.out.println("Permission denied");
			Gdx.app.exit();
		}

//		try {
//			Context con = getApplicationContext();
//			//con.startActivity();
//			// Initialize credentials and service object.
//			GoogleAccountCredential mCredential = GoogleAccountCredential.usingOAuth2(
//					context, Arrays.asList(SCOPES))
//					.setBackOff(new ExponentialBackOff());
//			con.getApplicationContext();
//			System.out.println("Context fetched");
//			//DownloadController dc = new DownloadController(mCredential);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		try{
			String accountName = getPreferences(Context.MODE_PRIVATE)
					.getString(PREF_ACCOUNT_NAME, null);

			Statics.GoogleCredential = GoogleAccountCredential.usingOAuth2(
					context, Arrays.asList(SCOPES))
					.setBackOff(new ExponentialBackOff());
			//startActivityForResult(Statics.GoogleCredential.newChooseAccountIntent(),REQUEST_ACCOUNT_PICKER);
			Statics.GoogleCredential.newChooseAccountIntent();
			Thread.sleep(500);
			//Statics.GoogleCredential = GoogleAccountCredential.usingOAuth2()
			//isGooglePlayServicesAvailable();
			//Statics.GoogleCredential = mCredential;
		}
		catch (Exception e){
			System.out.println("Failed getting credentials");
			System.exit(0);
		}

		initialize(new MainView(true), config);

		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		//client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
	}

//	@Override
//	public void onStart() {
//		super.onStart();
//
//		// ATTENTION: This was auto-generated to implement the App Indexing API.
//		// See https://g.co/AppIndexing/AndroidStudio for more information.
//		client.connect();
//		Action viewAction = Action.newAction(
//				Action.TYPE_VIEW, // TODO: choose an action type.
//				"AndroidLauncher Page", // TODO: Define a title for the content shown.
//				// TODO: If you have web page content that matches this app activity's content,
//				// make sure this auto-generated web page URL is correct.
//				// Otherwise, set the URL to null.
//				Uri.parse("http://host/path"),
//				// TODO: Make sure this auto-generated app deep link URI is correct.
//				Uri.parse("android-app://com.main/http/host/path")
//		);
//		AppIndex.AppIndexApi.start(client, viewAction);
//	}
//
//	@Override
//	public void onStop() {
//		super.onStop();
//
//		// ATTENTION: This was auto-generated to implement the App Indexing API.
//		// See https://g.co/AppIndexing/AndroidStudio for more information.
//		Action viewAction = Action.newAction(
//				Action.TYPE_VIEW, // TODO: choose an action type.
//				"AndroidLauncher Page", // TODO: Define a title for the content shown.
//				// TODO: If you have web page content that matches this app activity's content,
//				// make sure this auto-generated web page URL is correct.
//				// Otherwise, set the URL to null.
//				Uri.parse("http://host/path"),
//				// TODO: Make sure this auto-generated app deep link URI is correct.
//				Uri.parse("android-app://com.main/http/host/path")
//		);
//		AppIndex.AppIndexApi.end(client, viewAction);
//		client.disconnect();
//	}
}
