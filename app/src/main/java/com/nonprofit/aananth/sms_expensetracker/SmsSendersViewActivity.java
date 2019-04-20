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

public class SmsSendersViewActivity extends AppCompatActivity {
    private static final String TAG = SmsSendersViewActivity.class.getSimpleName();

    private ExpCategory mExpCat;

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private SmsSenderRecyclerAdapter mSmsSenderRecyclerAdapter;
    private boolean mRcVwInitialized;
    private boolean mRcVwUpdateNeeded;

    private ArrayList<Expense> mExpenseList;
    private List<SmsSender> mSmsSenderList;
    private SmsSender mSmsSender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sms_senders_list_activity);

        // get all arguments from the caller
        Intent intent = getIntent();
        mExpCat = (ExpCategory) intent.getSerializableExtra("expcat");
        mExpenseList = (ArrayList<Expense>) intent.getSerializableExtra("explist");
        setTitle("SMS Senders");

        // main code
        mSmsSenderList = getSmsSendersList();
        renderSmsSenderRecycleView(mSmsSenderList);
        mRcVwInitialized = true;
    }

    protected void onResume() {
        super.onResume();

        if (mRcVwInitialized & mRcVwUpdateNeeded) {
            mSmsSenderList.clear();
            mSmsSenderList.addAll(getSmsSendersList());
            mSmsSenderRecyclerAdapter.notifyDataSetChanged();
            mRcVwUpdateNeeded = false;
        }
    }

    private List<SmsSender> getSmsSendersList() {
        ExpenseDB expDb = new ExpenseDB(this);
        List<SmsSender> senders = expDb.GetSenderList();

        // parse expense list and add money to sender.money
        for (Expense exp : mExpenseList) {
            for (SmsSender sender : senders) {
                try {
                    if (exp.mSender.name.toLowerCase().contains(sender.name.toLowerCase())) {
                        sender.money += exp.mMoney;
                        break;
                    }
                }
                catch (Exception e)
                {
                    Log.d(TAG, e.getMessage());
                }
            }
        }

        return senders;
    }

    public void renderSmsSenderRecycleView(final List<SmsSender> list) {
        mRecyclerView = (RecyclerView) findViewById(R.id.exp_cat_recycler_view);
        if (mRecyclerView == null) {
            Log.d(TAG, "renderExpCatRecycleView: mRecylerView is null!!");
            return;
        }
        mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mSmsSenderRecyclerAdapter = new SmsSenderRecyclerAdapter(list);
        mRecyclerView.addItemDecoration(new DividerItemDecorator(this, LinearLayoutManager.VERTICAL));
        mRecyclerView.setAdapter(mSmsSenderRecyclerAdapter);

        mSmsSenderRecyclerAdapter.notifyDataSetChanged();

        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(this,
                mRecyclerView, new MainActivity.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                Log.d(TAG, "onClick()");
                mSmsSender = list.get(position);
                // handle this event
                Intent intent = new Intent(SmsSendersViewActivity.this, CategorizeSenderActivity.class);
                intent.putExtra("sender", mSmsSender);
                intent.putExtra(EXTRA_MESSAGE, "categorize");
                startActivity(intent);
                mRcVwUpdateNeeded = true;
            }

            @Override
            public void onLongClick(View view, int position) {
                Log.d(TAG, "onLongClick()");
                mSmsSender = list.get(position);
                // handle this event
                Intent intent = new Intent(SmsSendersViewActivity.this, SmsSenderAddModActivity.class);
                intent.putExtra("sender", mSmsSender);
                intent.putExtra("expcat", mExpCat);
                intent.putExtra(EXTRA_MESSAGE, "modify");
                startActivity(intent);
                mRcVwUpdateNeeded = true;
            }
        }));
    }

    public void AddSmsSender(View view) {
        Intent intent = new Intent(SmsSendersViewActivity.this, SmsSenderAddModActivity.class);
        intent.putExtra("sender", mSmsSender);
        intent.putExtra("expcat", mExpCat);
        intent.putExtra(EXTRA_MESSAGE, "new");
        startActivity(intent);
        mRcVwUpdateNeeded = true;
    }

}
