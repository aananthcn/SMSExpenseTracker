package com.nonprofit.aananth.sms_expensetracker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.util.List;

public class ExpenseFilterViewActivty extends AppCompatActivity {
    private static final String TAG = ExpenseFilterViewActivty.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private ExpenseFilterRecyclerAdapter mExpFiltRecyclerAdapter;
    private boolean mRcVwInitialized;
    private boolean mRcVwUpdateNeeded;

    private List<ExpenseFilter> mExpFilterList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exp_filt_activity_view);
        setTitle("Expense Filters");

        // main code
        mExpFilterList = getExpenseFilterList();
        renderExpFilterRecycleView(mExpFilterList);
        mRcVwInitialized = true;
    }

    public void AddExpenseFilter(View view) {
    }


    private List<ExpenseFilter> getExpenseFilterList() {
        List<ExpenseFilter> filterList;
        ExpenseDB expDb = new ExpenseDB(this);

        filterList = expDb.GetExpenseFilterList();

        return filterList;
    }

    public void renderExpFilterRecycleView(List<ExpenseFilter> list) {
        mRecyclerView = (RecyclerView) findViewById(R.id.exp_filter_list);
        if (mRecyclerView == null) {
            Log.d(TAG, "renderExpFilterRecycleView: mRecylerView is null!!");
            return;
        }
        mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mExpFiltRecyclerAdapter = new ExpenseFilterRecyclerAdapter(list);
        mRecyclerView.addItemDecoration(new DividerItemDecorator(this, LinearLayoutManager.VERTICAL));
        mRecyclerView.setAdapter(mExpFiltRecyclerAdapter);

        mExpFiltRecyclerAdapter.notifyDataSetChanged();

        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(this,
                mRecyclerView, new MainActivity.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                Log.d(TAG, "onClick()");
                // handle this event
            }

            @Override
            public void onLongClick(View view, int position) {
                Log.d(TAG, "onLongClick()");
                // handle this event
            }
        }));
    }

}
