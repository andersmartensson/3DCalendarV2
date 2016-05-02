package controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import data.Statics;

/**
 * Created by Anders on 2016-03-08.
 */
public class GoogleCalendarDownload {

    private static final String APPLICATION_NAME =
            "Google Calendar API Java Quickstart";

    /** Directory to store user credentials for this application. */
//    private static final java.io.File DATA_STORE_DIR = new java.io.File(
//            System.getProperty("user.home"), ".credentials/calendar-java-quickstart.json");
    private static final java.io.File DATA_STORE_DIR = new java.io.File(
            Gdx.files.getLocalStoragePath(), ".credentials/calendar-java-quickstart.json");
    /** Global instance of the {@link FileDataStoreFactory}. */
    private static FileDataStoreFactory DATA_STORE_FACTORY;

    /** Global instance of the JSON factory. */
    private static final JsonFactory JSON_FACTORY =
            JacksonFactory.getDefaultInstance();

    /** Global instance of the HTTP transport. */
    private static HttpTransport HTTP_TRANSPORT;

    /** Global instance of the scopes required by this quickstart.
     *
     * If modifying these scopes, delete your previously saved credentials
     * at ~/.credentials/calendar-java-quickstart.json
     */
    private static final List<String> SCOPES =
            Arrays.asList(CalendarScopes.CALENDAR_READONLY);
    public static DateTime fromDate;
    private static DateTime toDate;

    static {
        try {
            //if(Statics.is)
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            //HTTP_TRANSPORT = AndroidHttp.newCompatibleTransport();
            //final String CREDENTIALS_DIRECTORY = ".oauth-credentials";
            //File dataDirectory = new File(getContext().getFilesDir(), CREDENTIALS_DIRECTORY);
            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Creates an authorized Credential object.
     * @return an authorized Credential object.
     * @throws IOException
     */
    public static Credential authorize() throws IOException {
        // Load client secrets.
        String key = Statics.G_KEY;
        InputStream is = new ByteArrayInputStream( key.getBytes() );
        InputStreamReader isr2 = new InputStreamReader(is);
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,isr2);

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(
                        HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                        .setDataStoreFactory(DATA_STORE_FACTORY)
                        .setAccessType("offline")
                        .build();
        Credential credential = new AuthorizationCodeInstalledApp(
                flow, new LocalServerReceiver()).authorize("user");
        return credential;
    }

    /**
     * Build and return an authorized Calendar client service.
     * @return an authorized Calendar client service
     * @throws IOException
     */
    public static com.google.api.services.calendar.Calendar

    getCalendarService() throws IOException {
        Credential credential = authorize();
        return new com.google.api.services.calendar.Calendar.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public static Array<String> getCalenderNames() throws IOException {
        com.google.api.services.calendar.Calendar service =
                getCalendarService();
        long t = System.currentTimeMillis();
        Calendar.CalendarList calList = service.calendarList();
        CalendarList cList = calList.list().execute();
        Array<String> calendarsNames = new Array<String>(cList.getItems().size());
        for (CalendarListEntry cle: cList.getItems()){
            calendarsNames.add(cle.getId());
        }
        System.out.println("Fetching calender names took: " + (System.currentTimeMillis() - t));
        return calendarsNames;
    }

    public static Array<Event> execute(Array<String> cNames, long from, long to) throws IOException {
        // Build a new authorized API client service.
        // Note: Do not confuse this class with the
        //   com.google.api.services.calendar.model.Calendar class.
        long t = System.currentTimeMillis();

        com.google.api.services.calendar.Calendar service = null;
        if(!Statics.isAndroid){
            service = getCalendarService();

        }
        else {
            service = getAndroidCalendarService();
        }
        System.out.println("Authorization took: " + (System.currentTimeMillis() - t));

        fromDate = new DateTime( from);
        toDate = new DateTime(to);

        Array<Event> eventArray = new Array<Event>();
        //eventArray.
        long tt = 0;
        for(int i=0;i<cNames.size;i++){
            t = System.currentTimeMillis();
            Events events = service.events().list(cNames.get(i))
                    .setTimeMin(fromDate)
                    .setTimeMax(toDate)
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .execute();
            for(Event e: events.getItems()){
                eventArray.add(e);
            }
            t = System.currentTimeMillis() - t;
            System.out.println(cNames.get(i) + " took: " + t);
            tt+= t;
        }
        System.out.println("total for all calenders: " + tt);
        return eventArray;
    }

    private static Calendar getAndroidCalendarService() {
        //Get Calendar service
        Calendar c = null;
        try {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            c = new com.google.api.services.calendar.Calendar.Builder(
                    transport, jsonFactory, Statics.GoogleCredential)
                    .setApplicationName("Google Calendar API Android Quickstart")
                    .build();
        }catch (Exception e){
            System.out.println("Failed to create service from Google");
            e.printStackTrace();
            System.exit(0);
        }
        return c;
    }
}
