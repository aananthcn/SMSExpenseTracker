package com.nonprofit.aananth.sms_expensetracker;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    final int REQUEST_CODE_ASK_PERMISSIONS = 123;
    final String MONEY_SEP = "spent rs.";

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private MainActivityRecyclerAdapter mSmsSenderRecyclerAdapter;
    private boolean mRcVwInitialized;
    private boolean mRcVwUpdateNeeded;

    private List<SmsSender> mSmsSenderList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Main Window");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        // main code
        if(ContextCompat.checkSelfPermission(getBaseContext(), "android.permission.READ_SMS")
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.READ_SMS"},
                    REQUEST_CODE_ASK_PERMISSIONS);
        }
        mSmsSenderList = getSmsSendersList();
        refreshSmsInbox();

        // prepare view
        renderExpensesRecycleView();
        mRcVwInitialized = true;
    }

    protected void onResume() {
        super.onResume();

        if (mRcVwInitialized & mRcVwUpdateNeeded) {
            mSmsSenderList.clear();
            mSmsSenderList.addAll(getSmsSendersList());
            mSmsSenderRecyclerAdapter.notifyDataSetChanged();
        }
    }


    private void refreshSmsInbox() {
        String date_pattern = "yyyy/MM/dd";
        DateFormat dateFormat = new SimpleDateFormat(date_pattern);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -2);
        Date sdate = cal.getTime();
        Date edate = new Date();
        Double money = 0.0;
        String filter = "date>=" + sdate.getTime() + " and date<=" + edate.getTime();
        Log.d(TAG, dateFormat.format(sdate) + " to " + dateFormat.format(edate));
        Log.d(TAG, filter);

        ContentResolver contentResolver = getContentResolver();
        Cursor smsInboxCursor = contentResolver.query(Uri.parse("content://sms/inbox"),
                null, filter, null, null);
        int indexBody = smsInboxCursor.getColumnIndex("body");
        int indexAddress = smsInboxCursor.getColumnIndex("address");
        if (indexBody < 0 || !smsInboxCursor.moveToFirst()) return;
        //arrayAdapter.clear();
        do {
            //String str = "SMS From: " + smsInboxCursor.getString(indexAddress) +
            //        "\n" + smsInboxCursor.getString(indexBody) + "\n";

            String body = smsInboxCursor.getString(indexBody);
            String sender = smsInboxCursor.getString(indexAddress);

            if (body.toLowerCase().contains(MONEY_SEP)) {
                money = parseMoneyFromMessage(body);

                // FIXME: Following 3 lines are done for testing purpose only!!
                ExpCategory expCategory = new ExpCategory(money.toString());
                SmsSender smsSndr = new SmsSender(sender, 0, expCategory);
                mSmsSenderList.add(smsSndr);

                for (SmsSender smsSender : mSmsSenderList) {
                    if (sender.toLowerCase().contains(smsSender.name.toLowerCase())) {
                        break;
                    }
                    Log.d(TAG, "Sender: " + sender + ", Money: Rs." + money);
                }
            }
            Log.d(TAG, "Sender: " + sender);
            //arrayAdapter.add(str);
        } while (smsInboxCursor.moveToNext());
    }

    private double parseMoneyFromMessage(String msg) {
        double money;

        msg = msg.toLowerCase();
        String[] parts = msg.split(MONEY_SEP); // "ALERT: You've spent Rs.729.00  on CREDIT Card ..."
        String money_str = parts[1].split(" ")[0]; // split the 2nd part based on space and take the first part
        money = Double.parseDouble(money_str);

        return money;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.exp_categories) {
            Intent intent = new Intent(this, ExpCatViewActivity.class);
            Log.d(TAG, "Switching to View Expense Categories");
            startActivity(intent);
        } else if (id == R.id.sms_senders) {
            Intent intent = new Intent(this, SmsSendersViewActivity.class);
            Log.d(TAG, "Switching to View SMS Senders");
            ExpCategory expCategory = new ExpCategory("*");
            intent.putExtra("expcat", expCategory);
            startActivity(intent);
        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // interface class
    public static abstract class ClickListener{
        public abstract void onClick(View view, int position);
        public abstract void onLongClick(View view, int position);
    }

    private List<SmsSender> getSmsSendersList() {
        ExpenseDB expDb = new ExpenseDB(this);
        return expDb.GetSenderList();
    }

    public void renderExpensesRecycleView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.expenses_list);
        if (mRecyclerView == null) {
            Log.d(TAG, "renderExpensesRecycleView: mRecylerView is null!!");
            return;
        }
        mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mSmsSenderRecyclerAdapter = new MainActivityRecyclerAdapter(mSmsSenderList);
        mRecyclerView.addItemDecoration(new DividerItemDecorator(this, LinearLayoutManager.VERTICAL));
        mRecyclerView.setAdapter(mSmsSenderRecyclerAdapter);

        mSmsSenderRecyclerAdapter.notifyDataSetChanged();

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
