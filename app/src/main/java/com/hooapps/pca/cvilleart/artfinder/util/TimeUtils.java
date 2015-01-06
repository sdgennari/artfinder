package com.hooapps.pca.cvilleart.artfinder.util;

import java.util.Calendar;

public class TimeUtils {


    public static long parseUnixFromDateTime(String dateTimeString) {
        if (dateTimeString == null) {
            return 0;
        }
        // DATE FORM: YYYY-MM-DDThh:mm:ss-05:00
        int year = Integer.parseInt(dateTimeString.substring(0, 4));
        int month = Integer.parseInt(dateTimeString.substring(5, 7));
        int day = Integer.parseInt(dateTimeString.substring(8, 10));
        int hour = Integer.parseInt(dateTimeString.substring(11, 13));
        int minute = Integer.parseInt(dateTimeString.substring(14, 16));
        int timeZoneMod = Integer.parseInt(dateTimeString.substring(19, 21));

        Calendar c = Calendar.getInstance();
        c.set(year, Calendar.JANUARY, day, hour-timeZoneMod, minute);
        c.set(Calendar.MONTH, month-1);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        return c.getTimeInMillis();
    }

    public static long parseUnixFromDate(String dateString) {
        if (dateString == null) {
            return 0;
        }

        // DATE FORM: YYYY-MM-DD
        int year = Integer.parseInt(dateString.substring(0, 4));
        int month = Integer.parseInt(dateString.substring(5, 7));
        int day = Integer.parseInt(dateString.substring(8, 10));

        Calendar c = Calendar.getInstance();
        c.set(year, month-1, day);

        return c.getTimeInMillis();
    }

    public static String createTimeString(Calendar c) {
        String result = "";

        int hours = c.get(Calendar.HOUR_OF_DAY);
        int minutes = c.get(Calendar.MINUTE);
        int formattedHours = hours;

        // Properly format the hours for AM/PM
        if (hours > 12) {
            formattedHours = hours % 12;
        } else if (hours == 0) {
            formattedHours = 12;
        }
        result = String.format("% 2d", formattedHours) + ":" + String.format("%02d", minutes);

        if (hours/12 == 0) {
            result += " AM";
        } else {
            result += " PM";
        }

        return result;
    }

    public static String createDateString(Calendar c) {
        String result = "";

        // Find the day of the week
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        switch (dayOfWeek) {
            case Calendar.MONDAY:
                result += "Monday";
                break;
            case Calendar.TUESDAY:
                result += "Tuesday";
                break;
            case Calendar.WEDNESDAY:
                result += "Wednesday";
                break;
            case Calendar.THURSDAY:
                result += "Thursday";
                break;
            case Calendar.FRIDAY:
                result += "Friday";
                break;
            case Calendar.SATURDAY:
                result += "Saturday";
                break;
            case Calendar.SUNDAY:
                result += "Sunday";
                break;
        }

        result += ", ";

        // Find the month
        int month = c.get(Calendar.MONTH);
        switch (month) {
            case Calendar.JANUARY:
                result += "January";
                break;
            case Calendar.FEBRUARY:
                result += "February";
                break;
            case Calendar.MARCH:
                result += "March";
                break;
            case Calendar.APRIL:
                result += "April";
                break;
            case Calendar.MAY:
                result += "May";
                break;
            case Calendar.JUNE:
                result += "June";
                break;
            case Calendar.JULY:
                result += "July";
                break;
            case Calendar.AUGUST:
                result += "August";
                break;
            case Calendar.SEPTEMBER:
                result += "September";
                break;
            case Calendar.OCTOBER:
                result += "October";
                break;
            case Calendar.NOVEMBER:
                result += "November";
                break;
            case Calendar.DECEMBER:
                result += "December";
                break;
        }

        result += " ";

        // Add the date
        result += c.get(Calendar.DAY_OF_MONTH);

        return result;
    }
}
