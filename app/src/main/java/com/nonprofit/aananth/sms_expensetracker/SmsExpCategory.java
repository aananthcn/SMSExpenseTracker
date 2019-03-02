package com.nonprofit.aananth.sms_expensetracker;

import java.util.List;

public class SmsExpCategory {
    long id;
    String expCatName;
    List<SmsSender> smsSenders;

    public SmsExpCategory(String category, int sid) {
        id = sid;
        expCatName = category;
        smsSenders = null;
    }

    public SmsExpCategory(String category, int sid, List<SmsSender> list) {
        id = sid;
        expCatName = category;
        smsSenders = list;
    }
}
