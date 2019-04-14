package com.nonprofit.aananth.sms_expensetracker;

import java.io.Serializable;

public class Expense implements Serializable {
    double mMoney;
    String mSmsText;
    String mAddrNum;
    String mDate;
    ExpCategory mCategory;
    SmsSender mSender;

    public Expense(double money, String text, ExpCategory cat, SmsSender sender, String date, String addr_num) {
        mMoney = money;
        mSmsText = text;
        mCategory = cat;
        mSender = sender;
        mDate = date;
        mAddrNum = addr_num;
    }
}
