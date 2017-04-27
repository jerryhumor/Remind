package com.hdu.hanjianhao.remind;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.TimePickerView;
import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.getbase.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class RemindThingAddActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    //组件
    private Toolbar mToolbar;
    private FloatingActionButton addButton;
    private EditText editName, editRemarks;
    private TextView editManufactureDate, editGuaranteePeriod, editAdvancedTime;
    private Switch isRemindSwitch;
    private ImageButton imgBtnPear, imgBtnCarrot, imgBtnLemon, imgBtnDrumstick, imgBtnPizza,
            imgBtnCroissant, imgBtnWine, imgBtnCookies, imgBtnCalendar, imgBtnTime, imgBtnAdvanced;

    //工具
    private DateTimeCalculator calculator = new DateTimeCalculator();
    private AlarmReceiver mAlarmReceiver;

    //变量
    private int deadlineYear, deadlineMonth, deadlineDay;
    private int manufactureYear, manufactureMonth, manufactureDay;
    private int guaranteeYear, guaranteeMonth, guaranteeDay;
    private int advancedDay, advancedHour;
    private int category = 0;
    private List<String> yearList, monthList, dayList;
    private List<String> advancedDayList, advancedHourList;
    private TimePickerView pvManufactureDate;
    private OptionsPickerView pvGuaranteePeriod, pvAdvancedTime;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remind_thing_add);

        mToolbar = (Toolbar) findViewById(R.id.toolbar_add);
        editName = (EditText) findViewById(R.id.edit_name);
        editRemarks = (EditText) findViewById(R.id.edit_remarks);
        editManufactureDate = (TextView) findViewById(R.id.edit_manufacture_date);
        editGuaranteePeriod = (TextView) findViewById(R.id.edit_guarantee_period);
        editAdvancedTime = (TextView) findViewById(R.id.edit_advanced_time);
        imgBtnPear = (ImageButton) findViewById(R.id.img_btn_category_pear);
        imgBtnCarrot = (ImageButton) findViewById(R.id.img_btn_category_carrot);
        imgBtnLemon = (ImageButton) findViewById(R.id.img_btn_category_lemon);
        imgBtnDrumstick = (ImageButton) findViewById(R.id.img_btn_category_drumstick);
        imgBtnPizza = (ImageButton) findViewById(R.id.img_btn_category_pizza);
        imgBtnCroissant = (ImageButton) findViewById(R.id.img_btn_category_croissant);
        imgBtnWine = (ImageButton) findViewById(R.id.img_btn_category_wine);
        imgBtnCookies = (ImageButton) findViewById(R.id.img_btn_category_cookies);
        imgBtnCalendar = (ImageButton) findViewById(R.id.img_btn_calendar);
        imgBtnTime = (ImageButton) findViewById(R.id.img_btn_time);
        imgBtnAdvanced = (ImageButton) findViewById(R.id.img_btn_advanced);
        isRemindSwitch = (Switch) findViewById(R.id.switch_btn_is_remind);
        mAlarmReceiver = new AlarmReceiver();
        calendar = Calendar.getInstance();



        initTimeList();
        initPvManufactureDate();
        initPvGuaranteePeriod();
        initPvAdvancedTime();
        setSupportActionBar(mToolbar);
        addButton = (FloatingActionButton) findViewById(R.id.save_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Reminder reminder = getReminder();
                if (reminder == null)
                    Toast.makeText(RemindThingAddActivity.this, "请完整填写信息", Toast.LENGTH_SHORT).show();
                else if (checkReminder(reminder)){
                    reminder.save();
                    if (reminder.getIsRemind() == 1){
                        calendar = Calendar.getInstance();
                        calendar.set(deadlineYear, --deadlineMonth, deadlineDay, 0, 0, 0);
                        int advancedTime = advancedDay*24+advancedHour;
                        //TODO:放入setAlarm
                        mAlarmReceiver.setAlarm(getApplicationContext(), calendar, reminder.getId(), advancedTime);
                    }
                    finish();
                }
                else
                    Toast.makeText(RemindThingAddActivity.this, "物品已经过期", Toast.LENGTH_SHORT).show();
            }
        });

        imgBtnPear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                category = 0;
                resetCategoryBtnResourceId();
                imgBtnPear.setBackgroundResource(R.drawable.pear);
            }
        });
        imgBtnCarrot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                category = 1;
                resetCategoryBtnResourceId();
                imgBtnCarrot.setBackgroundResource(R.drawable.carrot);
            }
        });
        imgBtnLemon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                category = 2;
                resetCategoryBtnResourceId();
                imgBtnLemon.setBackgroundResource(R.drawable.lemon);
            }
        });
        imgBtnDrumstick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                category = 3;
                resetCategoryBtnResourceId();
                imgBtnDrumstick.setBackgroundResource(R.drawable.drumstick);
            }
        });
        imgBtnPizza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                category = 4;
                resetCategoryBtnResourceId();
                imgBtnPizza.setBackgroundResource(R.drawable.pizza);
            }
        });
        imgBtnCroissant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                category = 5;
                resetCategoryBtnResourceId();
                imgBtnCroissant.setBackgroundResource(R.drawable.croissant);
            }
        });
        imgBtnWine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                category = 6;
                resetCategoryBtnResourceId();
                imgBtnWine.setBackgroundResource(R.drawable.wine);
            }
        });
        imgBtnCookies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                category = 7;
                resetCategoryBtnResourceId();
                imgBtnCookies.setBackgroundResource(R.drawable.cookies);
            }
        });

        imgBtnCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pvManufactureDate.show();
            }
        });

        imgBtnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pvGuaranteePeriod.show();
            }
        });

        imgBtnAdvanced.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pvAdvancedTime.show();
            }
        });
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
        manufactureYear = year;
        manufactureMonth = ++month;
        manufactureDay = day;
        editManufactureDate.setText(manufactureYear+" 年 "+manufactureMonth+" 月 "+manufactureDay+" 日 ");
    }

    public Reminder getReminder(){

        Reminder reminder = new Reminder();
        String name = editName.getText().toString();
        String manufactureDate = editManufactureDate.getText().toString();
        String guaranteePeriod = editGuaranteePeriod.getText().toString();
        String remarks = editRemarks.getText().toString();

        if (name.isEmpty() || manufactureDate.isEmpty() || guaranteePeriod.isEmpty())
            return null;

        reminder.setName(name);
        reminder.setCategory(category);
        reminder.setCreateDate(calculator.getCurrentTime());
        reminder.setManufactureDate(manufactureYear+"-"+manufactureMonth+"-"+manufactureDay);
        reminder.setGuaranteePeriod(guaranteeYear+"-"+guaranteeMonth+"-"+guaranteeDay);
        reminder.setDeadline(calculator.getDeadline(reminder));
        reminder.setLeftGuarantee(calculator.firstGetLeftGuarantee(reminder));
        reminder.setFresh(calculator.getFresh(reminder));
        reminder.setIsRemind(isRemindSwitch.isChecked()? 1:0);
        reminder.setAdvanceHour(advancedDay*24+advancedHour);
        reminder.setRemarks(remarks);


        String deadlineSplit[] = reminder.getDeadline().split("-");
        deadlineYear = Integer.valueOf(deadlineSplit[0]);
        deadlineMonth = Integer.valueOf(deadlineSplit[1]);
        deadlineDay = Integer.valueOf(deadlineSplit[2]);

        return reminder;
    }


    public boolean checkReminder(Reminder reminder){
        if (reminder.getFresh() < 0){
            return false;
        }
        if (reminder.getLeftGuarantee() < advancedDay*24+advancedHour){
            Log.d("add remind", "left: " + reminder.getLeftGuarantee() + " advance day: " + advancedDay);
            return false;
        }

        return true;
    }

    public void initTimeList() {

        yearList = new ArrayList<>();
        monthList = new ArrayList<>();
        dayList = new ArrayList<>();
        advancedDayList = new ArrayList<>();
        advancedHourList = new ArrayList<>();


        for (int i=0; i<3; ++i)
            yearList.add(i+"");

        for (int i=0; i<12; ++i)
            monthList.add(i+"");

        for (int i=0; i<30; ++i)
            dayList.add(i+"");

        for (int i=1; i<30; ++i)
            advancedDayList.add("前"+i+"天");

        for (int i=0; i<24; ++i)
            advancedHourList.add(i+"点");

    }

    //保质期将会用到yearList monthList dayList 务必放在 initTimeList() 后面
    public void initPvGuaranteePeriod(){
        pvGuaranteePeriod = new  OptionsPickerView.Builder(RemindThingAddActivity.this, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3 ,View v) {
                guaranteeYear = options1;
                guaranteeMonth = options2;
                guaranteeDay = options3;
                Log.d("add remind", options1+"年"+options2+"月"+(options3+1)+"天");
                editGuaranteePeriod.setText(guaranteeYear+" 年 "+guaranteeMonth+" 月 "+guaranteeDay+" 天 ");
            }
        }).setTitleText("保质期选择")
                .setLabels("年", "月", "日")
                .build();
        pvGuaranteePeriod.setNPicker(yearList, monthList, dayList);
    }

    public void initPvManufactureDate(){
        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();
        startDate.set(1980, 0, 0);
        pvManufactureDate = new TimePickerView.Builder(RemindThingAddActivity.this, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                Calendar dateSelected = Calendar.getInstance();
                dateSelected.setTime(date);
                calendar = Calendar.getInstance();
                manufactureYear = dateSelected.get(Calendar.YEAR);
                manufactureMonth = dateSelected.get(Calendar.MONTH);
                manufactureDay = dateSelected.get(Calendar.DAY_OF_MONTH);
                editManufactureDate.setText(manufactureYear+" 年 "+(++manufactureMonth)+" 月 "+manufactureDay+" 日 ");
            }
        }).setType(TimePickerView.Type.YEAR_MONTH_DAY)
                .setLabel("年", "月", "日", "", "", "") //设置空字符串以隐藏单位提示   hide label
                .setDividerColor(R.color.colorAccent)
                .setDate(endDate)
                .setRangDate(startDate, endDate)
                .setContentSize(20)
                .build();
    }

    public void initPvAdvancedTime(){
        pvAdvancedTime = new  OptionsPickerView.Builder(RemindThingAddActivity.this, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3 ,View v) {
                advancedDay = options1;
                advancedHour = 24 - options2;
                editAdvancedTime.setText("将在前" + (advancedDay+1) + "天的" + options2 + "点通知");
                Log.d("add remind", options1+"天"+options2+"小时");
            }
        }).setTitleText("提前提醒时间选择")
                .setLabels("", "", "")
                .build();
        pvAdvancedTime.setNPicker(advancedDayList, advancedHourList, null);
    }

    public void resetCategoryBtnResourceId(){
        //TODO:提高效率
        imgBtnPear.setBackgroundResource(R.drawable.pear_unselected);
        imgBtnCarrot.setBackgroundResource(R.drawable.carrot_unselected);
        imgBtnLemon.setBackgroundResource(R.drawable.lemon_unselected);
        imgBtnDrumstick.setBackgroundResource(R.drawable.drumstick_unselected);
        imgBtnPizza.setBackgroundResource(R.drawable.pizza_unselected);
        imgBtnCroissant.setBackgroundResource(R.drawable.croissant_unselected);
        imgBtnWine.setBackgroundResource(R.drawable.wine_unselected);
        imgBtnCookies.setBackgroundResource(R.drawable.cookies_unselected);
    }


}
