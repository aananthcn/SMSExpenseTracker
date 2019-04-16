package com.nonprofit.aananth.sms_expensetracker;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    final int REQUEST_CODE_ASK_PERMISSIONS = 123;
    //final String MONEY_SEP = "spent rs.";

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private MainActivityRecyclerAdapter mSmsSenderRecyclerAdapter;
    private boolean mRcVwInitialized;
    private boolean mRcVwUpdateNeeded;

    private List<Expense> mExpenseList;
    private List<String> mExpFilterList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Main Window");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mExpenseList = new ArrayList<Expense>();

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

        // prepare filter for scan SMS
        mExpFilterList = getExpenseFilterList();
        scanSmsInboxForExpense();

        // prepare view
        renderExpensesRecycleView();
        mRcVwInitialized = true;
    }


    private List<String> getExpenseFilterList() {
        List<String> filterList;
        ExpenseDB expDb = new ExpenseDB(this);

        filterList = expDb.GetExpenseFilterList();
        if (filterList == null) {
            expDb.AddExpenseFilter("spent rs.");
            expDb.AddExpenseFilter("debited with INR");
            expDb.AddExpenseFilter("debited from");
            filterList = expDb.GetExpenseFilterList();
        }

        return filterList;
    }


    protected void onResume() {
        super.onResume();

        if (mRcVwInitialized & mRcVwUpdateNeeded) {
            mSmsSenderRecyclerAdapter.notifyDataSetChanged();
        }
    }


    private void scanSmsInboxForExpense() {
        String date_pattern = "yyyy-MM-dd";
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
        Cursor cursor = contentResolver.query(Uri.parse("content://sms/inbox"),
                null, filter, null, null);
        //String type = cursor.getString(cursor.getColumnIndex(Telephony.Sms.TYPE));
        int indexBody = cursor.getColumnIndex(Telephony.Sms.BODY);
        int indexAddress = cursor.getColumnIndex("address");
        int indexDate = cursor.getColumnIndex(Telephony.Sms.DATE);

        if (indexBody < 0 || !cursor.moveToFirst()) return;
        do {

            String body = cursor.getString(indexBody);
            String addr = cursor.getString(indexAddress);
            Date smsDate = new Date(Long.valueOf(cursor.getString(indexDate)));
            String date =  dateFormat.format(smsDate);

            for (String exp_filter: mExpFilterList) {
                Log.d(TAG, "filter: " + exp_filter);
                if (body.toLowerCase().contains(exp_filter)) {
                    money = parseMoneyFromMessage(body);
                    String sender = parseSenderFromMessage(body, exp_filter);
                    SmsSender smsSender = new SmsSender(sender);
                    Expense expense = new Expense(money, body, null, smsSender, date, addr);
                    mExpenseList.add(expense);
                    break;
                }
            }
            Log.d(TAG, "Sender: " + addr);
        } while (cursor.moveToNext());
    }

    private double parseMoneyFromMessage(String msg) {
        double money;
        String separator;

        if (msg.toLowerCase().contains("inr")) {
            separator = "inr\\s*";
        }
        else {
            separator = "rs.";
        }

        msg = msg.toLowerCase();
        String[] parts = msg.split(separator); // "ALERT: You've spent Rs.729.00  on CREDIT Card ..."
        String money_str = parts[1].split(" ")[0]; // split the 2nd part based on space and take the first part
        money = Double.parseDouble(money_str.replaceAll(",", ""));

        return money;
    }

    private String parseSenderFromMessage(String msg, String filter) {
        String sender;

        try {
            String[] parts = msg.split("\\s*(at)\\s*");
            sender = parts[1].split("\\s*(on)\\s*")[0];
        }
        catch (Exception e) {
            sender = "";
        }

        return sender;
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
            Log.d(TAG, "Switching to View com.nonprofit.aananth.sms_expensetracker.Expense Categories");
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

    public void renderExpensesRecycleView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.expenses_list);
        if (mRecyclerView == null) {
            Log.d(TAG, "renderExpensesRecycleView: mRecylerView is null!!");
            return;
        }
        mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mSmsSenderRecyclerAdapter = new MainActivityRecyclerAdapter(mExpenseList);
        mRecyclerView.addItemDecoration(new DividerItemDecorator(this, LinearLayoutManager.VERTICAL));
        mRecyclerView.setAdapter(mSmsSenderRecyclerAdapter);

        mSmsSenderRecyclerAdapter.notifyDataSetChanged();

        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(this,
                mRecyclerView, new MainActivity.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                Log.d(TAG, "onClick()");
                // handle this event
                Intent intent = new Intent(MainActivity.this, ExpenseViewActivty.class);
                Log.d(TAG, "Switching to View SMS Details");
                intent.putExtra("expense", mExpenseList.get(position));
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {
                Log.d(TAG, "onLongClick()");
                // handle this event
            }
        }));
    }

}
