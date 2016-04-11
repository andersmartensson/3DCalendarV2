package model;

import com.badlogic.gdx.utils.Array;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;

import java.util.Calendar;
import java.util.Date;

import data.Statics;
import model.GFX.DatePillar;

/**
 * Created by Anders on 2016-04-07.
 */
public class Date3d implements Cloneable{

    public int year;
    public int month;
    public int day;
    public int startHour;
    public int startMin;
    public int stopHour;
    public int stopMin;
    public Day dayOfWeek;
    private int dayOfWeekNum;
    public long date;

    public Date3d(long date) {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        this.date = date;
        day = c.get(Calendar.DAY_OF_MONTH);
        month = c.get(Calendar.MONTH);
        year = c.get(Calendar.YEAR);
    }

    public String getDayString() {
        return getDateString(day);
    }

    public String getDateString(int num){
        if(num / 10 == 0){
            return "0" + num;
        }
        return "" + num;
    }

    public enum Day{
        Sunday, Monday, Tuesday, Wednesday, Thursday, Friday, Saturday,  None
    }

    public enum Month{
        January, February, Mars, April, May, June, July, August, September, October, November, December
    }

    public Date3d(long date, int stopMin, int year, int month, int day, int startHour, int startMin, int stopHour) {
        this.stopMin = stopMin;
        this.year = year;
        this.month = month;
        this.day = day;
        this.startHour = startHour;
        this.startMin = startMin;
        this.stopHour = stopHour;
        this.date = date;
    }

    public Date3d(Event e){
        if(e.getStart().getDateTime() != null){
            date = e.getStart().getDateTime().getValue();
        }
        else {
            System.out.println("WARNING:===== "+ e.getSummary() + " start time is null");
            date = 0;
        }

        DateTime dt = e.getStart().getDateTime();
        Calendar c = Calendar.getInstance();
        Date d = new Date(e.getStart().getDateTime().getValue());
        c.setTime(d);
        if(c.get(Calendar.DAY_OF_WEEK) <= 7 ){
            dayOfWeek = Day.values()[c.get(Calendar.DAY_OF_WEEK)-1];
        }
        else{
            dayOfWeek = Day.None;
        }

        parseStart(e.getStart().getDateTime().toString());
        parseEnd(e.getEnd().getDateTime().toString());
    }

    private void parseDate(String s, boolean start){
        String[] over = s.split("T");
        String[] date  = over[0].split("-");
        year = Integer.parseInt(date[0]);
        month = Integer.parseInt(date[1]);
        day = Integer.parseInt(date[2]);
        String[] time = over[1].split(":");
//        for(int i=0;i<time.length;i++){
//            System.out.println("time Row " + i + ": " + time[i]);
//        }
        if(start){
            startHour = Integer.parseInt(time[0]);
            startMin = Integer.parseInt(time[1]);
        }
        else {
            stopHour = Integer.parseInt(time[0]);
            stopMin  = Integer.parseInt(time[1]);
        }
    }

    private void parseEnd(String s) {
        parseDate(s,false);
    }

    private void parseStart(String s) {
        parseDate(s, true);
    }

    @Override
    public Date3d clone(){
        return new Date3d(date,stopMin, year, month, day, startHour, startMin, stopHour);
    }

    /**
     * Matches a day with a day from the array and returns its x-value
     * @param pillars
     * @return
     */
    public float matchXValue(Array<DatePillar> pillars) {
        for(DatePillar p: pillars){
            if(p.d3d.year == year
                && p.d3d.month == month
                && p.d3d.day == day){
                return p.position.x;
            }
        }
        return Statics.ACTIVITY_DUMP_X_POSTION;
    }

    public String toString(){
        return "Date: "
                + year + " : " + month
                + " : " + day
                + " is a " + dayOfWeek
                + " | Start: " + startHour + ":" + startMin
                + " | End: " + stopHour + ":" + stopMin;
    }
}
