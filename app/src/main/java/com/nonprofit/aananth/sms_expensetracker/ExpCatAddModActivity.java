package com.nonprofit.aananth.sms_expensetracker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class ExpCatAddModActivity extends AppCompatActivity {
    private static final String TAG = ExpCatAddModActivity.class.getSimpleName();

    private ExpCategory mExpCat;
    private boolean mIsNew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exp_cat_add_mod_activity);

        // get all arguments from the caller
        Intent intent = getIntent();
        String message = intent.getStringExtra(EXTRA_MESSAGE);
        if (message.equals("new")) {
            setTitle("Add Expense Category");
            mIsNew = true;
            mExpCat = new ExpCategory("");
        }
        else {
            setTitle("Edit Expense Category");
            mIsNew = false;
            mExpCat = (ExpCategory) intent.getSerializableExtra("expcat");
        }

        //set up button handlers
        Button saveBtn = (Button) findViewById(R.id.expcat_save_btn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveExpenseCategory(mIsNew);
            }
        });
        Button deleBtn = (Button) findViewById(R.id.expcat_delete_btn);
        deleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteExpenseCategory();
            }
        });
        EditText expCatTxt = (EditText) findViewById(R.id.expcat_text);
        if (!mIsNew) {
            expCatTxt.setText(mExpCat.expCatName);
        }
        expCatTxt.setOnKeyListener(new View.OnKeyListener()
        {
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if (event.getAction() == KeyEvent.ACTION_DOWN)
                {
                    switch (keyCode)
                    {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            saveExpenseCategory(mIsNew);
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });

    }

    private void saveExpenseCategory(boolean is_new) {
        EditText expcat_txt;
        expcat_txt = (EditText) findViewById(R.id.expcat_text);

        if (expcat_txt.getText().toString().length() == 0) {
            Log.d(TAG, "Empty string in Expense Catogory");
            return;
        }

        mExpCat.expCatName = expcat_txt.getText().toString();
        ExpenseDB expenseDB = new ExpenseDB(this);
        if (is_new) {
            expenseDB.AddExpenseCategory(mExpCat);
        }
        else  {
            expenseDB.UpdateExpenseCategory(mExpCat);
        }

        myFinish("yes");
    }

    private void deleteExpenseCategory() {
        ExpenseDB expenseDB = new ExpenseDB(this);
        expenseDB.DeleteExpCategory(mExpCat);
        myFinish("yes");
    }

    private void myFinish(String update_needed) {
/*        Intent resultIntent = new Intent();
        resultIntent.putExtra("update_needed", update_needed);
        setResult(Activity.RESULT_OK, resultIntent);*/
        finish();
    }
}
