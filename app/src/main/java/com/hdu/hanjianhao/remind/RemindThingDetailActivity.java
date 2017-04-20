package com.hdu.hanjianhao.remind;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.TimePickerView;
import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.getbase.floatingactionbutton.FloatingActionButton;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class RemindThingDetailActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    //组件
    private Toolbar mToolbar;
    private ImageView imgCategory;
    private TextView name, createDate, manufactureDate, guaranteePeriod, deadline, leftGuarantee;
    private Switch isRemind;
    private FloatingActionButton editButton;
    private EditText editName;
    private ImageButton imgBtnEditName, imgBtnManufactureDate, imgBtnGuaranteePeriod;

    //变量
    private boolean nameEdited, manufactureDateEdited, guaranteePeriodEdited, remindEdited;
    private Reminder reminder;
    private int guaranteeYear, guaranteeMonth, guaranteeDay;
    private int manufactureYear, manufactureMonth, manufactureDay;
    private int deadlineYear, deadlineMonth, deadlineDay;
    private List<String> yearList, monthList, dayList;
    private Calendar calendar;

    //工具
    private DateTimeCalculator calculator = new DateTimeCalculator();
    private AlarmReceiver mAlarmReceiver = new AlarmReceiver();
    private TimePickerView pvManufactureDate;
    private OptionsPickerView pvGuaranteePeriod;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remind_thing_detail);

        reminder = DataSupport.find(Reminder.class, getIntent().getIntExtra("id", 0));
        //防止用户取消日期选择 导致最后赋空值
        Integer manufactureDateSplit[] = calculator.getDateSplit(reminder.getManufactureDate());
        manufactureYear = manufactureDateSplit[0];
        manufactureMonth = manufactureDateSplit[1];
        manufactureDay = manufactureDateSplit[2];
        //防止用户取消保质期选择 导致最后赋空值
        Integer guaranteePeriodSplit[] = calculator.getDateSplit(reminder.getGuaranteePeriod());
        guaranteeYear = guaranteePeriodSplit[0];
        guaranteeMonth = guaranteePeriodSplit[1];
        guaranteeDay = guaranteePeriodSplit[2];

        mToolbar = (Toolbar) findViewById(R.id.toolbar_detail);
        imgCategory = (ImageView) findViewById(R.id.img_category);
        name = (TextView) findViewById(R.id.text_name);
        createDate = (TextView) findViewById(R.id.text_create_date);
        manufactureDate = (TextView) findViewById(R.id.text_manufacture_date);
        guaranteePeriod = (TextView) findViewById(R.id.text_guarantee_period);
        deadline = (TextView) findViewById(R.id.text_deadline);
        leftGuarantee = (TextView) findViewById(R.id.text_left_guarantee);
        isRemind = (Switch) findViewById(R.id.btn_is_remind);
        editButton = (FloatingActionButton) findViewById(R.id.edit_btn);
        editName = (EditText) findViewById(R.id.edit_name);
        imgBtnEditName = (ImageButton) findViewById(R.id.img_btn_edit_name);
        imgBtnManufactureDate = (ImageButton) findViewById(R.id.img_btn_edit_manufacture_date);
        imgBtnGuaranteePeriod = (ImageButton) findViewById(R.id.img_btn_edit_guarantee_period);

        setSupportActionBar(mToolbar);
        imgCategory.setImageResource(getImgResourceId(reminder.getCategory()));
        name.setText(reminder.getName());
        createDate.setText(calculator.getDisplayDate(reminder.getCreateDate()));
        manufactureDate.setText(calculator.getDisplayDate(reminder.getManufactureDate()));
        guaranteePeriod.setText(calculator.getDisplayGuarantee(reminder.getGuaranteePeriod()));
        deadline.setText(calculator.getDisplayDate(reminder.getDeadline()));
        leftGuarantee.setText(calculator.getDisplayLeftGuarantee(reminder.getLeftGuarantee()));
        if (reminder.getIsRemind() == 1)
            isRemind.setChecked(true);
        else
            isRemind.setChecked(false);

        initTimeList();
        initPvGuaranteePeriod();
        initPvManufactureDate();
        calendar = Calendar.getInstance();
        editName.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER){
                    if (event.getAction() == KeyEvent.ACTION_UP){
                        InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (imm.isActive())
                            imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0 );

                        editName.setVisibility(View.INVISIBLE);
                        name.setText(editName.getText().toString());
                        name.setVisibility(View.VISIBLE);

                        return true;
                    }
                    return true;
                }
                return false;
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remindEdited = (reminder.getIsRemind() != (isRemind.isChecked()?1:0));
                if (nameEdited || manufactureDateEdited || guaranteePeriodEdited ||remindEdited){
                    if (nameEdited){
                        reminder.setName(name.getText().toString());
                    }
                    /*
                    * 如果生产日期和保质期改了 那就需要重新计算数据 同时重设alarm
                    * 如果生产日期保质期都没有改 那就只是需要重设alarm
                    * */
                    if (guaranteePeriodEdited || manufactureDateEdited){
                        reminder.setManufactureDate(manufactureYear+"-"+manufactureMonth+"-"+manufactureDay);
                        reminder.setGuaranteePeriod(guaranteeYear+"-"+guaranteeMonth+"-"+guaranteeDay);
                        reminder.setDeadline(calculator.getDeadline(reminder));
                        reminder.setLeftGuarantee(calculator.getLeftGuarantee(reminder));
                        reminder.setFresh(calculator.getFresh(reminder));

                        Integer deadlineSplitArray[] = calculator.getDateSplit(reminder.getDeadline());
                        deadlineYear = deadlineSplitArray[0];
                        deadlineMonth = deadlineSplitArray[1];
                        deadlineDay = deadlineSplitArray[2];

                        if (isRemind.isChecked()){
                            if (reminder.getIsRemind() == 0){
                                reminder.setIsRemind(1);
                                calendar = Calendar.getInstance();
                                calendar.set(deadlineYear, --deadlineMonth, deadlineDay, 0, 0, 0);
                                mAlarmReceiver.setAlarm(getApplicationContext(), calendar, reminder.getId());
                            }
                        }else{
                            if (reminder.getIsRemind() == 1){
                                reminder.setIsRemind(0);
                                mAlarmReceiver.cancelAlarm(getApplicationContext(), reminder.getId());
                            }
                        }
                    }else if (remindEdited){
                        if (isRemind.isChecked()){
                            if (reminder.getIsRemind() == 0){
                                reminder.setIsRemind(1);
                                calendar = Calendar.getInstance();
                                calendar.set(deadlineYear, --deadlineMonth, deadlineDay, 0, 0, 0);
                                mAlarmReceiver.setAlarm(getApplicationContext(), calendar, reminder.getId());
                            }
                        }else{
                            if (reminder.getIsRemind() == 1){
                                reminder.setIsRemind(0);
                                mAlarmReceiver.cancelAlarm(getApplicationContext(), reminder.getId());
                            }
                        }
                    }
                    reminder.save();
                }
                Log.d("detail change", "nameEdited: " + nameEdited);
                Log.d("detail change", "manufactureDateEdited: " + manufactureDateEdited);
                Log.d("detail change", "guaranteePeriodEdited: " + guaranteePeriodEdited);
                Log.d("detail change", "remindEdited: " + remindEdited);
                finish();
            }
        });

        imgBtnEditName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameEdited = true;
                name.setVisibility(View.INVISIBLE);
                editName.setVisibility(View.VISIBLE);
                editName.setText(name.getText().toString());
            }
        });

        imgBtnManufactureDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manufactureDateEdited = true;
                pvManufactureDate.show();
            }
        });

        imgBtnGuaranteePeriod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guaranteePeriodEdited = true;
                pvGuaranteePeriod.show();
            }
        });
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
        manufactureYear = year;
        manufactureMonth = ++month;
        manufactureDay = day;
        manufactureDate.setText(manufactureYear+" 年 "+manufactureMonth+" 月 "+manufactureDay+" 日");
    }

    public int getImgResourceId(int category){
        switch(category){
            case 0:return R.drawable.pear;
            case 1:return R.drawable.carrot;
            case 2:return R.drawable.lemon;
            case 3:return R.drawable.drumstick;
            case 4:return R.drawable.pizza;
            case 5:return R.drawable.croissant;
            case 6:return R.drawable.wine;
            case 7:return R.drawable.cookies;
            default:return R.drawable.delete;
        }
    }

    //保质期将会用到yearList monthList dayList 务必放在 initTimeList() 后面
    public void initPvGuaranteePeriod(){
        pvGuaranteePeriod = new  OptionsPickerView.Builder(RemindThingDetailActivity.this, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3 ,View v) {
                guaranteeYear = options1;
                guaranteeMonth = options2;
                guaranteeDay = options3;
                Log.d("add remind", options1+"年"+options2+"月"+options3+"天");
                guaranteePeriod.setText(guaranteeYear+" 年 "+guaranteeMonth+" 月 "+guaranteeDay+" 天 ");
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
        pvManufactureDate = new TimePickerView.Builder(RemindThingDetailActivity.this, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                Calendar dateSelected = Calendar.getInstance();
                dateSelected.setTime(date);
                calendar = Calendar.getInstance();
                manufactureYear = dateSelected.get(Calendar.YEAR);
                manufactureMonth = dateSelected.get(Calendar.MONTH);
                manufactureDay = dateSelected.get(Calendar.DAY_OF_MONTH);
                manufactureDate.setText(manufactureYear+" 年 "+(++manufactureMonth)+" 月 "+manufactureDay+" 日 ");
            }
        }).setType(TimePickerView.Type.YEAR_MONTH_DAY)
                .setLabel("年", "月", "日", "", "", "") //设置空字符串以隐藏单位提示   hide label
                .setDividerColor(R.color.colorAccent)
                .setDate(endDate)
                .setRangDate(startDate, endDate)
                .setContentSize(20)
                .build();
    }

    public void initTimeList() {

        yearList = new ArrayList<>();
        monthList = new ArrayList<>();
        dayList = new ArrayList<>();


        for (int i=0; i<3; ++i)
            yearList.add(i+"");

        for (int i=0; i<12; ++i)
            monthList.add(i+"");

        for (int i=0; i<30; ++i)
            dayList.add(i+"");

    }

}
