package com.nonprofit.aananth.sms_expensetracker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class ExpCatViewActivity extends AppCompatActivity {
    private static final String TAG = ExpCatViewActivity.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private ExpCatRecyclerAdapter mExpCatRecyclerAdapter;
    private boolean mRcVwInitialized;
    private boolean mRcVwUpdateNeeded;

    private ArrayList<Expense> mExpenseList;
    private List<ExpCategory> mExpCategoryList;
    private ExpCategory mExpCategory;

    public static final int ADD_MODIFY_CAT = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exp_cat_activity_view);
        setTitle("Expense Categories");

        // get all arguments from the caller
        Intent intent = getIntent();
        mExpenseList = (ArrayList<Expense>) intent.getSerializableExtra("explist");

        // main code
        mExpCategoryList = getExpCategoryList();
        renderExpCatRecycleView(mExpCategoryList);
        mRcVwInitialized = true;
    }

    protected void onResume() {
        super.onResume();

        if (mRcVwInitialized & mRcVwUpdateNeeded) {
            mExpCategoryList.clear();
            mExpCategoryList.addAll(getExpCategoryList());
            mExpCatRecyclerAdapter.notifyDataSetChanged();
            mRcVwUpdateNeeded = false;
        }
    }

    public void renderExpCatRecycleView(final List<ExpCategory> list) {
        //update_mode(Mode.VIEW_TREAT);
        mRecyclerView = (RecyclerView) findViewById(R.id.exp_cat_recycler_view);
        if (mRecyclerView == null) {
            Log.d(TAG, "renderExpCatRecycleView: mRecylerView is null!!");
            return;
        }
        mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mExpCatRecyclerAdapter = new ExpCatRecyclerAdapter(list);
        mRecyclerView.addItemDecoration(new DividerItemDecorator(this, LinearLayoutManager.VERTICAL));
        mRecyclerView.setAdapter(mExpCatRecyclerAdapter);

        mExpCatRecyclerAdapter.notifyDataSetChanged();

        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(this,
                mRecyclerView, new MainActivity.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                Log.d(TAG, "onClick()");
                mExpCategory = list.get(position);

                Intent intent = new Intent(ExpCatViewActivity.this, SmsSendersViewActivity.class);
                Log.d(TAG, "Switching to View SMS Senders view");
                intent.putExtra("expcat", mExpCategory);
                intent.putExtra("explist", mExpenseList);
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {
                // edit expense category
                mExpCategory = list.get(position);

                Intent intent = new Intent(ExpCatViewActivity.this, ExpCatAddModActivity.class);
                Log.d(TAG, "Switching to Edit Expense Category");
                intent.putExtra(EXTRA_MESSAGE, "modify");
                intent.putExtra("expcat", mExpCategory);
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

    private List<ExpCategory> getExpCategoryList() {
        ExpenseDB expDb = new ExpenseDB(this);
        List<ExpCategory> expcatlist = expDb.GetExpCategoryList();
        List<SmsSender> senders = expDb.GetSenderList();

        // parse expense list and add money to sender.money
        for (Expense exp : mExpenseList) {
            for (SmsSender sender : senders) {
                boolean sender_search_complete = false;
                try {
                    if (exp.mSender.name.toLowerCase().contains(sender.name.toLowerCase())) {
                        for (ExpCategory cat : expcatlist) {
                            if (cat.expCatName.toLowerCase().equals(sender.expCategory.expCatName.toLowerCase())) {
                                cat.money += exp.mMoney;
                                sender_search_complete = true;
                                break;
                            }
                        }
                    }
                }
                catch (Exception e)
                {
                    Log.d(TAG, e.getMessage());
                }
                if (sender_search_complete) {
                    break;
                }
            }
        }
        return expcatlist;
    }
}
