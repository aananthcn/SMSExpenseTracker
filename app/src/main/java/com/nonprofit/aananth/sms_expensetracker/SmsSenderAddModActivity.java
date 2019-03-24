package com.nonprofit.aananth.sms_expensetracker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class SmsSenderAddModActivity extends AppCompatActivity {

    private SmsSender mSmsSender;
    private boolean mIsNew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sms_sender_add_mod_activity);

        // get all arguments from the caller
        Intent intent = getIntent();
        String message = intent.getStringExtra(EXTRA_MESSAGE);
        if (message.equals("new")) {
            setTitle("Add New Sender");
            mIsNew = true;
            mSmsSender = new SmsSender("");
        }
        else {
            setTitle("Edit Expense Category");
            mSmsSender = (SmsSender) intent.getSerializableExtra("sender");
            mIsNew = false;
        }
    }
}
