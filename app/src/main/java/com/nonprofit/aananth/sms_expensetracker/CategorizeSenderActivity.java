package com.nonprofit.aananth.sms_expensetracker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class CategorizeSenderActivity extends AppCompatActivity {
    private static final String TAG = CategorizeSenderActivity.class.getSimpleName();

    private SmsSender mSmsSender;
    private List<ExpCategory> mExpCatList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.catize_sender_activity);

        // get all arguments from the caller
        Intent intent = getIntent();
        mSmsSender = (SmsSender) intent.getSerializableExtra("sender");
        setTitle("Categorize " + mSmsSender.name);

        ArrayList<String> list = new ArrayList<String>();
        final ExpenseDB expDb = new ExpenseDB(this);
        mExpCatList = expDb.GetExpCategoryList();

        // copy names to list and find the position of the current sender list
        int position = 0, i = 0;
        for (ExpCategory cat : mExpCatList) {
            list.add(cat.expCatName);
            if (mSmsSender.expCategory != null) {
                if (cat.expCatName.equals(mSmsSender.expCategory.expCatName)) {
                    position = i;
                }
            }
            i++;
        }

        final Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(CategorizeSenderActivity.this,
                android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(position);

        Button clickButton = (Button) findViewById(R.id.select);
        clickButton.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                ExpCategory cat = mExpCatList.get(spinner.getSelectedItemPosition());
                expDb.UpdateSmsSender(mSmsSender, cat);
                finish();
            }
        });
    }
}
