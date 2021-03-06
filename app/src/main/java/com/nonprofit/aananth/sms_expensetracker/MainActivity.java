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
import android.widget.TextView;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    final int REQUEST_CODE_ASK_PERMISSIONS = 123;
    final int DEFAULT_MONTH_SPAN = 2;

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private MainActivityRecyclerAdapter mMainRecyclerAdapter;
    private boolean mRcVwInitialized;
    private boolean mRcVwUpdateNeeded;

    private ArrayList<Expense> mExpenseList;
    private List<ExpenseFilter> mExpFilterList;
    private Double mTotalMoney = 0.0;
    private Date mSdate, mMdate, mEdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Last " + DEFAULT_MONTH_SPAN + " month's Expenses");
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
        TextView footer = (TextView) findViewById(R.id.footer);
        footer.setText(getFooterText());
        mRcVwInitialized = true;
    }

    protected void onResume() {
        super.onResume();

        if (mRcVwInitialized & mRcVwUpdateNeeded) {
            mExpFilterList = getExpenseFilterList();

            // prepare view
            mExpenseList.clear();
            scanSmsInboxForExpense(); // fill-up the mExpFilterList
            mMainRecyclerAdapter.notifyDataSetChanged();
            mRcVwUpdateNeeded = false;

            TextView footer = (TextView) findViewById(R.id.footer);
            footer.setText(getFooterText());
        }
    }

    private String getFooterText() {
        String date_pattern = "MM/dd";
        DateFormat dateFormat = new SimpleDateFormat(date_pattern);
        String sdate = dateFormat.format(mMdate);
        String edate = dateFormat.format(mEdate);
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("ta", "IN"));
        String money = formatter.format(mTotalMoney);
        String footer = money + " spent between " + sdate + " & " + edate;

        return footer;
    }

    private List<ExpenseFilter> getExpenseFilterList() {
        List<ExpenseFilter> filterList;
        ExpenseDB expDb = new ExpenseDB(this);

        filterList = expDb.GetExpenseFilterList();
        if (filterList == null) {
            ExpenseFilter ef1 = new ExpenseFilter("(?s).*\\bspent rs.\\b.*", " at ", " on ",
                    "Rs.", " ");
            expDb.AddExpenseFilter(ef1);
            ExpenseFilter ef2 = new ExpenseFilter("(?s).*\\bdebited with INR.*", "\\s*(and)\\s*",
                    "\\s*(credited)\\s*", "INR", " ");
            expDb.AddExpenseFilter(ef2);
            ExpenseFilter ef3 = new ExpenseFilter("(?s).*\\bINR\\b.*\\bdebited\\b.*",
                    "\\s*(Info:)\\s*", "\\s*[:.]\\s*", "INR ", " ");
            expDb.AddExpenseFilter(ef3);
            ExpenseFilter ef4 = new ExpenseFilter("(?s).*\\bis debited from\\b.*", "\\s*(for)\\s*",
                    "\\s*(via)\\s*", "Rs.", " ");
            expDb.AddExpenseFilter(ef4);
            ExpenseFilter ef5 = new ExpenseFilter("(?s).*\\busing\\b.*\\bfoodplus card\\b.*", " at ",
                    " on ", "INR", " ");
            expDb.AddExpenseFilter(ef5);
            ExpenseFilter ef6 = new ExpenseFilter("(?s).*\\byou.*ve withdrawn\\b.*", " at ",
                    " on ", "Rs.", " ");
            expDb.AddExpenseFilter(ef6);
            ExpenseFilter ef7 = new ExpenseFilter("(?s).*\\bdebited with rs\\b.*", "Ur ",
                    " ", "Rs.", " ");
            expDb.AddExpenseFilter(ef7);
            filterList = expDb.GetExpenseFilterList();
        }

        return filterList;
    }


    private Date getDateWithTimeSetToZero(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return cal.getTime();
    }


    private void scanSmsInboxForExpense() {
        String date_pattern = "yyyy-MM-dd, EEE";
        DateFormat dateFormat = new SimpleDateFormat(date_pattern);

        Calendar cal2 = Calendar.getInstance();
        cal2.add(Calendar.MONTH, -DEFAULT_MONTH_SPAN); // last 2 months
        mSdate = getDateWithTimeSetToZero(cal2);
        Calendar cal1 = Calendar.getInstance();   // this takes current date
        cal1.set(Calendar.DAY_OF_MONTH, 1);
        mMdate = getDateWithTimeSetToZero(cal1);
        mEdate = new Date();
        Double money = 0.0;
        String filter = "date>=" + mSdate.getTime() + " and date<=" + mEdate.getTime();
        Log.d(TAG, dateFormat.format(mSdate) + " to " + dateFormat.format(mEdate));
        Log.d(TAG, filter);

        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(Uri.parse("content://sms/inbox"),
                null, filter, null, null);
        //String type = cursor.getString(cursor.getColumnIndex(Telephony.Sms.TYPE));
        int indexBody = cursor.getColumnIndex(Telephony.Sms.BODY);
        int indexAddress = cursor.getColumnIndex("address");
        int indexDate = cursor.getColumnIndex(Telephony.Sms.DATE);

        if (indexBody < 0 || !cursor.moveToFirst()) return;
        boolean valid_expense = false;
        mTotalMoney = 0.0;
        do {

            String body = cursor.getString(indexBody);
            String addr = cursor.getString(indexAddress);
            Date smsDate = new Date(Long.valueOf(cursor.getString(indexDate)));
            String date =  dateFormat.format(smsDate);

            // collect all information
            for (ExpenseFilter ef: mExpFilterList) {
                Log.d(TAG, "filter: " + ef.filter);
                try {
                    if (body.toLowerCase().matches(ef.filter.toLowerCase())) {
                        money = parseMoneyFromMessage(body, ef);
                        String sender = parseSenderFromMessage(body, ef);
                        SmsSender smsSender = new SmsSender(sender);
                        Expense expense = new Expense(money, body, null, smsSender, date, addr);
                        mExpenseList.add(expense);
                        valid_expense = true;
                        break;
                    }
                }
                catch (Exception e) {
                    Log.d(TAG, "Exception " + e.getMessage());
                }
            }

            // add to window total
            if (valid_expense) {
                if (smsDate.after(mMdate) || smsDate.equals(mMdate)) {
                    mTotalMoney += money;
                    valid_expense = false; // mark it as accounted!
                }
            }
            Log.d(TAG, "Sender: " + addr);
        } while (cursor.moveToNext());
    }

    private double parseMoneyFromMessage(String msg, ExpenseFilter ef) {
        double money;

        try {
            String[] parts = msg.split(ef.money_start); // "ALERT: You've spent Rs.729.00  on CREDIT Card ..."
            String money_str = parts[1].split(ef.money_end)[0]; // split the 2nd part based on space and take the first part
            money = Double.parseDouble(money_str.replaceAll(",", ""));
        }
        catch (Exception e) {
            money = 0.0;
        }

        return money;
    }

    private String parseSenderFromMessage(String msg, ExpenseFilter ef) {
        String sender;

        try {
            String[] parts = msg.split(ef.sender_start);
            sender = parts[1].split(ef.sender_end)[0];
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
            Log.d(TAG, "Switching to View Expense Categories");
            intent.putExtra("explist", mExpenseList);
            startActivity(intent);
        } else if (id == R.id.sms_senders) {
            Intent intent = new Intent(this, SmsSendersViewActivity.class);
            Log.d(TAG, "Switching to View SMS Senders");
            ExpCategory expCategory = new ExpCategory("*");
            intent.putExtra("expcat", expCategory);
            intent.putExtra("explist", mExpenseList);
            startActivity(intent);
        } else if (id == R.id.exp_filters) {
            Intent intent = new Intent(this, ExpenseFilterViewActivty.class);
            Log.d(TAG, "Switching to View Expense Filters");
            startActivity(intent);
            mRcVwUpdateNeeded = true;
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
        mMainRecyclerAdapter = new MainActivityRecyclerAdapter(mExpenseList);
        mRecyclerView.addItemDecoration(new DividerItemDecorator(this, LinearLayoutManager.VERTICAL));
        mRecyclerView.setAdapter(mMainRecyclerAdapter);

        mMainRecyclerAdapter.notifyDataSetChanged();

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
