package com.main;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.api.services.calendar.CalendarScopes;

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
        

		initialize(new MainView(true), config);

	}

}
