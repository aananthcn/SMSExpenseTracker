package com.nonprofit.aananth.sms_expensetracker;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


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

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Category Table Functions

    private void CreateExpCatTableIfNotExists(SQLiteDatabase db) {
        String query = "CREATE TABLE IF NOT EXISTS " + EXP_CAT_TABLE + " ( " + PKEY +
                " INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR(100), uid VARCHAR(100) )";
        db.execSQL(query);
    }

    public void AddCategory2ExpCatTable(SmsExpCategory category) {
        SQLiteDatabase db = this.getWritableDatabase();

        String uid = (category.expCatName + "_" + new Date()).replaceAll("[^a-zA-Z0-9]+", "_");
        String query = "INSERT INTO " + EXP_CAT_TABLE + " (name, uid) VALUES ('"+
                category.expCatName + "', '" + uid + "')";

        Log.d(TAG, query);
        db.execSQL(query);

        mDbChanged = true;
        db.close();
    }

    // This function returns the senderTable name by browsing the EXP_CAT_TABLE
    public String GetSenderTableName(SQLiteDatabase db, SmsExpCategory cat) {
        String tableName = null;
        String query = "SELECT * FROM " + EXP_CAT_TABLE + " ORDER BY " + PKEY + cat.expCatName;
        Log.d(TAG, query);
        Cursor res = db.rawQuery(query, null);
        res.moveToFirst();

        if (res.getCount() <= 0) {
            res.close();
            return null;
        }

        Log.d(TAG, "Number of entries found = "+res.getCount());
        // return the first item. It is expected that only one entry is present
        if(!res.isAfterLast()){
            tableName = res.getString(res.getColumnIndex("uid"));
        }

        res.close();

        return tableName;
    }

    public List<SmsExpCategory> GetExpCategoryList() {
        SQLiteDatabase db = this.getWritableDatabase();
        List<SmsExpCategory> expCatList = new ArrayList<>();
        String query, name;
        int id;
        Cursor res;
        SmsExpCategory category;

        query = "SELECT * FROM " + EXP_CAT_TABLE + " ORDER BY " + PKEY;
        Log.d(TAG, query);
        res = db.rawQuery(query, null);
        res.moveToFirst();

        if (res.getCount() <= 0) {
            category = new SmsExpCategory("Empty", 0);
            expCatList.add(category);
            res.close();
            return expCatList;
        }

        Log.d(TAG, "Number of doctors = "+res.getCount());
        while(!res.isAfterLast()){
            name = res.getString(res.getColumnIndex("name"));
            id = res.getInt(res.getColumnIndex(PKEY));
            category = new SmsExpCategory(name, id);
            expCatList.add(category);

            res.moveToNext();
        }

        res.close();
        db.close();
        return expCatList;
    }



    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Sender Table Functions

    public void createSenderTableIfNotExists(SQLiteDatabase db, String tablename) {
        String query;

        query = "CREATE TABLE IF NOT EXISTS " + tablename + " ( " + PKEY + 
                " INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR(100) )";

        Log.d(TAG, query);
        db.execSQL(query);
    }

    public void AddSmsSender(SmsSender sender, SmsExpCategory category) {
        String tablename;
        SQLiteDatabase db = this.getWritableDatabase();

        tablename = GetSenderTableName(db, category);
        createSenderTableIfNotExists(db, tablename);

        String query = "INSERT INTO " + tablename + " (name) VALUES ('"+ sender.name + "')";

        Log.d(TAG, query);
        db.execSQL(query);
        mDbChanged = true;
        db.close();
    }

    public void UpdateSmsSender(SmsSender smsSender, SmsExpCategory category) {
        SQLiteDatabase db = this.getWritableDatabase();
        String tablename = GetSenderTableName(db, category);

        String query = "UPDATE " + tablename + " SET name = '" + smsSender.name +
                "' WHERE " + PKEY + " = '" + smsSender.id + "';";

        Log.d(TAG, query);
        db.execSQL(query);
        mDbChanged = true;
        db.close();
    }

    public void DeleteSmsSender(SmsSender smsSender, SmsExpCategory category) {
        SQLiteDatabase db = this.getWritableDatabase();
        String tablename = GetSenderTableName(db, category);

        String query = "DELETE FROM " + tablename + " WHERE " + PKEY + " = '" + smsSender.id + "';";

        Log.d(TAG, query);
        db.execSQL(query);
        mDbChanged = true;
        db.close();
    }

    public List<SmsSender> GetSenderListInCategory(SmsExpCategory category) {
        SQLiteDatabase db = this.getWritableDatabase();
        List<SmsSender> senderList = new ArrayList<>();
        String query, name;
        int id;
        Cursor res;
        SmsSender sender;
        String tablename = GetSenderTableName(db, category);
        createSenderTableIfNotExists(db, tablename);

        query = "SELECT * FROM " + tablename + " ORDER BY " + PKEY;
        Log.d(TAG, query);
        res = db.rawQuery(query, null);
        res.moveToFirst();

        if (res.getCount() <= 0) {
            sender = new SmsSender("Empty", 0);
            senderList.add(sender);
            res.close();
            return senderList;
        }

        Log.d(TAG, "Number of doctors = "+res.getCount());
        while(!res.isAfterLast()){
            name = res.getString(res.getColumnIndex("name"));
            id = res.getInt(res.getColumnIndex(PKEY));
            sender = new SmsSender(name, id);
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
