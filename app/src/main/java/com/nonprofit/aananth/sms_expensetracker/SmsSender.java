package com.nonprofit.aananth.sms_expensetracker;

public class SmsSender {
    long id;
    String name;

    public SmsSender(String sname, int sid) {
        id = sid;
        name = sname;
    }
}
