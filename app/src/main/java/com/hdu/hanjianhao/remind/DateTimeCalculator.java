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
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
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
        int deadlineYear = Integer.valueOf(deadlineSplit[0]);
        int deadlineMonth = Integer.valueOf(deadlineSplit[1]);
        int deadlineDay = Integer.valueOf(deadlineSplit[2]);

        Calendar currentCalendar = Calendar.getInstance();
        currentCalendar.set(currentYear, currentMonth, currentDay);
        Calendar deadlineCalendar = Calendar.getInstance();
        deadlineCalendar.set(deadlineYear, deadlineMonth, deadlineDay);

        long guaranteeMillis = deadlineCalendar.getTimeInMillis() - currentCalendar.getTimeInMillis();
        long guaranteeDays = guaranteeMillis/(1000*60*60*24);

        return guaranteeDays;
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
        long leftGuaranteeDays = reminder.getLeftGuarantee();

        Log.d(tag, "left guarantee: " + reminder.getLeftGuarantee());
        Log.d(tag, "guarantee: " + guaranteeDays);

        return (float)leftGuaranteeDays/guaranteeDays;
    }

    //传入一个reminder 得到剩余保质期
    //根据createDate 和 deadline 来计算 务必放到 getDeadline() 之后
    public long firstGetLeftGuarantee(Reminder reminder){
        //TODO:检测是否过期
        String currentDateSpilt[] = reminder.getCreateDate().split("-");
        String deadlineSplit[] = reminder.getDeadline().split("-");
        int currentYear = Integer.valueOf(currentDateSpilt[0]);
        int currentMonth = Integer.valueOf(currentDateSpilt[1]);
        int currentDay = Integer.valueOf(currentDateSpilt[2]);
        int deadlineYear = Integer.valueOf(deadlineSplit[0]);
        int deadlineMonth = Integer.valueOf(deadlineSplit[1]);
        int deadlineDay = Integer.valueOf(deadlineSplit[2]);

        Calendar currentCalendar = Calendar.getInstance();
        currentCalendar.set(currentYear, currentMonth, currentDay);
        Calendar deadlineCalendar = Calendar.getInstance();
        deadlineCalendar.set(deadlineYear, deadlineMonth, deadlineDay);

        long guaranteeMillis = deadlineCalendar.getTimeInMillis() - currentCalendar.getTimeInMillis();
        long guaranteeDays = guaranteeMillis/(1000*60*60*24);

        return guaranteeDays;
    }

    public String getDisplayLeftGuarantee(long leftGuarantee){
        long day = leftGuarantee%30;
        long month = leftGuarantee/30;
        long year = month/365;
        if (month == 0)
            return day+" 天";
        else{
            if (year == 0)
                return month + " 月 " + day + " 天";
            else
                return year + " 年 " + month + " 月 " + day + " 天";
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
}
