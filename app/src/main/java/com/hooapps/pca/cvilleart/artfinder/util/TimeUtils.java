package com.hooapps.pca.cvilleart.artfinder.util;

import java.util.Calendar;

public class TimeUtils {

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
