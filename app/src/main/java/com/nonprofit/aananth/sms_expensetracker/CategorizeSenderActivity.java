package com.nonprofit.aananth.sms_expensetracker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;

public class CategorizeSenderActivity extends AppCompatActivity {
    private static final String TAG = CategorizeSenderActivity.class.getSimpleName();

    private SmsSender mSmsSender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.catize_sender_activity);

        // get all arguments from the caller
        Intent intent = getIntent();
        mSmsSender = (SmsSender) intent.getSerializableExtra("sender");
        setTitle("Categorize " + mSmsSender.name);

        ArrayList<String> list = new ArrayList<String>();
        list.add("Baba");
        list.add("Babu");

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(CategorizeSenderActivity.this,
                android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }
}
