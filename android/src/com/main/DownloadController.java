package com.main;

import com.badlogic.gdx.utils.Array;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import data.Statics;

/**
 * Created by Anders on 2016-05-02.
 */
public class DownloadController extends Thread{
    //controller
    Event event;
    Array<Event> events;
    GoogleAccountCredential mCredential;
    //Context context;
    private static final String[] SCOPES = { CalendarScopes.CALENDAR_READONLY };
    private com.google.api.services.calendar.Calendar mService = null;

    public DownloadController(GoogleAccountCredential cred) throws IOException {
        Thread uT = new UpdateThread();
        uT.start();
        //context = applicationContext;
        mCredential = cred;
    }

    private class UpdateThread extends Thread{
        view.MainView main;
        long from;
        long to;
        public UpdateThread() throws IOException {


        }

        public void run(){
            //Init


            //Get Calendar service
            try {
                HttpTransport transport = AndroidHttp.newCompatibleTransport();
                JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
                mService = new com.google.api.services.calendar.Calendar.Builder(
                        transport, jsonFactory, mCredential)
                        .setApplicationName("Google Calendar API Android Quickstart")
                        .build();
            }catch (Exception e){
                System.out.println("Failed to create service from Google");
                e.printStackTrace();
                System.exit(0);
            }

            // List the next 10 events from the primary calendar.
            DateTime now = new DateTime(System.currentTimeMillis());
            List<String> eventStrings = new ArrayList<String>();

            Events events = null;
            try {
                events = mService.events().list("primary")
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
            } catch (IOException e) {
                System.out.println("Failed download data from Google");
                e.printStackTrace();
                System.exit(0);
            }


            while(true){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(Statics.updateCalendar){
                    System.out.println("Update !!!!");

                    Statics.updateCalendar = false;
                }
                else {
                    System.out.println("Don't update!!!");
                }
                System.out.println("    _____________     ");
            }
        }
    }

//    /**
//     * Attempt to call the API, after verifying that all the preconditions are
//     * satisfied. The preconditions are: Google Play Services installed, an
//     * account was selected and the device currently has online access. If any
//     * of the preconditions are not satisfied, the app will prompt the user as
//     * appropriate.
//     */
//    private void getResultsFromApi() {
//        if (! AndroidLauncher.isGooglePlayServicesAvailable()) {
//            acquireGooglePlayServices();
//        } else if (mCredential.getSelectedAccountName() == null) {
//            chooseAccount();
//        } else if (! isDeviceOnline()) {
//            mOutputText.setText("No network connection available.");
//        } else {
//            new MakeRequestTask(mCredential).execute();
//        }
//    }




}
