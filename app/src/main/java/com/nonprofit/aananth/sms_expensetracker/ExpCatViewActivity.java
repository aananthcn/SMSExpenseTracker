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

public class ExpCatViewActivity extends AppCompatActivity {
    private static final String TAG = ExpCatViewActivity.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private ExpCatRecyclerAdapter mExpCatRecyclerAdapter;
    private boolean mRcVwInitialized;
    private boolean mRcVwUpdateNeeded;

    private List<ExpCategory> mExpCategoryList;
    private ExpCategory mExpCategory;

    public static final int ADD_MODIFY_CAT = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exp_cat_activity_view);
        setTitle("com.nonprofit.aananth.sms_expensetracker.Expense Categories");

        // main code
        renderExpCatRecycleView();
        mRcVwInitialized = true;
    }

    protected void onResume() {
        super.onResume();

        if (mRcVwInitialized & mRcVwUpdateNeeded) {
            mExpCategoryList.clear();
            mExpCategoryList.addAll(getExpCategoryList());
            mExpCatRecyclerAdapter.notifyDataSetChanged();
        }
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
        mExpCategoryList = getExpCategoryList();
        mExpCatRecyclerAdapter = new ExpCatRecyclerAdapter(mExpCategoryList);
        mRecyclerView.addItemDecoration(new DividerItemDecorator(this, LinearLayoutManager.VERTICAL));
        mRecyclerView.setAdapter(mExpCatRecyclerAdapter);

        mExpCatRecyclerAdapter.notifyDataSetChanged();

        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(this,
                mRecyclerView, new MainActivity.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                Log.d(TAG, "onClick()");
                mExpCategory = mExpCategoryList.get(position);

                Intent intent = new Intent(ExpCatViewActivity.this, SmsSendersViewActivity.class);
                Log.d(TAG, "Switching to View SMS Senders view");
                intent.putExtra("expcat", mExpCategory);
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {
                // edit treatment
                mExpCategory = mExpCategoryList.get(position);

                Intent intent = new Intent(ExpCatViewActivity.this, ExpCatAddModActivity.class);
                Log.d(TAG, "Switching to View Categories");
                intent.putExtra(EXTRA_MESSAGE, "modify");
                intent.putExtra("expcat", mExpCategory);
                //startActivityForResult(intent, ADD_MODIFY_CAT);
                startActivity(intent);
                mRcVwUpdateNeeded = true;
            }
        }));
    }

    public void AddExpenseCategory(View view) {
        Intent intent = new Intent(ExpCatViewActivity.this, ExpCatAddModActivity.class);
        intent.putExtra(EXTRA_MESSAGE, "new");
        intent.putExtra("expcat", mExpCategory);
        startActivity(intent);
        mRcVwUpdateNeeded = true;
    }

    /*@Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);

        switch (requestCode) {
            case ADD_MODIFY_CAT:
                if (resultCode == Activity.RESULT_OK) {
                    Log.d(TAG, "onActivityResult()::ADD_MODIFY_CAT");
                    String strLoginResult = resultData.getStringExtra("update_needed");

                    if (strLoginResult.equalsIgnoreCase("yes")) {
                        refreshExpCatList();
                        renderExpCatRecycleView();
                    }
                }
                break;
        }
    }*/

    private List<ExpCategory> getExpCategoryList() {
        ExpenseDB expDb = new ExpenseDB(this);
        return expDb.GetExpCategoryList();
    }
}
