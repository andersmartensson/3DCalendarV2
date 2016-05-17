package controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.services.calendar.model.Event;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import data.Statics;
import view.MainView;

public class CalendarController {
    public Array<Event> events;
    public long lastUpdate;
    MainView main;
    Array<String> calendarNames;
    Array<String> primaryCalendar;

    public CalendarController(MainView m){
        main = m;
        primaryCalendar = new Array<String>();
        primaryCalendar.add("primary");
    }
    /**
     *      Downloads activities default weeks back, beginning
     *      with the first day of the current week.
     */
    public void initialDownload(){
        try {
            calendarNames = GoogleCalendarDownload.getCalenderNames();
        } catch (IOException e) {
            e.printStackTrace();
        }
        lastUpdate = getAdjustedDay(System.currentTimeMillis());
        long to = lastUpdate + milliSecondsInADay() * (Statics.NUM_OF_WEEKS_BEFORE_AND_AFTER *2 +1) * 7;
        update(lastUpdate, to);
        //Set from and to in main
        main.from = lastUpdate;
        main.to = to;
        while(!main.isDownloadDone()){
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public long milliSecondsInADay() {
        return 24*milliSecondsInAnHour();
    }
    /*
    Converts current date to the beginning of the week, of set weeks back
     */
    public long getAdjustedDay(long date) {
        //Get current week
        //Convert to monday:
        Calendar c = Statics.calendar;
        c.setTime(new Date(date));
        int day = c.get(Calendar.DAY_OF_WEEK ) -1;
        System.out.println("Current day according to calendar is " + c.get(Calendar.DAY_OF_MONTH));
        System.out.println("Today is " + day + " st/rd day of the week");
        //Counts weeks as starting sunday, so -1
        date -= (day -1) * milliSecondsInADay();
        c.setTime(new Date(date));
        System.out.println("Monday is " + c.get(Calendar.DAY_OF_MONTH));
        //Step four weeks back to get previous month
        date -= milliSecondsInADay() * Statics.NUM_OF_WEEKS_BEFORE_AND_AFTER * 7;
        c.setTime(new Date(date));
        System.out.println("currentTime is " + c.get(Calendar.DAY_OF_MONTH));
        return date;
    }



    public void InsertEvent(Event e){
        InsertThread it = new InsertThread(e);
        it.start();
    }

    private class InsertThread extends Thread{
        private Event event;
        public InsertThread(Event e){
            event = e;
        }

        public void run(){
            System.out.println("Starting insert....");
            try {
                GoogleCalendarDownload.uploadEvent(event);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Done inserting.");
            //When done, update the calendar to reflect the changes
            main.updateActivities = true;
            main.clearedAndMoved = false;
            main.setDownloadDone(false);
            download(main.from, main.to);
        }
    }

    public void update(long from, long to){
        main.setDownloadDone(false);
        Date fromDate = new Date(from), toDate = new Date(to);
        Calendar c = Statics.calendar;
        c.setTime(fromDate);
        System.out.println("updating from: " + c.get(Calendar.DAY_OF_MONTH) + "/" + c.get(Calendar.MONTH) + "/" + c.get(Calendar.YEAR));
        c.setTime(toDate);
        System.out.println("updating to: " + c.get(Calendar.DAY_OF_MONTH) + "/" + c.get(Calendar.MONTH) + "/" + c.get(Calendar.YEAR));
        DownloadThread downloadThread = new DownloadThread(main, from,to);
        downloadThread.start();
    }

    public void updateEvent(Event event) {
        Thread t = new UpdateEventThread(event);
        t.start();
        //Update calender when done.
        main.updateCalendar(main.from, main.to, false);
    }

    private class UpdateEventThread extends Thread{
        Event event;
        public UpdateEventThread(Event e){
            event = e;
        }

        public void run(){
            try {
                GoogleCalendarDownload.updateEvent(event);
            } catch (IOException e) {
                e.printStackTrace();
            }
            //When done, update the calendar to reflect the changes
            update(main.from, main.to);
        }
    }

    private class DownloadThread extends Thread{
        MainView main;
        long from;
        long to;
        public DownloadThread(MainView m, long f, long t) {
            main = m;
            from = f;
            to = t;
        }

        public void run(){
            download(from, to);
        }

    }

    private void download(long from, long to) {
        long time = System.currentTimeMillis();
        if(Statics.downloadPrimary){
            try {
                events = GoogleCalendarDownload.execute(primaryCalendar, from, to );
            } catch ( UserRecoverableAuthIOException er) {
                System.out.println("USER RECOVER EXCEPTION");
                er.printStackTrace();
                Gdx.app.exit();
                System.exit(0);
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
        else {
            try {
                events = GoogleCalendarDownload.execute(calendarNames, from, to );
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        main.setDownloadDone(true);
        System.out.println("Downloading took : " + (time - System.currentTimeMillis()));
    }

    public long milliSecondsInAnHour() {
        return 1000 * 60 *60;
    }


}
