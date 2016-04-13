package controller;

import com.google.api.services.calendar.model.Event;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import data.Statics;
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

    public void update(long from, long to){
        long time = System.currentTimeMillis();
        System.out.println();
        try {
            events = GoogleCalendarDownload.execute( from, to );
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Downloading took : " + (time - System.currentTimeMillis() ) );
    }
    /**
     *      Downloads activities 4 weeks back, beginning
     *      with the first day of the current week.
     */
    public void initialDownload(){

        lastUpdate = getAdjustedDay(System.currentTimeMillis());
        long to = lastUpdate + milliSecondsInADay() * (Statics.NUM_OF_WEEKS_BEFORE_AND_AFTER *2 +1) * 7;
        update(lastUpdate, to);
    }

    public long milliSecondsInADay() {
        return 24*60*60*1000;
    }

    public long getAdjustedDay(long date) {
        //Get current week
        //long currentTime =
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
}
