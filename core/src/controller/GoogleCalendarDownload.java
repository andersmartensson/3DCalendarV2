package controller;

import com.google.api.client.auth.oauth2.Credential;
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
import com.google.api.services.calendar.CalendarScopes;
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
    private static final java.io.File DATA_STORE_DIR = new java.io.File(
            System.getProperty("user.home"), ".credentials/calendar-java-quickstart.json");

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
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
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
        InputStream in = null;
//        System.out.println(GoogleCalendarDownload.class.getResource("/").getPath());
//
//        try{
//            in = GoogleCalendarDownload.class.getResourceAsStream("/client_secret.json");
//            //int s = in.read();
//        }
//        catch(Exception e){
//            System.out.println("FAILED!!!");
//        }
//        InputStreamReader isr = new InputStreamReader(in);

        //System.out.println("->" + in. + "<-");
        String key = Statics.G_KEY;
        InputStream is = new ByteArrayInputStream( key.getBytes() );
        InputStreamReader isr2 = new InputStreamReader(is);
        //GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, isr);
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,isr2);
        //clientSecrets = GoogleClientSecrets.

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(
                        HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                        .setDataStoreFactory(DATA_STORE_FACTORY)
                        .setAccessType("offline")
                        .build();
        Credential credential = new AuthorizationCodeInstalledApp(
                flow, new LocalServerReceiver()).authorize("user");
        System.out.println(
                "Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
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

    public static void main(String[] args) throws IOException {
        //downloadGoogle();
    }

    public static List<Event> execute(long from, long to) throws IOException {
        // Build a new authorized API client service.
        // Note: Do not confuse this class with the
        //   com.google.api.services.calendar.model.Calendar class.
        com.google.api.services.calendar.Calendar service =
                getCalendarService();

        // List the next 10 events from the primary calendar.
        //DateTime now = new DateTime( (System.currentTimeMillis() -10000000000L));
        fromDate = new DateTime( from);
        toDate = new DateTime(to);
        //Date date = new Date(2015,1,1,1,1);// =
        //TimeZone zone = TimeZone.getTimeZone("America/Los_Angeles");
        //DateTime febmars = new DateTime(date);
        //now  -= 100000;
        Events events = service.events().list("primary")
                //.setMaxResults(40)
                .setTimeMin(fromDate)
                .setTimeMax(toDate)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();
        List<Event> items = events.getItems();
        System.out.println("Fetched " + items.size());
//        if (items.size() == 0) {
//            System.out.println("No upcoming events found.");
//        } else {
//            System.out.println("Upcoming events");
//            for (Event event : items) {
//                DateTime start = event.getStart().getDateTime();
//                if (start == null) {
//                    start = event.getStart().getDate();
//                }
//                System.out.printf("%s (%s)\n", event.getSummary(), start);
//            }
//        }
        return items;
    }
}
