package com.nonprofit.aananth.sms_expensetracker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class ExpenseFilterAddModActivity extends AppCompatActivity {
    private static final String TAG = ExpenseFilterAddModActivity.class.getSimpleName();

    private ExpenseFilter mExpFilt;
    private boolean mIsNew;

    private EditText mFilter, mSenderStart, mSenderEnd, mMoneyStart, mMoneyEnd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exp_filt_add_mod_activity);

        // get all arguments from the caller
        Intent intent = getIntent();
        String message = intent.getStringExtra(EXTRA_MESSAGE);
        mExpFilt = (ExpenseFilter) intent.getSerializableExtra("expfilt");
        if (message.equals("new")) {
            setTitle("Add New Expense Filter");
            mIsNew = true;
            mExpFilt = new ExpenseFilter();
        }
        else {
            setTitle("Edit Expense Filter");
            mExpFilt = (ExpenseFilter) intent.getSerializableExtra("expfilt");
            mIsNew = false;
        }

        // get all references to resources
        mFilter = (EditText) findViewById(R.id.filter);
        mSenderStart = (EditText) findViewById(R.id.sender_start);
        mSenderEnd = (EditText) findViewById(R.id.sender_end);
        mMoneyStart = (EditText) findViewById(R.id.money_start);
        mMoneyEnd = (EditText) findViewById(R.id.money_end);

        if (!mIsNew) {
            mFilter.setText(mExpFilt.filter);
            mSenderStart.setText(mExpFilt.sender_start);
            mSenderEnd.setText(mExpFilt.sender_end);
            mMoneyStart.setText(mExpFilt.money_start);
            mMoneyEnd.setText(mExpFilt.money_end);
        }
    }

    public void SaveExpenseFilter(View view) {
        mExpFilt.filter = mFilter.getText().toString();
        mExpFilt.sender_start = mSenderStart.getText().toString();
        mExpFilt.sender_end = mSenderEnd.getText().toString();
        mExpFilt.money_start = mMoneyStart.getText().toString();
        mExpFilt.money_end = mMoneyEnd.getText().toString();

        ExpenseDB expDB = new ExpenseDB(this);
        if (mIsNew) {
            expDB.AddExpenseFilter(mExpFilt);
        }
        else {
            expDB.UpdateExpenseFilter(mExpFilt);
        }
        finish();
    }

    public void DeleteExpenseFilter(View view) {
        if (!mIsNew) {
            ExpenseDB expDB = new ExpenseDB(this);
            expDB.DeleteExpenseFilter(mExpFilt);
        }
        finish();
    }
}
