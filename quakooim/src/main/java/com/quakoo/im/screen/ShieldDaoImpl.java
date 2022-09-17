package com.quakoo.im.screen;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/11/21.
 */

public class ShieldDaoImpl implements UserDao {

    UserDBHelper dbHelper;

    public ShieldDaoImpl(Context context) {
        dbHelper = new UserDBHelper(context);
    }

    @Override
    public synchronized void insertThread(User screenInfo) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put("master", screenInfo.master);
        values.put("user_id", screenInfo.userId);
        db.insert("shield_user", null, values);
        db.close();
    }

    @Override
    public synchronized void deleteThread(String master, String user_id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        int count = db.delete("shield_user", "master = ? and user_id = ?", new String[]{master, user_id});
        db.close();
    }

    @Override
    public synchronized void updateThread(String master, String user_id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ContentValues values = new ContentValues();
        db.update("shield_user", values, "master = ? and user_id = ? ", new String[]{master, user_id});
    }

    @Override
    public synchronized void updateThread(String master, String user_id, String chat_bg) {

    }

    @Override
    public synchronized List<User> queryThread(String master, String user_id) {
        List<User> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("shield_user", null, "master = ?  and user_id = ?",
                new String[]{master, user_id}, null, null, null);
        while (cursor.moveToNext()) {
            User threadInfo = new User();
            threadInfo.master = cursor.getString(cursor.getColumnIndex("master"));
            threadInfo.userId = cursor.getString(cursor.getColumnIndex("user_id"));
            list.add(threadInfo);
        }
        cursor.close();
        db.close();
        return list;
    }

    @Override
    public synchronized List<User> queryThreadAll(String master) {
        List<User> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("shield_user", null, "master = ?",
                new String[]{master}, null, null, null);
        while (cursor.moveToNext()) {
            User threadInfo = new User();
            threadInfo.master = cursor.getString(cursor.getColumnIndex("master"));
            threadInfo.userId = cursor.getString(cursor.getColumnIndex("user_id"));
            list.add(threadInfo);
        }
        cursor.close();
        db.close();
        return list;
    }

    public synchronized boolean isExists(String master, String user_id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("shield_user", null, "master = ? and user_id = ?",
                new String[]{master, user_id}, null, null, null);
        boolean exists = cursor.moveToNext();
        cursor.close();
        db.close();
        return exists;
    }
}