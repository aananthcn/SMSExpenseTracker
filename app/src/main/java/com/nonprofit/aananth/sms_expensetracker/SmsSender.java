package com.nonprofit.aananth.sms_expensetracker;

import java.io.Serializable;

public class SmsSender implements Serializable {
    long id;
    String name;
    ExpCategory expCategory;
    double money;

    public SmsSender(String sname) {
        name = sname;
        id = -1;
        expCategory = null;
        money = 0.0;
    }

    public SmsSender(String sname, int sid) {
        id = sid;
        name = sname;
        expCategory = null;
        money = 0.0;
    }

    public SmsSender(String sname, int sid, ExpCategory expCat) {
        id = sid;
        name = sname;
        expCategory = expCat;
        money = 0.0;
    }
}
