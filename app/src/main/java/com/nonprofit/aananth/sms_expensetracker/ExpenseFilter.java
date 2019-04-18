package com.nonprofit.aananth.sms_expensetracker;

import java.io.Serializable;

public class ExpenseFilter implements Serializable {
    long id;
    String filter; //search text
    String sender_start;
    String sender_end;
    String money_start;
    String money_end;

    public ExpenseFilter() {
        filter = null;
        sender_start = null;
        sender_end = null;
        money_start = null;
        money_end = null;
        id = 0;
    }

    public ExpenseFilter(String filt, String ss, String se, String ms, String me) {
        filter = filt.toLowerCase();
        sender_start = ss;
        sender_end = se;
        money_start = ms;
        money_end = me;
        id = 0;
    }

    public void setId(long id_val) {
        id = id_val;
    }
}
