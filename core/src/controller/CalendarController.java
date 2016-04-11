package controller;

import com.google.api.services.calendar.model.Event;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import view.MainView;

/**
 * Created by Anders on 2016-04-07.
 */
public class CalendarController {
    public List<Event> events;
    public long lastUpdate;
    MainView main;

    public CalendarController(MainView m){
        main = m;
    }
    public void update(){
        //Get current week
        long currentTime = System.currentTimeMillis();
        //Convert to monday:
        Calendar c = Calendar.getInstance();
        c.setTime(new Date(currentTime));
        int day = c.get(Calendar.DAY_OF_WEEK) -1;
        System.out.println("Day = " + day);
        currentTime -= day * milliSecondsInADay();
        c.setTime(new Date(currentTime));
        System.out.println("Current time is " + c.get(Calendar.DAY_OF_WEEK));
        lastUpdate = currentTime;
        //int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        try {
            events = GoogleCalendarDownload.execute( lastUpdate );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public long milliSecondsInADay() {
        System.out.println("Milliseconds in a day = " + (24*60*60*1000));
        return 24*60*60*1000;
    }
}
