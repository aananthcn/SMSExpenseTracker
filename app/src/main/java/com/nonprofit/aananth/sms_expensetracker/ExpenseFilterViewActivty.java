package com.nonprofit.aananth.sms_expensetracker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.util.List;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class ExpenseFilterViewActivty extends AppCompatActivity {
    private static final String TAG = ExpenseFilterViewActivty.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private ExpenseFilterRecyclerAdapter mExpFiltRecyclerAdapter;
    private boolean mRcVwInitialized;
    private boolean mRcVwUpdateNeeded;

    private List<ExpenseFilter> mExpFilterList;
    private ExpenseFilter mExpFilter;


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

    protected void onResume() {
        super.onResume();

        if (mRcVwInitialized & mRcVwUpdateNeeded) {
            mExpFilterList.clear();
            mExpFilterList.addAll(getExpenseFilterList());
            mExpFiltRecyclerAdapter.notifyDataSetChanged();
            mRcVwUpdateNeeded = false;
        }
    }

    public void AddExpenseFilter(View view) {
        Intent intent = new Intent(ExpenseFilterViewActivty.this, ExpenseFilterAddModActivity.class);
        intent.putExtra(EXTRA_MESSAGE, "new");
        Log.d(TAG, "Switching to Expense Filter AddMod Activity view");
        startActivity(intent);
        mRcVwUpdateNeeded = true;
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

                mExpFilter = mExpFilterList.get(position);

                Intent intent = new Intent(ExpenseFilterViewActivty.this, ExpenseFilterAddModActivity.class);
                Log.d(TAG, "Switching to Expense Filter AddMod Activity view");
                intent.putExtra(EXTRA_MESSAGE, "modify");
                intent.putExtra("expfilt", mExpFilter);
                startActivity(intent);
                mRcVwUpdateNeeded = true;
            }
        }));
    }

}
