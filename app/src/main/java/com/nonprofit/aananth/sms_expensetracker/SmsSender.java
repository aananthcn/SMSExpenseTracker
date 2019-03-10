package com.nonprofit.aananth.sms_expensetracker;

import java.io.Serializable;

public class SmsSender implements Serializable {
    long id;
    String name;

    public SmsSender(String sname, int sid) {
        id = sid;
        name = sname;
    }
}
