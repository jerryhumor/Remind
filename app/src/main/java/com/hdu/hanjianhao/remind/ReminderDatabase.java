package com.hdu.hanjianhao.remind;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by hanjianhao on 17/4/8.
 */

public class ReminderDatabase {

    private List<Reminder> reminders;
    private DateTimeCalculator calculator = new DateTimeCalculator();

    ReminderDatabase(){
        freshDatabase();
    }

    public List<Reminder> getReminders(){
        reminders = DataSupport.where("fresh >= 0").find(Reminder.class);
        return reminders;
    }

    //请谨慎使用
    public void freshDatabase(){
        List<Reminder> reminderList = DataSupport.where("fresh > 0").find(Reminder.class);

        for(Reminder reminder:reminderList){
            reminder.setLeftGuarantee(calculator.getLeftGuarantee(reminder));
            reminder.setFresh(calculator.getFresh(reminder));
            reminder.save();
        }

        reminders = DataSupport.where("fresh > 0").find(Reminder.class);
    }

    public void removeReminder(int id){
        DataSupport.delete(Reminder.class, id);
    }
}
