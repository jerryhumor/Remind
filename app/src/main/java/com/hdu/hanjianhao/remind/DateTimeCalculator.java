package com.hdu.hanjianhao.remind;


import android.content.Intent;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by hanjianhao on 17/4/11.
 */

public class DateTimeCalculator {

    //获取当前时间 格式为"yyyy-MM-dd"
    public String getCurrentTime(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH");
        String date = simpleDateFormat.format(new Date());
        return date;
    }

    //传入一个reminder 得到最后期限
    public String getDeadline(Reminder reminder){

        String[] guaranteeDate = reminder.getGuaranteePeriod().split("-");
        String[] manufactureDate = reminder.getManufactureDate().split("-");
        String deadline;

        int guaranteeYear = Integer.valueOf(guaranteeDate[0]);
        int guaranteeMonth = Integer.valueOf(guaranteeDate[1]);
        int guaranteeDay = Integer.valueOf(guaranteeDate[2]);

        int manufactureYear = Integer.valueOf(manufactureDate[0]);
        int manufactureMonth = Integer.valueOf(manufactureDate[1]);
        int manufactureDay = Integer.valueOf(manufactureDate[2]);

        String tag = "getDeadline";

        Log.d(tag, "guarantee period: " + guaranteeYear);
        Log.d(tag, "guarantee period: " + guaranteeMonth);
        Log.d(tag, "guarantee period: " + guaranteeDay);

        Calendar calendar = Calendar.getInstance();
        calendar.set(manufactureYear, manufactureMonth, manufactureDay);

        calendar.add(Calendar.YEAR, guaranteeYear);
        calendar.add(Calendar.MONTH, guaranteeMonth);
        calendar.add(Calendar.DAY_OF_MONTH, guaranteeDay);

        deadline = calendar.get(Calendar.YEAR) + "-" +
                calendar.get(Calendar.MONTH) + "-" +
                calendar.get(Calendar.DAY_OF_MONTH);
        return deadline;
    }

    //传入一个reminder 得到剩余保质期（未进行检验）
    //根据currentTime 和 deadline 来计算 务必放到 getDeadline() 之后
    public long getLeftGuarantee(Reminder reminder) {
        //TODO:检测是否过期
        String currentTime = getCurrentTime();
        String deadline = reminder.getDeadline();
        String[] currentTimeSplit = currentTime.split("-");
        String[] deadlineSplit = deadline.split("-");
        int currentYear = Integer.valueOf(currentTimeSplit[0]);
        int currentMonth = Integer.valueOf(currentTimeSplit[1]);
        int currentDay = Integer.valueOf(currentTimeSplit[2]);
        int currentHour = Integer.valueOf(currentTimeSplit[3]);
        int deadlineYear = Integer.valueOf(deadlineSplit[0]);
        int deadlineMonth = Integer.valueOf(deadlineSplit[1]);
        int deadlineDay = Integer.valueOf(deadlineSplit[2]);

        Calendar currentCalendar = Calendar.getInstance();
        currentCalendar.set(currentYear, currentMonth, currentDay, currentHour, 0, 0);
        Calendar deadlineCalendar = Calendar.getInstance();
        deadlineCalendar.set(deadlineYear, deadlineMonth, deadlineDay, 0, 0, 0);

        long guaranteeMillis = deadlineCalendar.getTimeInMillis() - currentCalendar.getTimeInMillis();
        long guaranteeHours = guaranteeMillis/(1000*60*60);

        Log.d("calculator", "left guarantee: " + guaranteeHours);

        return guaranteeHours;
    }

    //传入一个reminder 得到新鲜度
    //根据剩余保质期和保质期来计算 务必放到 getLeftGuarantee() 之后
    public float getFresh(Reminder reminder){

        String tag = "getFresh";

        String[] guarantee = reminder.getGuaranteePeriod().split("-");
        int guaranteeYear = Integer.valueOf(guarantee[0]);
        int guaranteeMonth = Integer.valueOf(guarantee[1]);
        int guaranteeDay = Integer.valueOf(guarantee[2]);

        long guaranteeDays = guaranteeYear * 365 + guaranteeMonth * 30 + guaranteeDay;

        if (guaranteeDays == 0)
            return 0f;
        long leftGuaranteeHours = reminder.getLeftGuarantee();

        Log.d(tag, "left guarantee: " + reminder.getLeftGuarantee());
        Log.d(tag, "guarantee: " + guaranteeDays);

        return (float)leftGuaranteeHours/(guaranteeDays*24);
    }

