package com.quakoo.im.screen;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Administrator on 2017/11/21.
 */


public class UserDBHelper extends SQLiteOpenHelper {
    private static final String TAG = "UserDBHelper";
    public static final String DB_NAME = "screen.db";
    public static final int DB_VERSION = 2;
    public static final String CREATE_SCREEN_TABLE = "create table screen_user(id integer primary key autoincrement," +
            "master integer,user_id integer)";
    public static final String CREATE_DESTROY_TABLE = "create table destroy_user(id integer primary key autoincrement," +
            "master integer,user_id integer)";
    public static final String CREATE_SHIELD_TABLE = "create table shield_user(id integer primary key autoincrement," +
            "master integer,user_id integer)";
    public static final String CREATE_BLACKLIST_TABLE = "create table blacklist_user(id integer primary key autoincrement," +
            "master integer,user_id integer)";
    public static final String CREATE_CHATBG_TABLE = "create table chatbg_user(id integer primary key autoincrement," +
            "master integer,user_id integer,chat_bg varchar)";

    public UserDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate: ");
        db.execSQL(CREATE_SCREEN_TABLE);
        db.execSQL(CREATE_DESTROY_TABLE);
        db.execSQL(CREATE_SHIELD_TABLE);
        db.execSQL(CREATE_BLACKLIST_TABLE);
        db.execSQL(CREATE_CHATBG_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade: oldVersion "+oldVersion);
        Log.d(TAG, "onUpgrade: newVersion "+newVersion);
//        if (oldVersion == 1) {
//            db.execSQL(CREATE_SCREEN_TABLE);
//            db.execSQL(CREATE_DESTROY_TABLE);
//            db.execSQL(CREATE_SHIELD_TABLE);
//            db.execSQL(CREATE_BLACKLIST_TABLE);
//        }
        if (newVersion == 2) {
            db.execSQL(CREATE_CHATBG_TABLE);
        }
    }
}