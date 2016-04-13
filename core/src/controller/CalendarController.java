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

    public void update(Long date){
        try {
            events = GoogleCalendarDownload.execute( date );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     *      Downloads activities 4 weeks back, beginning
     *      with the first day of the current week.
     */
    public void initialDownload(){
        //Get current week
        long currentTime = System.currentTimeMillis();


        //Convert to monday:
        Calendar c = Statics.calendar;
        c.setTime(new Date(currentTime));
        int day = c.get(Calendar.DAY_OF_WEEK ) -1;
        System.out.println("current day according to calendar is " + c.get(Calendar.DAY_OF_MONTH));

        System.out.println("Today is " + day + " st/rd day of the week");
        //Counts weeks as starting sunday, so -1
        currentTime -= (day -1) * milliSecondsInADay();
        c.setTime(new Date(currentTime));
        System.out.println("Monday is " + c.get(Calendar.DAY_OF_MONTH));
        //Step four weeks back to get previous month

        currentTime -= milliSecondsInADay() * Statics.NUM_OF_WEEKS_BEFORE_AND_AFTER * 7;
        c.setTime(new Date(currentTime));

        System.out.println("currentTime is " + c.get(Calendar.DAY_OF_MONTH));
        lastUpdate = currentTime;
        update(lastUpdate);
    }

    public long milliSecondsInADay() {
        return 24*60*60*1000;
    }
}
