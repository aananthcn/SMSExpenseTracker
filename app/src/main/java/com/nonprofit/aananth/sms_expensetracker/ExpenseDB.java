package com.nonprofit.aananth.sms_expensetracker;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by aananth on 02/03/19.
 */

public class ExpenseDB extends SQLiteOpenHelper{
    private static final String TAG = ExpenseDB.class.getSimpleName();
    public static final String PKEY = "_id";
    public static final String MAIN_DATABASE = "SmsExpMan.db";
    public static final int DATABASE_VERSION = 1;


    // This is the master of all tables. Each row in this table will point to
    // sender tables, which contains list of senders that should be classified
    // under each category.
    public static final String EXP_CAT_TABLE = "expCatTable";
    public static final String EXP_FILTER_LIST = "expFilterList";
    public static final String SMS_SENDER_LIST = "smsSenderList";


    private boolean mDbChanged = false;

    // Database creation sql statement
    public ExpenseDB(Context context) {
        super(context, MAIN_DATABASE, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        CreateExpCatTableIfNotExists(database);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");

        // TODO: In future we should add saving/exporting old tables before delete
        /*
        String query;
        List<Patient> patientList = GetPatientList(null, ListOrder.ASCENDING);

        // Delete all treatment history of all patients
        for (Patient pat: patientList) {
            query = "DROP TABLE IF EXISTS '" + pat.Uid + "'";
            db.execSQL(query);
        }

        // Delete the patient table and then create it in the end
        query = "DROP TABLE IF EXISTS " + PATIENT_LIST;
        db.execSQL(query);
        onCreate(db);
        mDbChanged = true;
        */
    }

    private String getUniqueID(String inStr) {
        return  (inStr + "_" + new Date()).replaceAll("[^a-zA-Z0-9]+", "_");
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Expense Filter Table Functions

    private void CreateExpFiltTableIfNotExists(SQLiteDatabase db) {
        String query = "CREATE TABLE IF NOT EXISTS " + EXP_FILTER_LIST + " ( " + PKEY +
                " INTEGER PRIMARY KEY AUTOINCREMENT, filter VARCHAR(255) )";
        db.execSQL(query);
    }

    public void AddExpenseFilter(String expFilter) {
        SQLiteDatabase db = this.getWritableDatabase();

        expFilter = expFilter.toLowerCase();
        String query = "INSERT INTO " + EXP_FILTER_LIST + " (filter) VALUES ('" + expFilter + "')";

        Log.d(TAG, query);
        db.execSQL(query);

        mDbChanged = true;
        db.close();
    }

    public List<String> GetExpenseFilterList() {
        SQLiteDatabase db = this.getWritableDatabase();
        List<String> expFilterList = null;
        String query, filter;
        int id;
        Cursor res;

        CreateExpFiltTableIfNotExists(db);
        query = "SELECT * FROM " + EXP_FILTER_LIST + " ORDER BY " + PKEY;
        Log.d(TAG, query);
        res = db.rawQuery(query, null);
        res.moveToFirst();

        if (res.getCount() > 0) {
            Log.d(TAG, "Number of filters = " + res.getCount());
            expFilterList = new ArrayList<>();

            while (!res.isAfterLast()) {
                filter = res.getString(res.getColumnIndex("filter"));
                expFilterList.add(filter);
                res.moveToNext();
            }
        }

        res.close();
        db.close();
        return expFilterList;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Expense Category Table Functions

    private void CreateExpCatTableIfNotExists(SQLiteDatabase db) {
        String query = "CREATE TABLE IF NOT EXISTS " + EXP_CAT_TABLE + " ( " + PKEY +
                " INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR(100), uid VARCHAR(100) )";
        db.execSQL(query);
    }

    public void AddExpenseCategory(ExpCategory expCat) {
        SQLiteDatabase db = this.getWritableDatabase();

        String uid = getUniqueID(expCat.expCatName);
        String query = "INSERT INTO " + EXP_CAT_TABLE + " (name, uid) VALUES ('"+
                expCat.expCatName + "', '" + uid + "')";

        Log.d(TAG, query);
        db.execSQL(query);

        mDbChanged = true;
        db.close();
    }

    public void UpdateExpenseCategory(ExpCategory expCategory) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + EXP_CAT_TABLE + " SET name = '" + expCategory.expCatName +
                "' WHERE uid = '" + expCategory.uid + "';";

        Log.d(TAG, query);
        db.execSQL(query);
        mDbChanged = true;
        db.close();
    }


    public void DeleteExpCategory(ExpCategory expCategory) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query;

        // delete category - SMS senders list table
        query = "DROP TABLE IF EXISTS '" + expCategory.uid + "'";
        Log.d(TAG, query);
        db.execSQL(query);

        // delete the patient
        query = "DELETE FROM " + EXP_CAT_TABLE + " WHERE uid = '" + expCategory.uid + "';";
        Log.d(TAG, query);
        db.execSQL(query);
        mDbChanged = true;
        db.close();
    }

    public List<ExpCategory> GetExpCategoryList() {
        SQLiteDatabase db = this.getWritableDatabase();
        List<ExpCategory> expCatList = new ArrayList<>();
        String query, name, uid;
        Cursor res;
        ExpCategory category;

        query = "SELECT * FROM " + EXP_CAT_TABLE + " ORDER BY " + PKEY;
        Log.d(TAG, query);
        res = db.rawQuery(query, null);
        res.moveToFirst();

        if (res.getCount() <= 0) {
            category = new ExpCategory("Empty", "Invalid");
            expCatList.add(category);
            res.close();
            db.close();
            return expCatList;
        }

        Log.d(TAG, "Number of com.nonprofit.aananth.sms_expensetracker.Expense Categories = "+res.getCount());
        while (!res.isAfterLast()){
            name = res.getString(res.getColumnIndex("name"));
            uid = res.getString(res.getColumnIndex("uid"));
            category = new ExpCategory(name, uid);
            expCatList.add(category);

            res.moveToNext();
        }

        res.close();
        db.close();
        return expCatList;
    }


    public ExpCategory GetExpCategoryFromUID(String uid) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query, name;
        Cursor res;
        ExpCategory category;

        query = "SELECT * FROM " + EXP_CAT_TABLE + " WHERE uid = '" + uid + "';";
        Log.d(TAG, query);
        res = db.rawQuery(query, null);
        res.moveToFirst();

        if (res.getCount() <= 0) {
            category = new ExpCategory("Empty", "Invalid");
            res.close();
            db.close();
            return category;
        }

        Log.d(TAG, "Number of com.nonprofit.aananth.sms_expensetracker.Expense Categories = "+res.getCount());
        name = res.getString(res.getColumnIndex("name"));
        uid = res.getString(res.getColumnIndex("uid"));
        category = new ExpCategory(name, uid);

        res.close();
        db.close();
        return category;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Sender Table Functions

    private void createSenderTableIfNotExists(SQLiteDatabase db) {
        String query = "CREATE TABLE IF NOT EXISTS " + SMS_SENDER_LIST + " ( " + PKEY +
                " INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR(100), cat_uid VARCHAR(100) )";
        db.execSQL(query);
    }

    public void AddSmsSender(SmsSender sender, ExpCategory category) {
        String tablename;
        SQLiteDatabase db = this.getWritableDatabase();

        createSenderTableIfNotExists(db);

        String query = "INSERT INTO " + SMS_SENDER_LIST + " (name, cat_uid) VALUES ('"+ sender.name
                + "', '" + category.uid + "')";

        Log.d(TAG, query);
        db.execSQL(query);
        mDbChanged = true;
        db.close();
    }

    public void UpdateSmsSender(SmsSender smsSender, ExpCategory category) {
        SQLiteDatabase db = this.getWritableDatabase();

        String query = "UPDATE " + SMS_SENDER_LIST + " SET name = '" + smsSender.name +
                "', cat_uid = '" + category.uid + "' WHERE " + PKEY + " = '" + smsSender.id + "';";

        Log.d(TAG, query);
        db.execSQL(query);
        mDbChanged = true;
        db.close();
    }

    public void DeleteSmsSender(SmsSender smsSender, ExpCategory category) {
        SQLiteDatabase db = this.getWritableDatabase();

        String query = "DELETE FROM " + SMS_SENDER_LIST + " WHERE " + PKEY + " = '" + smsSender.id + "';";

        Log.d(TAG, query);
        db.execSQL(query);
        mDbChanged = true;
        db.close();
    }

    public List<SmsSender> GetSenderList() {
        SQLiteDatabase db = this.getWritableDatabase();
        List<SmsSender> senderList = new ArrayList<>();
        String query, name, cat_uid;
        int id;
        Cursor res;
        SmsSender sender;

        createSenderTableIfNotExists(db);
        query = "SELECT * FROM " + SMS_SENDER_LIST + " ORDER BY " + PKEY;
        Log.d(TAG, query);
        res = db.rawQuery(query, null);
        res.moveToFirst();

        if (res.getCount() <= 0) {
            sender = new SmsSender("Empty", 0);
            sender.expCategory = new ExpCategory("Null");
            senderList.add(sender);
            res.close();
            db.close();
            return senderList;
        }

        Log.d(TAG, "Number of senders = "+res.getCount());
        while(!res.isAfterLast()){
            name = res.getString(res.getColumnIndex("name"));
            id = res.getInt(res.getColumnIndex(PKEY));
            sender = new SmsSender(name, id);
            cat_uid = res.getString(res.getColumnIndex("cat_uid"));
            sender.expCategory = GetExpCategoryFromUID(cat_uid);
            senderList.add(sender);

            res.moveToNext();
        }

        res.close();
        db.close();
        return senderList;
    }



    public boolean isDbChanged() {
        Log.d(TAG, "mDbChanged = " + mDbChanged);
        return mDbChanged;
    }


    public void DbSaved() {
        mDbChanged = false;
    }
}