    //传入一个reminder 得到剩余保质期
    //根据createDate 和 deadline 来计算 务必放到 getDeadline() 之后
    public long firstGetLeftGuarantee(Reminder reminder){

        String tag = "first guarantee";

        //TODO:检测是否过期
        String currentDateSpilt[] = reminder.getCreateDate().split("-");
        String deadlineSplit[] = reminder.getDeadline().split("-");
        int currentYear = Integer.valueOf(currentDateSpilt[0]);
        int currentMonth = Integer.valueOf(currentDateSpilt[1]);
        int currentDay = Integer.valueOf(currentDateSpilt[2]);
        int currentHour = Integer.valueOf(currentDateSpilt[3]);
        int deadlineYear = Integer.valueOf(deadlineSplit[0]);
        int deadlineMonth = Integer.valueOf(deadlineSplit[1]);
        int deadlineDay = Integer.valueOf(deadlineSplit[2]);

        Calendar currentCalendar = Calendar.getInstance();
        currentCalendar.set(currentYear, currentMonth, currentDay, currentHour, 0, 0);
        Calendar deadlineCalendar = Calendar.getInstance();
        deadlineCalendar.set(deadlineYear, deadlineMonth, deadlineDay, 0, 0, 0);

        long deadlineMillis = deadlineCalendar.getTimeInMillis();
        long currentMillis = currentCalendar.getTimeInMillis();

        long guaranteeMillis = deadlineMillis - currentMillis;
        long guaranteeHours = guaranteeMillis/(1000*60*60);

        Log.d(tag, "current year: " + currentYear);
        Log.d(tag, "current month: " + currentMonth);
        Log.d(tag, "current day: " + currentDay);
        Log.d(tag, "current hour: " + currentHour);
        Log.d(tag, "deadlineMillis: " + deadlineMillis);
        Log.d(tag, "currentMillis: " + currentMillis);
        Log.d(tag, "left guarantee millis: " + guaranteeMillis);

        return guaranteeHours;
    }

    public String getDisplayLeftGuarantee(long leftGuarantee){
        leftGuarantee /= 24;
        long year = leftGuarantee/365;
        long days = leftGuarantee%365;
        long month = days/30;
        long day = days%30;

        if (year != 0 && month != 0){
            if (month == 12){
                year += 1;
                month = 0;
                return "剩余约 " + year + " 年";
            }
            return "剩余约 " + year + " 年 " + month + " 个月";
        }else if (year != 0 && month == 0){
            return "剩余约 " + year + " 年";
        }else if (year == 0 && month != 0){
            if (month == 12){
                return "剩余约 1 年";
            }
            return "剩余 " + month + " 个月 " + day + " 天";
        }else{
            return "剩余 " + day + " 天";
        }
    }

    public String getDisplayGuarantee(String guarantee){
        String guaranteeSplit[] = guarantee.split("-");
        int guaranteeYear = Integer.valueOf(guaranteeSplit[0]);
        int guaranteeMonth = Integer.valueOf(guaranteeSplit[1]);
        int guaranteeDay = Integer.valueOf(guaranteeSplit[2]);
        if (guaranteeYear == 0){
            if (guaranteeMonth ==0)
                return guaranteeDay + " 天";
            else
                return guaranteeMonth + " 月 " + guaranteeDay + " 天";
        }
        else
            return guaranteeYear + " 年 " + guaranteeMonth + " 月 " + guaranteeDay + " 天";
    }

    public String getDisplayDate(String date){
        String dateSplit[] = date.split("-");
        String year = dateSplit[0];
        String month = dateSplit[1];
        String day = dateSplit[2];

        return year + " 年 " + month + " 月 " + day + " 日";
    }

    public Integer[] getDateSplit(String date){
        Integer[] dateArray = new Integer[3];
        String dateSplit[] = date.split("-");
        dateArray[0] = Integer.valueOf(dateSplit[0]);
        dateArray[1] = Integer.valueOf(dateSplit[1]);
        dateArray[2] = Integer.valueOf(dateSplit[2]);
        return dateArray;
    }

    public String getDisplayRemindDate(int advancedHour){
        int advancedDay = advancedHour/24 + 1;
        advancedHour %= 24;
        advancedHour = 24 - advancedHour;

        return "将在前" + advancedDay + "天" + advancedHour + "点提醒";
    }
}
