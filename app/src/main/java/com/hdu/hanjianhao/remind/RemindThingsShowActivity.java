package com.hdu.hanjianhao.remind;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bignerdranch.android.multiselector.ModalMultiSelectorCallback;
import com.bignerdranch.android.multiselector.MultiSelector;
import com.bignerdranch.android.multiselector.SwappingHolder;
import com.getbase.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class RemindThingsShowActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private TextView noReminderTv;
    private RecyclerView mReminderList;
    private MultiSelector mMultiSelector = new MultiSelector();
    private FloatingActionButton mAddButton;
    private List<Reminder> reminderList = new ArrayList<>();
    private ReminderDatabase db = new ReminderDatabase();
    private DateTimeCalculator dateTimeCalculator = new DateTimeCalculator();
    private AlarmReceiver mAlarmReceiver = new AlarmReceiver();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remind_things_show);

        mToolbar = (Toolbar) findViewById(R.id.toolbar_show);
        noReminderTv = (TextView) findViewById(R.id.no_reminder_tv);
        setSupportActionBar(mToolbar);

        mAddButton = (FloatingActionButton) findViewById(R.id.add_button);
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RemindThingsShowActivity.this, RemindThingAddActivity.class);
                startActivity(intent);
            }
        });



        mReminderList = (RecyclerView) findViewById(R.id.reminder_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mReminderList.setLayoutManager(layoutManager);

        //TODO:优化 这边db中的freshDatabase()执行了两次
        reminderList = db.getReminders();

        ReminderAdapter reminderAdapter = new ReminderAdapter(reminderList);
        mReminderList.setAdapter(reminderAdapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        reminderList.clear();
        reminderList.addAll(db.getReminders());
        if (reminderList.isEmpty())
            noReminderTv.setVisibility(View.VISIBLE);
        else
            noReminderTv.setVisibility(View.INVISIBLE);
        mReminderList.getAdapter().notifyDataSetChanged();
        Log.d("onResume", "in on resume");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_about, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_about:
                Toast.makeText(this, "click about", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
        return true;
    }
 
    private ActionMode.Callback mDeleteMode = new ModalMultiSelectorCallback(mMultiSelector) {

        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            getMenuInflater().inflate(R.menu.menu_edit_reminder, menu);
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            if (item.getItemId() == R.id.action_delete_reminder) {

                for (int i=mReminderList.getAdapter().getItemCount(); i>=0; i--){
                    if(mMultiSelector.isSelected(i, 0)){
                        int id = reminderList.get(i).getId();
                        int isRemind = reminderList.get(i).getIsRemind();                   //得到移除reminder的id和isRemind
                        if (isRemind == 1){                                                 //如果设置了通知 则取消通知
                            mAlarmReceiver.cancelAlarm(getApplicationContext(), id);
                        }
                        db.removeReminder(id);                                              //数据库中移除reminder记录
                        reminderList.remove(i);                                             //数据源中移除reminder记录
                        mReminderList.getAdapter().notifyItemRemoved(i);                    //刷新recyclerview
                    }
                }
                mode.finish();
                return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            Log.d("location", "destroy action mode");
            mMultiSelector.clearSelections();
            mMultiSelector.setSelectable(false);

            mMultiSelector.setSelectable(true);                         //此处代码用于删除multiselector在退出actionmode后留下来的阴影
            mMultiSelector.setSelectable(false);
        }
    };

    public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ViewHolder> {

        private List<Reminder> reminders;

        public ReminderAdapter(List<Reminder> reminders){
            this.reminders = reminders;
        }

        public class ViewHolder extends SwappingHolder
                implements View.OnClickListener, View.OnLongClickListener{

            ImageView img;
            TextView name;
            TextView left;
            ImageButton imgBtn;

            public ViewHolder(final View itemView){
                super(itemView, mMultiSelector);
                img = (ImageView) itemView.findViewById(R.id.reminder_item_image);
                name = (TextView) itemView.findViewById(R.id.reminder_item_name);
                left = (TextView) itemView.findViewById(R.id.reminder_item_left);
                imgBtn = (ImageButton) itemView.findViewById(R.id.image_btn);
                itemView.setLongClickable(true);
                itemView.setOnLongClickListener(this);
                itemView.setOnClickListener(this);

                imgBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = ViewHolder.this.getAdapterPosition();
                        Log.d("imgbtn", "onclick: " + position);
                        Reminder selectedReminder = reminders.get(position);
                        if (selectedReminder.getIsRemind() == 1){
                            imgBtn.setBackgroundResource(R.drawable.unalarm2);
                            selectedReminder.setIsRemind(0);
                            selectedReminder.save();
                            mAlarmReceiver.cancelAlarm(getApplicationContext(), selectedReminder.getId());
                        }else{
                            imgBtn.setBackgroundResource(R.drawable.alarm2);
                            selectedReminder.setIsRemind(1);
                            selectedReminder.save();
                            String deadlineSplit[] = selectedReminder.getDeadline().split("-");
                            int deadlineYear = Integer.valueOf(deadlineSplit[0]);
                            int deadlineMonth = Integer.valueOf(deadlineSplit[1]);
                            int deadlineDay = Integer.valueOf(deadlineSplit[2]);
                            Calendar calendar = Calendar.getInstance();
                            calendar.set(deadlineYear, --deadlineMonth, deadlineDay, 0, 0, 0);
                            mAlarmReceiver.setAlarm(getApplicationContext(),calendar, selectedReminder.getId());
                        }
                    }
                });
            }

            @Override
            public boolean onLongClick(View v) {
                if (!mMultiSelector.isSelectable()) {
                    mMultiSelector.setSelectable(true);
                    mMultiSelector.setSelected(ReminderAdapter.ViewHolder.this, true);
                    startSupportActionMode(mDeleteMode);
                    return true;
                }
                return false;
            }

            @Override
            public void onClick(View v) {
                if (mMultiSelector.isSelectable()){
                    if (!mMultiSelector.tapSelection(ReminderAdapter.ViewHolder.this)){
                        mMultiSelector.setSelected(ReminderAdapter.ViewHolder.this, true);
                    }
                }else{
                    int position = ViewHolder.this.getAdapterPosition();
                    Reminder reminder = reminderList.get(position);
                    Intent intent = new Intent(RemindThingsShowActivity.this, RemindThingDetailActivity.class);
                    intent.putExtra("id", reminder.getId());
                    startActivity(intent);
                }
            }
        }

        @Override
        public ReminderAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.reminder_item, parent, false);//最后一个false是什么意思
            ReminderAdapter.ViewHolder holder = new ReminderAdapter.ViewHolder(view);

            return holder;
        }

        @Override
        public void onBindViewHolder(ReminderAdapter.ViewHolder holder, int position) {
            Reminder reminder = reminders.get(position);
            holder.img.setImageResource(getImgResourceId(reminder.getCategory()));
            holder.name.setText(reminder.getName());
            holder.left.setText(String.valueOf(dateTimeCalculator.getDisplayLeftGuarantee(reminder.getLeftGuarantee())));
            Log.d("show reminder", "reminder" + reminder.getId() + "is remind: " + reminder.getIsRemind());
            holder.imgBtn.setBackgroundResource((reminder.getIsRemind() == 1)?R.drawable.alarm2:R.drawable.unalarm2);
        }

        @Override
        public int getItemCount() {
            return reminders.size();
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
    }
}
