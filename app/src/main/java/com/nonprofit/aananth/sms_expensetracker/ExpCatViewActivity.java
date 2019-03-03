package com.nonprofit.aananth.sms_expensetracker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class ExpCatViewActivity extends AppCompatActivity {
    private static final String TAG = ExpCatViewActivity.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private ExpCatRecyclerAdapter expCatRecyclerAdapter;
    private Boolean mRcVwInitialized = false;
    private Boolean mRcVwUpdateNeeded = false;

    private List<SmsExpCategory> expCategoryList;
    private SmsExpCategory expCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exp_cat_view);

        // test code
        SmsExpCategory cat1 = new SmsExpCategory("Grocery", 1);
        SmsExpCategory cat2 = new SmsExpCategory("Fuel", 2);
        expCategoryList = new ArrayList<>();
        expCategoryList.add(cat1);
        expCategoryList.add(cat2);
        renderExpCatRecycleView();
    }

    public void renderExpCatRecycleView() {
        //update_mode(Mode.VIEW_TREAT);
        mRecyclerView = (RecyclerView) findViewById(R.id.exp_cat_recycler_view);
        if (mRecyclerView == null) {
            Log.d(TAG, "renderExpCatRecycleView: mRecylerView is null!!");
            return;
        }
        mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        expCatRecyclerAdapter = new ExpCatRecyclerAdapter(expCategoryList);
        mRecyclerView.addItemDecoration(new DividerItemDecorator(this, LinearLayoutManager.VERTICAL));
        mRecyclerView.setAdapter(expCatRecyclerAdapter);

        expCatRecyclerAdapter.notifyDataSetChanged();

        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(this,
                mRecyclerView, new MainActivity.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                Log.d(TAG, "onClick()");
                // share treatment
                expCategory = expCategoryList.get(position);
                registerForContextMenu(view);
                openContextMenu(view);
                view.showContextMenu();
                unregisterForContextMenu(view);
            }

            @Override
            public void onLongClick(View view, int position) {
                // edit treatment
                expCategory = expCategoryList.get(position);
                // EditTreatmentRecord();
            }
        }));
    }
}
