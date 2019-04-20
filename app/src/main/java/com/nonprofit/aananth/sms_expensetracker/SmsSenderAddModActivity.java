package com.nonprofit.aananth.sms_expensetracker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class SmsSenderAddModActivity extends AppCompatActivity {

    private SmsSender mSmsSender;
    private ExpCategory mExpCat;
    private boolean mIsNew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sms_sender_add_mod_activity);

        // get all arguments from the caller
        Intent intent = getIntent();
        String message = intent.getStringExtra(EXTRA_MESSAGE);
        mExpCat = (ExpCategory) intent.getSerializableExtra("expcat");
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

        //set up button handlers
        Button saveBtn = (Button) findViewById(R.id.sender_save_btn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSmsSender(mIsNew);
            }
        });
        Button deleBtn = (Button) findViewById(R.id.sender_delete_btn);
        deleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteSmsSender();
            }
        });
        EditText senderTxt = (EditText) findViewById(R.id.sender_text);
        if (!mIsNew) {
            senderTxt.setText(mSmsSender.name);
        }
        senderTxt.setOnKeyListener(new View.OnKeyListener()
        {
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if (event.getAction() == KeyEvent.ACTION_DOWN)
                {
                    switch (keyCode)
                    {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            saveSmsSender(mIsNew);
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });
    }

    private void saveSmsSender(boolean isNew) {
        ExpenseDB expenseDB = new ExpenseDB(this);
        EditText senderTxt = (EditText) findViewById(R.id.sender_text);
        mSmsSender.name = senderTxt.getText().toString();

        if (isNew) {
            expenseDB.AddSmsSender(mSmsSender, mExpCat);
        }
        else {
            expenseDB.UpdateSmsSender(mSmsSender, mExpCat);
        }

        finish();
    }

    private void deleteSmsSender() {
        ExpenseDB expenseDB = new ExpenseDB(this);
        expenseDB.DeleteSmsSender(mSmsSender, mExpCat);
        finish();
    }
}
