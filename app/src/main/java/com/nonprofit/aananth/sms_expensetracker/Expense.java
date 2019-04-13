package com.nonprofit.aananth.sms_expensetracker;

import java.io.Serializable;

public class Expense implements Serializable {
    double mMoney;
    String mSmsText;
    ExpCategory mCategory;
    SmsSender mSender;

    public Expense(double money, String text, ExpCategory cat, SmsSender sender) {
        mMoney = money;
        mSmsText = text;
        mCategory = cat;
        mSender = sender;
    }
}
