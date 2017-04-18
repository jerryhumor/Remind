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
import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.getbase.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class RemindThingAddActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    //组件
    private Toolbar mToolbar;
    private FloatingActionButton addButton;
    private EditText editName;
    private TextView editManufactureDate, editGuaranteePeriod;
    private Switch isRemindSwitch;
    private ImageButton imgBtnPear, imgBtnCarrot, imgBtnLemon, imgBtnDrumstick, imgBtnPizza,
            imgBtnCroissant, imgBtnWine, imgBtnCookies, imgBtnCalendar, imgBtnTime;

    //工具
    private DateTimeCalculator calculator = new DateTimeCalculator();
    private AlarmReceiver mAlarmReceiver;

    //变量
    private int deadlineYear, deadlineMonth, deadlineDay;
    private int manufactureYear, manufactureMonth, manufactureDay;
    private int guaranteeYear, guaranteeMonth, guaranteeDay;
    private int category = 0;
    private List<String> yearList = new ArrayList<>();
    private List<List<String>> monthList = new ArrayList<>();
    private List<List<List<String>>> dayList = new ArrayList<>();
    private DatePickerDialog datePickerDialog;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remind_thing_add);

        mToolbar = (Toolbar) findViewById(R.id.toolbar_add);
        editName = (EditText) findViewById(R.id.edit_name);
        editManufactureDate = (TextView) findViewById(R.id.edit_manufacture_date);
        editGuaranteePeriod = (TextView) findViewById(R.id.edit_guarantee_period);
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
        isRemindSwitch = (Switch) findViewById(R.id.switch_btn_is_remind);
        mAlarmReceiver = new AlarmReceiver();
        calendar = Calendar.getInstance();
        datePickerDialog = DatePickerDialog.newInstance(this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), false);

        initTimeList();
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
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(deadlineYear, --deadlineMonth, deadlineDay, 0, 0, 0);
                        mAlarmReceiver.setAlarm(getApplicationContext(), calendar, reminder.getId());
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
                datePickerDialog.setCloseOnSingleTapDay(false);
                datePickerDialog.show(getSupportFragmentManager(), "calendar");
            }
        });

        imgBtnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OptionsPickerView pvOptions = new  OptionsPickerView.Builder(RemindThingAddActivity.this, new OptionsPickerView.OnOptionsSelectListener() {
                    @Override
                    public void onOptionsSelect(int options1, int options2, int options3 ,View v) {
                        guaranteeYear = options1;
                        guaranteeMonth = options2;
                        guaranteeDay = options3;
                        Log.d("add remind", options1+"年"+options2+"月"+options3+"天");
                        editGuaranteePeriod.setText(guaranteeYear+" 年 "+guaranteeMonth+" 月 "+guaranteeDay+" 天 ");
                    }
                }).setTitleText("保质期选择").setLabels("年", "月", "日").build();
                 pvOptions.setPicker(yearList, monthList, dayList);
                pvOptions.show();
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

        String deadlineSplit[] = reminder.getDeadline().split("-");
        deadlineYear = Integer.valueOf(deadlineSplit[0]);
        deadlineMonth = Integer.valueOf(deadlineSplit[1]);
        deadlineDay = Integer.valueOf(deadlineSplit[2]);

        return reminder;
    }


    public boolean checkReminder(Reminder reminder){
        if (reminder.getFresh() <= 0){
            return false;
        }
        return true;
    }

    public void initTimeList() {

        for (int i=0; i<3; ++i)
            yearList.add(i+"");

        List<String> monthList1 = new ArrayList<>();
        for (int i=0; i<12; ++i)
            monthList1.add(i+"");

        for (int i=0; i<3; ++i)
            monthList.add(monthList1);

        List<String> dayList1 = new ArrayList<>();
        for (int i=0; i<30; ++i)
            dayList1.add(i+"");

        List<List<String>> dayList2 = new ArrayList<>();
        for (int i=0; i<12; ++i)
            dayList2.add(dayList1);

        for (int i=0; i<3; ++i)
            dayList.add(dayList2);

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
