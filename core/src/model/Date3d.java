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
        Calendar c = Statics.calendar;
        c.setTime(new Date(date));
        day = c.get(Calendar.DAY_OF_MONTH);
        month = c.get(Calendar.MONTH) +1;
        year = c.get(Calendar.YEAR);
        //System.out.println("Constructor 1: \nDate: " + date + "\nDay: " + day + " Month: " + month + " Year: " + year);
        this.startHour = 0;
        this.stopMin = 0;
        this.startMin = 0;
        this.stopHour = 0;
        this.date = date;
    }

    public Date3d(long date, int stopMin, int startHour, int startMin, int stopHour){
        this(date);
        //System.out.println("Constructor 2: \nDate: " + date + "\nDay: " + day + " Month: " + month + " Year: " + year);
        this.startHour = startHour;
        this.stopMin = stopMin;
        this.startMin = startMin;
        this.stopHour = stopHour;
    }

    public Date3d(long date, int stopMin, int year, int month, int day, int startHour, int startMin, int stopHour) {
//        System.out.println("Constructor 3: \nDate: " + date + "\nDay: " + day + " Month: " + month + " Year: " + year);
        this.day = day;
        this.month = month;
        this.year = year;
        this.startHour = startHour;
        this.stopMin = stopMin;
        this.startMin = startMin;
        this.stopHour = stopHour;
        this.date = date;
    }

    public Date3d clone(boolean recalculateDate){
        if(recalculateDate){
            //Recalculate day, year and month
            return new Date3d(date, stopMin, startHour, startMin, stopHour);
        }
        return new Date3d(date, stopMin, year, month, day, startHour, startMin, stopHour);

    }

    public String getDayString() {
        return getDateString(day, true);
    }

    public String getDateString(int num, boolean leadingZero){
        /*
        Places a zero in front of the num
         */
        if(leadingZero && num / 10 == 0) {
            return "0" + num;
        }
        return "" + num;
    }

    public String getStartTime() {
        return startHour + ":" + startMin;
    }

    public String getStartTime(boolean leadingZero) {
        //return startHour + ":" +
        return getDateString(startHour, leadingZero) + ":" + getDateString(startMin, leadingZero);
    }

    public String getStopTime() {
        return stopHour + ":" + stopMin;
    }

    public String getStopTime(boolean leadingZero) {
        return getDateString(stopHour, leadingZero) + ":" + getDateString(stopMin, leadingZero);
    }

    public String getDuration(){
        int h=0, m=0;
        m = stopHour * 60;
        m += startMin;
        m -= startHour * 60;
        m -= startMin;
        h = m / 60;
        System.out.println("h: " + h + " 180 rest 60 = " + (180 / 60) );
        m -= h * 60;
        System.out.println("m: " + m);
        return "" + h + ":" + formatMin(m);

    }

    private String formatMin(int m) {
        if(m /10 == 0){
            return "0" + m;
        }
        return "" + m;
    }

    /**
     *
     * @return String with the name of the day and month
     */
    public String getDateName(){
        StringBuilder sb = new StringBuilder();
        Calendar c = Statics.calendar;
        c.setTime(new Date(date));
        //Get day
        sb.append(Day.values()[c.get(Calendar.DAY_OF_WEEK)].toString());
        sb.append(" on the ");
        sb.append(day);
        sb.append(" of ");
        sb.append(Month.values()[month].toString());
        sb.append(" " + year);
        return sb.toString();
    }

    public boolean parseStartTime(String text) {

        return parseTime(text, true);
    }

    public boolean parseStopTime(String text){
        return parseTime(text, false);
    }

    private boolean parseTime(String s, boolean start) {
        String[] as = s.split(":");
//        System.out.println("Parse Time:(" + as.length);
//        for(String st:as){
//            System.out.println(st);
//        }
        if(!checkValidityOfTime(as)){
            return false;
        }
        if(start){
            if(as.length == 1){
                startHour = Integer.parseInt(as[0]);
                startMin = 0;
            }
            else {
                startHour = Integer.parseInt(as[0]);
                startMin = Integer.parseInt(as[1]);
            }
        }
        else {
            if(as.length == 1){
                stopHour = Integer.parseInt(as[0]);
                stopMin = 0;
            }
            else {
                stopHour = Integer.parseInt(as[0]);
                stopMin = Integer.parseInt(as[1]);
            }

        }
        return true;
    }

    private boolean checkValidityOfTime(String[] as) {
        int sTH = Integer.parseInt(as[0]);
        if(sTH < 0 && sTH >= 24 ){
            return false;
        }
        if(as.length > 1){
            int sTM = Integer.parseInt(as[1]);
               if(sTM <0 && sTM >= 60) {
                   return false;
               }
        }
        return true;
    }

    public String getMonth(boolean b) {
        return getDateString(month, b);
    }

    public Object getDay(boolean b) {
        return getDateString(day, b);
    }


    public enum Day{
        Zero, Sunday, Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, None
    }

    public enum Month{
        ZERO, January, February, Mars, April, May, June, July, August, September, October, November, December
    }



    public Date3d(Event e){
        if(e.getStart().getDateTime() != null){
            date = e.getStart().getDateTime().getValue();
        }
        else {
            System.out.println("WARNING:===== "+ e.getSummary() + " start time is null");
            date = 0;
        }
        //System.out.println("EVENT NAME: " + e.getSummary());
        DateTime dt = e.getStart().getDateTime();
        Calendar c = Statics.calendar;
        Date d = new Date(e.getStart().getDateTime().getValue());
        c.setTime(d);
        if(c.get(Calendar.DAY_OF_WEEK) <= 7 ){
            dayOfWeek = Day.values()[c.get(Calendar.DAY_OF_WEEK)];
        }
        else{
            dayOfWeek = Day.Zero;
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
        //System.out.println("PARSE: " + s + "\n Becomes: Y: " + year + " M: " + month + " D: " + day);
    }

    private void parseEnd(String s) {
        parseDate(s,false);
    }

    private void parseStart(String s) {
        parseDate(s, true);
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
                //System.out.println("Matched " + p.d3d.day + " with " + day + " Sending back pos.x:  " + p.position.x);
                return p.position.x;
            }
        }
        //System.out.println("No match for " + day );
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
