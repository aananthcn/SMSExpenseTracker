package com.nonprofit.aananth.sms_expensetracker;

public class ExpenseFilter {
    String filter; //search text
    String sender_start;
    String sender_end;
    String money_start;
    String money_end;

    public ExpenseFilter(String filt, String ss, String se, String ms, String me) {
        filter = filt.toLowerCase();
        sender_start = ss;
        sender_end = se;
        money_start = ms;
        money_end = me;
    }
}
