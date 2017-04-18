package com.hdu.hanjianhao.remind;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;

import java.util.Date;

/**
 * Created by hanjianhao on 17/4/8.
 */

public class Reminder extends DataSupport{
    private int id;

    @Column(nullable = false, defaultValue = "default")
    private String name;

    @Column(nullable = false, defaultValue = "0")
    private int category;


    private String createDate;

    @Column(nullable = false, defaultValue = "1995-9-20")
    private String manufactureDate;                                     //生产日期

    @Column(nullable = false, defaultValue = "0-0-0")
    private String guaranteePeriod;                                     //此字符串为自定义 格式为 y-M-d 代表年-月-日 后期用split分隔

    @Column(nullable = false, defaultValue = "1995-9-20")
    private String deadline;

    @Column(nullable = false, defaultValue = "0")
    private long leftGuarantee;                                         //表示剩余保质期天数

    @Column(nullable = false, defaultValue = "1")
    private float fresh;

    @Column(nullable = false, defaultValue = "1")
    private int isRemind;

    @Column(nullable = false, defaultValue = "15")                      //默认提前17小时 即前一天早上9点提醒
    private int advanceHour;



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getManufactureDate() {
        return manufactureDate;
    }

    public void setManufactureDate(String manufactureDate) {
        this.manufactureDate = manufactureDate;
    }

    public String getGuaranteePeriod() {
        return guaranteePeriod;
    }

    public void setGuaranteePeriod(String guaranteePeriod) {
        this.guaranteePeriod = guaranteePeriod;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public long getLeftGuarantee() {
        return leftGuarantee;
    }

    public void setLeftGuarantee(long leftGuarantee) {
        this.leftGuarantee = leftGuarantee;
    }

    public float getFresh() {
        return fresh;
    }

    public void setFresh(float fresh) {
        this.fresh = fresh;
    }

    public int getIsRemind() {
        return isRemind;
    }

    public void setIsRemind(int isRemind) {
        this.isRemind = isRemind;
    }

    public int getAdvanceHour() {
        return advanceHour;
    }

    public void setAdvanceHour(int advanceHour) {
        this.advanceHour = advanceHour;
    }
}
