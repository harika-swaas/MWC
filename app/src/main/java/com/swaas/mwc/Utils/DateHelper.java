package com.swaas.mwc.Utils;

import android.text.TextUtils;
import android.util.Log;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by barath on 7/24/2018.
 */

public class DateHelper {

    public static String getDBFormat(String dateStr, String givenFormat) {
        Log.d("GivenDateFormat", dateStr);
        SimpleDateFormat formatter = new SimpleDateFormat(givenFormat);
        Date date;
        try {
            date = formatter.parse(dateStr);


            SimpleDateFormat convertFormat = new SimpleDateFormat("yyyy-MM-dd");
            Log.d("DBFormat", convertFormat.format(date));
            return convertFormat.format(date);
        } catch (Throwable t) {
            return "";
        }
    }

    public static String getDisplayFormat(String dateStr, String givenFormat) {
        SimpleDateFormat formatter = new SimpleDateFormat(givenFormat);
        Date date;
        try {
            date = formatter.parse(dateStr);
            SimpleDateFormat convertFormat = new SimpleDateFormat("dd-MMM-yyyy");
            return convertFormat.format(date);
        } catch (Throwable t) {
            return "";
        }
    }

    public static String getDisplayFormatDateMonth(String dateStr, String givenFormat) {
        SimpleDateFormat formatter = new SimpleDateFormat(givenFormat);
        Date date;
        try {
            date = formatter.parse(dateStr);
            SimpleDateFormat convertFormat = new SimpleDateFormat("dd MMM");
            return convertFormat.format(date);
        } catch (Throwable t) {
            return "";
        }
    }


    public static String getCurrentDate() {
        String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        return currentDate;
    }

    public static String getCurrentTime() {
        String currentTime = new SimpleDateFormat("hh:mm a").format(new Date());
        return currentTime;
    }

    public static String convertDateToString(Date date, String format) {
        String dateString = new SimpleDateFormat(format).format(date);
        return dateString;
    }

    public static String getDateAndTimeFormat(String dateStr,String givenFormat) {
        SimpleDateFormat formatter = new SimpleDateFormat(givenFormat);
        Date date;
        try {
            date = formatter.parse(dateStr);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm a");
//            String formattedDate = dateFormat.format(new Date()).toString();
            return dateFormat.format(date);
        } catch (Throwable t) {
            return "";
        }
    }

