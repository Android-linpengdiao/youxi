package com.yuoxi.android.app.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import com.yuoxi.android.app.MainApplication;

public class DBHelper extends SQLiteOpenHelper {

    private final static String DB_NAME = "search.db";
    private final static int DB_VERSION = 1;
    public final static String TABLE_NAME = "search_history";
    private static volatile DBHelper sInstance;


    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public synchronized static DBHelper getInstance() {
        if (sInstance == null) {
            synchronized (DBHelper.class) {
                if (sInstance == null) {
                    sInstance = new DBHelper(MainApplication.getInstance());
                }
            }
        }
        return sInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    private void createTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE search_history (_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "userId VARCHAR," +
                "search_content VARCHAR)");
    }

}