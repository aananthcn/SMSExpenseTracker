package com.nonprofit.aananth.sms_expensetracker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ExpenseViewActivty extends AppCompatActivity {
    private static final String TAG = ExpCatViewActivity.class.getSimpleName();

    private Expense mExpense;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_view_activty);
        setTitle("Expense Details View");

        // get all arguments from the caller
        Intent intent = getIntent();
        mExpense = (Expense) intent.getSerializableExtra("expense");

        TextView senderTxt = (TextView) findViewById(R.id.sender_name);
        senderTxt.setText(mExpense.mSender.name);
        TextView smsTxt = (TextView) findViewById(R.id.sms_body);
        smsTxt.setText(mExpense.mSmsText);
    }
}