    public static Date convertStringToDate(String dateInString, String format) {
        Date date = null;
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(format);
            date = formatter.parse(dateInString);
        } catch (ParseException e) {
            date = new Date();
        }
        return date;
    }

    public static long convertDateStringtoLong(String dateStirng) {
        String[] dateArray = dateStirng.split("-");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(dateArray[0]);
        stringBuilder.append(dateArray[1]);
        stringBuilder.append(dateArray[2]);
        return Long.parseLong(stringBuilder.toString());
    }

    public static String getStartOrLastDate(String dateString, boolean getLastDate) {
        if (!TextUtils.isEmpty(dateString)) {
            String[] dateArray = dateString.split("-");
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(dateArray[0]);
            stringBuilder.append("-");
            stringBuilder.append(dateArray[1].trim().length() == 1 ? "0" + dateArray[1] : dateArray[1]);
            stringBuilder.append("-");
            int year = Integer.parseInt(dateArray[0]);
            int month = Integer.parseInt(dateArray[1]);
            if (getLastDate) {
                if (month == 2) {
                    if ((year % 4) == 0) {
                        stringBuilder.append("29");
                    } else {
                        stringBuilder.append("28");
                    }
                } else if ((month == 1) || (month == 12) || (month == 3) || (month == 5) || (month == 7) || (month == 8) || (month == 10)) {
                    stringBuilder.append("31");
                } else {
                    stringBuilder.append("30");
                }
            } else {
                stringBuilder.append("01");
            }
            return stringBuilder.toString();
        }
        return "";
    }

    public static int getDaysDifferenceBetweenDates(String date1, String date2) {
        int diff = -1;
        int totalDaysForMonth1 = 0;
        int totalDaysForMonth2 = 0;
        String[] dateArray1 = date1.split("-");
        String[] dateArray2 = date2.split("-");
        int year1 = Integer.parseInt(dateArray1[0]);
        int month1 = Integer.parseInt(dateArray1[1]);
        int day1 = Integer.parseInt(dateArray1[2]);
        int year2 = Integer.parseInt(dateArray2[0]);
        int month2 = Integer.parseInt(dateArray2[1]);
        int day2 = Integer.parseInt(dateArray2[2]);
        if (year2 == year1) {
            if (month2 - month1 == 1) {
                if (month2 == 2) {
                    if ((year2 % 4) == 0) {
                        totalDaysForMonth2 = 29;
                    } else {
                        totalDaysForMonth2 = 28;
                    }
                } else if ((month2 == 1) || (month2 == 12) || (month2 == 3) || (month2 == 5) || (month2 == 7) || (month2 == 8) || (month2 == 10)) {
                    totalDaysForMonth2 = 31;
                } else {
                    totalDaysForMonth2 = 30;
                }
                if (month1 == 2) {
                    if ((year1 % 4) == 0) {
                        totalDaysForMonth1 = 29;
                    } else {
                        totalDaysForMonth1 = 28;
                    }
                } else if ((month1 == 1) || (month1 == 12) || (month1 == 3) || (month1 == 5) || (month1 == 7) || (month1 == 8) || (month1 == 10)) {
                    totalDaysForMonth1 = 31;
                } else {
                    totalDaysForMonth1 = 30;
                }
                diff = day2 + (totalDaysForMonth1 - day1 + 1);
            } else if (month2 == month1) {
                diff = day2 - day1;
            } else {

            }
        } else if (year2 > year1) {
            if (month2 == 1 && month1 == 12) {
                diff = day2 + (30 - day1);
            }
        }
        return diff;
    }

    public static String getMonth(int month) {
        if(month <= 0){
            return "NA";
        }
        return new DateFormatSymbols().getMonths()[month-1];
    }
    public static String timeConversion(int totalSeconds) {

        String timeValue = "";
        final int MINUTES_IN_AN_HOUR = 60;
        final int SECONDS_IN_A_MINUTE = 60;

        int seconds = totalSeconds % SECONDS_IN_A_MINUTE;
        int totalMinutes = totalSeconds / SECONDS_IN_A_MINUTE;
        int minutes = totalMinutes % MINUTES_IN_AN_HOUR;
        int hours = totalMinutes / MINUTES_IN_AN_HOUR;


        if(hours > 0){
            if(minutes > 0){
                if(seconds == 0){
                    seconds = 00;
                }
                if(minutes == 0){
                    minutes = 00;
                }
                timeValue = String.valueOf(hours)+":"+String.valueOf(minutes)+":"+String.valueOf(seconds)+" Hrs";
            }
        }else{
            if(minutes > 0){
                if(seconds == 0){
                    seconds = 00;
                }
                timeValue = String.valueOf(minutes)+":"+String.valueOf(seconds)+" Mins";
                if(minutes <= 9){
                    timeValue = "0"+String.valueOf(minutes)+":"+String.valueOf(seconds)+" Mins";
                }
                if(seconds <= 9 ){
                    seconds = Integer.parseInt(String.valueOf("0"+seconds));
                    timeValue = String.valueOf(minutes)+":"+String.valueOf(seconds)+" Mins";
                }

                if(minutes <= 9 && seconds <= 9 ){
                    timeValue = "0"+String.valueOf(minutes)+":"+"0"+String.valueOf(seconds)+" Mins";
                }


            } else {
                //timeValue = String.valueOf(seconds)+" (sec)";
                if(seconds <= 9 ){
                    timeValue = "00:0"+String.valueOf(seconds)+" Secs";
                }else{
                    timeValue = "00:"+String.valueOf(seconds)+" Secs";
                }

            }
        }




        // return hours + " hours " + minutes + " minutes " + seconds + " seconds";
        return timeValue;
    }


    public static String getDateAndTimeFormatForMode(String dateStr) {
        String shortTimeStr = null;
        Date date;
        try {
            java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            date = null;
            date = df.parse(dateStr);
            SimpleDateFormat sdf = new SimpleDateFormat("a");
            shortTimeStr = sdf.format(date);

        } catch (ParseException e) {
            // To change body of catch statement use File | Settings | File Templates.
            e.printStackTrace();
        }
        return shortTimeStr;
    }

    public static String getTimeFormDateString(String dateStr) {
        String shortTimeStr = null;
        Date date;
        try {
            java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd h:mm:ss");
            date = null;
            date = df.parse(dateStr);
            SimpleDateFormat sdf = new SimpleDateFormat("h:mm a");
            shortTimeStr = sdf.format(date);

        } catch (ParseException e) {
            // To change body of catch statement use File | Settings | File Templates.
            e.printStackTrace();
        }
        return shortTimeStr;
    }

    public static String getDateAndTimeAM_PMFormat(String dateStr,String givenFormat) {
        SimpleDateFormat formatter = new SimpleDateFormat(givenFormat);
        Date date;
        try {
            date = formatter.parse(dateStr);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            String formattedDate = dateFormat.format(new Date()).toString();
            return dateFormat.format(date);
        } catch (Throwable t) {
            return "";
        }
    }


    public static String get24hoursCurrentTime() {
        String currentTime = new SimpleDateFormat("HH:mm:ss").format(new Date());
        return currentTime;
    }
}

