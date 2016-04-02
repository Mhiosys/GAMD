package app.gamd.common;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Sigcomt on 31/03/2016.
 */
public class DateManager {

    private static DateManager mDateManager;

    private Calendar mCalendar;

    private DateManager() {
        mCalendar = Calendar.getInstance();
        mCalendar.set(Calendar.HOUR_OF_DAY, 0);
        mCalendar.set(Calendar.MINUTE, 0);
        mCalendar.set(Calendar.SECOND, 0);
        mCalendar.set(Calendar.MILLISECOND, 0);

        // if (mCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
        // mCalendar.add(Calendar.DAY_OF_MONTH, 2);
        // } else if (mCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
        // mCalendar.add(Calendar.DAY_OF_MONTH, 1);
        // }
    }

    public static DateManager getSharedInstance() {
        if (mDateManager == null) {
            mDateManager = new DateManager();
        }

        return mDateManager;
    }

    public void addDay() {
        mCalendar.add(Calendar.DAY_OF_MONTH, 1);
        // if (mCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
        // mCalendar.add(Calendar.DAY_OF_MONTH, 2);
        // } else if (mCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
        // mCalendar.add(Calendar.DAY_OF_MONTH, 1);
        // }
    }

    public void sustractDay() {
        mCalendar.add(Calendar.DAY_OF_MONTH, -1);
        // if (mCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
        // mCalendar.add(Calendar.DAY_OF_MONTH, -2);
        // } else if (mCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)
        // {
        // mCalendar.add(Calendar.DAY_OF_MONTH, -1);
        // }
    }

    public long getTimeInMillis() {
        return mCalendar.getTimeInMillis();
    }

    public int get(int field) {
        return mCalendar.get(field);
    }

    public Date getTime() {
        return mCalendar.getTime();
    }

    public int getNotValidDatesBetweenDays(long initialDate) {
        return 0;
        // Calendar calendarCurrent = (Calendar) mCalendar.clone();
        //
        // Calendar calendarInitial = Calendar.getInstance();
        // calendarInitial.setTimeInMillis(initialDate);
        //
        // int notValidDays = 0;
        //
        // while (calendarCurrent.after(calendarInitial)) {
        // calendarCurrent.add(Calendar.DAY_OF_MONTH, -1);
        // if (calendarCurrent.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
        // notValidDays++;
        // } else if (calendarCurrent.get(Calendar.DAY_OF_WEEK) ==
        // Calendar.SATURDAY) {
        // notValidDays++;
        // }
        // }
        //
        // return notValidDays;
    }
}
