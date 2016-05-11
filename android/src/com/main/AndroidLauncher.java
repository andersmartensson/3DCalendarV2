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
import com.google.android.gms.auth.GoogleAuthException;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.services.calendar.CalendarScopes;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import data.Statics;


public class AndroidLauncher extends AndroidApplication {
	private static Context context;

    private static final List<String> SCOPES =
            Arrays.asList(CalendarScopes.CALENDAR);
//    private static final List<String> SCOPES =
//            Arrays.asList(CalendarScopes.CALENDAR_READONLY);
    //private static final String[] SCOPES = { CalendarScopes.CALENDAR_READONLY };
	private static final String PREF_ACCOUNT_NAME = "accountName";
	static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int MY_PERMISSIONS_REQUEST_WRITE_CALENDAR = 2000;
    @Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();

		if (getContext().checkCallingOrSelfPermission("android.permission.INTERNET") == PackageManager.PERMISSION_GRANTED) {
			System.out.println("Permission granted");
		} else {
			System.out.println("Permission denied");
			Gdx.app.exit();
		}

        // Assume thisActivity is the current activity
//        int permissionCheck = ContextCompat.checkSelfPermission(this,
//                Manifest.permission.WRITE_CALENDAR);
//        if(getContext().checkCallingOrSelfPermission("android.permission.WRITE_CALENDAR") == PackageManager.PERMISSION_GRANTED){
//            System.out.println("Calendar write was enabled");
//        }
//        else {
//            System.out.println("Failed write permissions");
//            Gdx.app.exit();
//        }
//
//        // Here, thisActivity is the current activity
//        if (ContextCompat.checkSelfPermission(this,
//                Manifest.permission.WRITE_CALENDAR)
//                != PackageManager.PERMISSION_GRANTED) {
//            System.out.println("Write permission was not granted ==================");
//
//            // Should we show an explanation?
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
//                    Manifest.permission.WRITE_CALENDAR)) {
//                // Show an expanation to the user *asynchronously* -- don't block
//                // this thread waiting for the user's response! After the user
//                // sees the explanation, try again to request the permission.
//
//            } else {
//
//                // No explanation needed, we can request the permission.
//                System.out.println("Request permissions");
//                ActivityCompat.requestPermissions(this,
//                        new String[]{Manifest.permission.WRITE_CALENDAR},
//                        MY_PERMISSIONS_REQUEST_WRITE_CALENDAR);
//
//                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
//                // app-defined int constant. The callback method gets the
//                // result of the request.
//            }
//        }
//        else {
//            System.out.println("Permission WRITE, not passed");
//        }
        // Initialize credentials and service object.
            Statics.GoogleCredential = GoogleAccountCredential. usingOAuth2(
                    getContext()
                    ,SCOPES
            );
        System.out.println("SCOPE: " + Statics.GoogleCredential.getScope());
//            Statics.GoogleCredential = GoogleAccountCredential.usingOAuth2(
//                    getApplicationContext()
//                    , Arrays.asList(SCOPES))
//                    .setBackOff(new ExponentialBackOff());


        //Launch account picker
        if(Statics.GoogleCredential.getSelectedAccountName() == null){
            chooseAccount();
        }
        try {
            System.out.println("Get Token: " + Statics.GoogleCredential.getToken());
        } catch (UserRecoverableAuthIOException e) {
            System.out.println("USerRecoverable Exception");
            startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (GoogleAuthException e) {
            System.out.println("Authentication Exception");
            startActivityForResult(getIntent(), REQUEST_AUTHORIZATION);
            e.printStackTrace();
        }
        initialize(new view.MainView(true), config);
	}

    private void chooseAccount() {
//        startActivityForResult(getIntent(), REQUEST_AUTHORIZATION);

        startActivityForResult(
                    Statics.GoogleCredential.newChooseAccountIntent(),
                    REQUEST_ACCOUNT_PICKER);
//        catch (UserRecoverableAuthIOException e){
//            startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);
//        }

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

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_CALENDAR: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Gdx.app.exit();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

}
