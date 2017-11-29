package christophershae.budgettracker;

import com.github.mikephil.charting.data.BarEntry;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
/**
 * Created by chrissmith on 11/16/17.
 */

public class Utils {

    private static FirebaseDatabase mDataBase;

    public static FirebaseDatabase getDatabase() {
        if (mDataBase == null) {
            mDataBase = FirebaseDatabase.getInstance();
            mDataBase.setPersistenceEnabled(true);
        }
        return mDataBase;
    }

    public String newDate;

    //Retrieving the correct weeklong budget object to store the new item in
    public static WeekLongBudget createNewWeek()
    {
        //DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, Locale.US);
        //Decrement the date to be the most recent sunday
        Date currentDate = new Date();
        String newWeekIndex = decrementDate(currentDate);
        WeekLongBudget newWeek = new WeekLongBudget(newWeekIndex);


        return newWeek;

    }




    static SimpleDateFormat sdf = new SimpleDateFormat("MMddyyyy");    //This is the format we want our date string to be in

    //This function decrements the date so it adds it to the correct weeklong budget
    public static String decrementDate(Date date)
    {

        //Get an instance of the calendar and get the current day of the week
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int day = calendar.get(Calendar.DAY_OF_WEEK);

        //Depending on what day it is, decrement the date to be the most recent sunday
        //If it is Sunday, then it won't change the date at all
        switch(day){
            case Calendar.MONDAY:
                calendar.setTime(date);
                calendar.add(Calendar.DATE, -1);
                date = calendar.getTime();
                break;

            case Calendar.TUESDAY:
                calendar.setTime(date);
                calendar.add(Calendar.DATE, -2);
                date = calendar.getTime();
                break;

            case Calendar.WEDNESDAY:
                calendar.setTime(date);
                calendar.add(Calendar.DATE, -3);
                date = calendar.getTime();
                break;

            case Calendar.THURSDAY:
                calendar.setTime(date);
                calendar.add(Calendar.DATE, -4);
                date = calendar.getTime();
                break;

            case Calendar.FRIDAY:
                calendar.setTime(date);
                calendar.add(Calendar.DATE, -5);
                date = calendar.getTime();
                break;

            case Calendar.SATURDAY:
                calendar.setTime(date);
                calendar.add(Calendar.DATE, -6);
                date = calendar.getTime();
                break;

            default:
                break;
        }
        return sdf.format(date);   //return the decremented date as a string
    }

    //This function attains the week previous to the current date (for bar graph)
    public static Date prevDate(Date date) {

        // Get an instance of the calendar and get the current day of the week
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int day = calendar.get(Calendar.DAY_OF_WEEK);

        //Depending on what day it is, decrement the date to be the previous sunday
        switch (day) {
            case Calendar.SUNDAY:
                calendar.setTime(date);
                calendar.add(Calendar.DATE, -7);
                date = calendar.getTime();
                break;

            case Calendar.MONDAY:
                calendar.setTime(date);
                calendar.add(Calendar.DATE, -8);
                date = calendar.getTime();
                break;

            case Calendar.TUESDAY:
                calendar.setTime(date);
                calendar.add(Calendar.DATE, -9);
                date = calendar.getTime();
                break;

            case Calendar.WEDNESDAY:
                calendar.setTime(date);
                calendar.add(Calendar.DATE, -10);
                date = calendar.getTime();
                break;

            case Calendar.THURSDAY:
                calendar.setTime(date);
                calendar.add(Calendar.DATE, -11);
                date = calendar.getTime();
                break;

            case Calendar.FRIDAY:
                calendar.setTime(date);
                calendar.add(Calendar.DATE, -12);
                date = calendar.getTime();
                break;

            case Calendar.SATURDAY:
                calendar.setTime(date);
                calendar.add(Calendar.DATE, -13);
                date = calendar.getTime();
                break;

            default:
                break;
        }
        return date;
    }

    // Originally made for the bar graph, due to prevDate's requirement to return a date, not a string
    public static String convertDate(Date date)
    {
        String prevWeeksDate = sdf.format(date);
        return prevWeeksDate;
    }

}
