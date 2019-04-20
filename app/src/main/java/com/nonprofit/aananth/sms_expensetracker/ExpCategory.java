package com.nonprofit.aananth.sms_expensetracker;

import java.io.Serializable;
import java.util.List;

public class ExpCategory implements Serializable {
    String uid;
    String expCatName;
    double money;

    public ExpCategory(String category) {
        uid = "";
        expCatName = category;
        money = 0.0;
    }

    public ExpCategory(String category, String sid) {
        uid = sid;
        expCatName = category;
        money = 0.0;
    }

    public ExpCategory(String category, String sid, List<SmsSender> list) {
        uid = sid;
        expCatName = category;
        money = 0.0;
    }
}
