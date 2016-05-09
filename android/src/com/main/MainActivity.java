package com.main;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends Activity
{
        GoogleAccountCredential mCredential;
        private TextView mOutputText;
        private Button mCallApiButton;
        ProgressDialog mProgress;

        static final int REQUEST_ACCOUNT_PICKER = 1000;
        static final int REQUEST_AUTHORIZATION = 1001;
        static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
        static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

        private static final String BUTTON_TEXT = "Call Google Calendar API";
        private static final String PREF_ACCOUNT_NAME = "accountName";
        private static final String[] SCOPES = { CalendarScopes.CALENDAR_READONLY };

        /**
         * Create the main activity.
         * @param savedInstanceState previously saved instance data.
         */
        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                LinearLayout activityLayout = new LinearLayout(this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                activityLayout.setLayoutParams(lp);
                activityLayout.setOrientation(LinearLayout.VERTICAL);
                activityLayout.setPadding(16, 16, 16, 16);

                ViewGroup.LayoutParams tlp = new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);

                mCallApiButton = new Button(this);
                mCallApiButton.setText(BUTTON_TEXT);
                mCallApiButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mCallApiButton.setEnabled(false);
                        mOutputText.setText("");
                        getResultsFromApi();
                        mCallApiButton.setEnabled(true);
                    }
                });
                activityLayout.addView(mCallApiButton);

                mOutputText = new TextView(this);
                mOutputText.setLayoutParams(tlp);
                mOutputText.setPadding(16, 16, 16, 16);
                mOutputText.setVerticalScrollBarEnabled(true);
                mOutputText.setMovementMethod(new ScrollingMovementMethod());
                mOutputText.setText(
                        "Click the \'" + BUTTON_TEXT + "\' button to test the API.");
                activityLayout.addView(mOutputText);

                mProgress = new ProgressDialog(this);
                mProgress.setMessage("Calling Google Calendar API ...");

                setContentView(activityLayout);
                /*
                STEP 1:
                 */
                // Initialize credentials and service object.
                mCredential = GoogleAccountCredential.usingOAuth2(
                        getApplicationContext(), Arrays.asList(SCOPES))
                        .setBackOff(new ExponentialBackOff());
        }

        private void getResultsFromApi() {
                mOutputText.append("\nGetting Result from API");
            if(mCredential.getSelectedAccountName() == null){
                    mOutputText.append("\n Selected account name was null, go to choose acount");
                chooseAccount();
            }
            else {
                //Log.i("requestTask", "Launching requestTask thread");
                    mOutputText.append("\nCreating new fetch task");
                new MakeRequestTask(mCredential).execute();
            }
        }

        private void chooseAccount() {
            startActivityForResult(
                            mCredential.newChooseAccountIntent(),
                            REQUEST_ACCOUNT_PICKER);
        }

        @Override
        protected void onActivityResult(
                int requestCode, int resultCode, Intent data) {
                super.onActivityResult(requestCode, resultCode, data);
                mOutputText.append("\nChecking status!!!!!");
                switch(requestCode) {
                        case REQUEST_GOOGLE_PLAY_SERVICES:
                                if (resultCode != RESULT_OK) {
                                        mOutputText.append(
                                                "\nThis app requires Google Play Services. Please install " +
                                                        "Google Play Services on your device and relaunch this app.");
                                } else {
                                        getResultsFromApi();
                                }
                                break;
                        case REQUEST_ACCOUNT_PICKER:
                                if (resultCode == RESULT_OK && data != null &&
                                        data.getExtras() != null) {
                                        mOutputText.append("\nRequesting Account picker!!!!!!!!!!");
                                        String accountName =
                                                data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                                        if (accountName != null) {
                                                mOutputText.append("\nAccount name NOT null!!!!!!!!! Name is: " + accountName);

                                                SharedPreferences settings =
                                                        getPreferences(Context.MODE_PRIVATE);
                                                SharedPreferences.Editor editor = settings.edit();
                                                editor.putString(PREF_ACCOUNT_NAME, accountName);
                                                editor.apply();
                                                mOutputText.append("\nName again is: " + accountName);

                                                //mCredential.

                                                mCredential.setSelectedAccountName(accountName);
                                                mOutputText.append("\naccount name is: " + mCredential.getSelectedAccountName());
                                                mOutputText.append("\nGetting results!!!!!!!!!!");

                                                getResultsFromApi();
                                        }
                                        else {
                                                mOutputText.append("\nAccount name null!!!!!!!!!!");
                                        }
                                }
                                break;
                        case REQUEST_AUTHORIZATION:
                                if (resultCode == RESULT_OK) {
                                        mOutputText.append("\nResult code OK!!!!!!!");
                                        //mOutputText.setText("");
                                        getResultsFromApi();
                                }
                                break;
                }
        }


        /**
         * An asynchronous task that handles the Google Calendar API call.
         * Placing the API calls in their own task ensures the UI stays responsive.
         */
        private class MakeRequestTask extends AsyncTask<Void, Void, List<String>> {
                private com.google.api.services.calendar.Calendar mService = null;
                private Exception mLastError = null;

                public MakeRequestTask(GoogleAccountCredential credential) {
                        HttpTransport transport = AndroidHttp.newCompatibleTransport();
                        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
                        mService = new com.google.api.services.calendar.Calendar.Builder(
                                transport, jsonFactory, credential)
                                .setApplicationName("Google Calendar API Android Quickstart")
                                .build();
                }

                /**
                 * Background task to call Google Calendar API.
                 * @param params no parameters needed for this task.
                 */
                @Override
                protected List<String> doInBackground(Void... params) {
                        try {
                                return getDataFromApi();
                        } catch (Exception e) {
                                mLastError = e;
                                cancel(true);
                                return null;
                        }
                }

                /**
                 * Fetch a list of the next 10 events from the primary calendar.
                 * @return List of Strings describing returned events.
                 * @throws IOException
                 */
                private List<String> getDataFromApi() throws IOException {
                        // List the next 10 events from the primary calendar.
                        DateTime now = new DateTime(System.currentTimeMillis());
                        List<String> eventStrings = new ArrayList<String>();
                        Events events = mService.events().list("primary")
                                .setMaxResults(10)
                                .setTimeMin(now)
                                .setOrderBy("startTime")
                                .setSingleEvents(true)
                                .execute();
                        List<Event> items = events.getItems();

                        for (Event event : items) {
                                DateTime start = event.getStart().getDateTime();
                                if (start == null) {
                                        // All-day events don't have start times, so just use
                                        // the start date.
                                        start = event.getStart().getDate();
                                }
                                eventStrings.add(
                                        String.format("%s (%s)", event.getSummary(), start));
                        }
                        return eventStrings;
                }


                @Override
                protected void onPreExecute() {
                        mOutputText.setText("");
                        mProgress.show();
                }

                @Override
                protected void onPostExecute(List<String> output) {
                        mProgress.hide();
                        if (output == null || output.size() == 0) {
                                mOutputText.setText("No results returned.");
                        } else {
                                output.add(0, "Data retrieved using the Google Calendar API:");
                                mOutputText.setText(TextUtils.join("\n", output));
                        }
                }

                @Override
                protected void onCancelled() {
                        mProgress.hide();
                        if (mLastError != null) {
                                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                                        startActivityForResult(
                                                ((UserRecoverableAuthIOException) mLastError).getIntent(),
                                                MainActivity.REQUEST_AUTHORIZATION);
                                } else {
                                        mOutputText.setText("The following error occurred:\n"
                                                + mLastError.getMessage());
                                }
                        } else {
                                mOutputText.setText("Request cancelled.");
                        }
                }
        }
}
