package com.main;

import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;

import java.util.Arrays;
import java.util.List;

import data.Statics;


public class AndroidLauncher extends AndroidApplication {
	private static Context context;

	private static final String[] SCOPES = { CalendarScopes.CALENDAR_READONLY };
	private static final String PREF_ACCOUNT_NAME = "accountName";
	static final int REQUEST_ACCOUNT_PICKER = 1000;

    @Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();

		if (getContext().checkCallingOrSelfPermission("android.permission.INTERNET") == PackageManager.PERMISSION_GRANTED) {
			System.out.println("Permission granted");
			context = getContext();
		} else {
			System.out.println("Permission denied");
			Gdx.app.exit();
		}

        // Initialize credentials and service object.
        Statics.GoogleCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());

        //Launch account picker
        if(Statics.GoogleCredential.getSelectedAccountName() == null){
            chooseAccount();
        }
        initialize(new view.MainView(true), config);
	}

    private void chooseAccount() {
        startActivityForResult(
                Statics.GoogleCredential.newChooseAccountIntent(),
                REQUEST_ACCOUNT_PICKER);
    }

    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("Activity","Activity Done, checking result......");
        String accountName =
                data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
        SharedPreferences settings =
                getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(PREF_ACCOUNT_NAME, accountName);
        editor.apply();
        Statics.GoogleCredential.setSelectedAccountName(accountName);
    }

}
