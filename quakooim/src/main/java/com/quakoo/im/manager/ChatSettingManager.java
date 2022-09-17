package com.quakoo.im.manager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.base.BaseApplication;
import com.base.Constants;
import com.quakoo.im.ChatDBHelper;
import com.quakoo.im.model.ChatSettingEntity;
import com.quakoo.im.model.MainMessage;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class ChatSettingManager {

    private static final String TAG = "ChatSettingManager";
    private static ChatSettingManager mInstance;

    private Context context;

    public synchronized static ChatSettingManager getInstance() {
        if (null == mInstance) {
            mInstance = new ChatSettingManager();
        }
        return mInstance;
    }

    private ChatSettingManager() {
        context = BaseApplication.getInstance();
    }

    /**
     * 查询该好友的聊天设置
     *
     * @param username
     * @param friend
     * @return
     */
    public synchronized ChatSettingEntity query(String username, String friend) {
        SQLiteDatabase db = ChatDBHelper.getInstance(context).getReadableDatabase();
        Cursor cursor = db.query("chat_setting", null, "username = ?  and friend = ?",
                new String[]{username, friend}, null, null, null);
        ChatSettingEntity chatSetting = new ChatSettingEntity();
        if (cursor.moveToNext()) {
            chatSetting.setMaster(cursor.getString(cursor.getColumnIndex("username")));
            chatSetting.setFriend(cursor.getString(cursor.getColumnIndex("friend")));
            chatSetting.setScreenshot(cursor.getString(cursor.getColumnIndex("screenshot_notice")).equals("1"));
            chatSetting.setDestroy(cursor.getString(cursor.getColumnIndex("destroy_msg")).equals("1"));
            chatSetting.setShield(cursor.getString(cursor.getColumnIndex("shield_msg")).equals("1"));
            chatSetting.setChattheme(cursor.getString(cursor.getColumnIndex("chat_theme")));
            chatSetting.setChatTop(cursor.getString(cursor.getColumnIndex("chat_top")).equals("1"));
        } else {
            ContentValues values = new ContentValues();
            values.put("username", username);
            values.put("friend", friend);
            values.put("screenshot_notice", false);
            values.put("destroy_msg", false);
            values.put("shield_msg", false);
            values.put("chat_top", false);
            values.put("chat_theme", "");
            db.insert("chat_setting", null, values);
            chatSetting.setMaster(username);
            chatSetting.setFriend(friend);
        }
        cursor.close();
        db.close();
        return chatSetting;
    }

    /**
     * 查询当前用户,全部好友的聊天设置
     *
     * @param username
     * @return
     */
    public synchronized List queryAll(String username) {
        SQLiteDatabase db = ChatDBHelper.getInstance(context).getReadableDatabase();
        Cursor cursor = db.query("chat_setting", null, "username = ?",
                new String[]{username}, null, null, null);
        List<ChatSettingEntity> list = new ArrayList<ChatSettingEntity>();
        while (cursor.moveToNext()) {
            ChatSettingEntity chatSetting = new ChatSettingEntity();
            chatSetting.setMaster(cursor.getString(cursor.getColumnIndex("username")));
            chatSetting.setFriend(cursor.getString(cursor.getColumnIndex("friend")));
            chatSetting.setScreenshot(cursor.getString(cursor.getColumnIndex("screenshot_notice")).equals("1"));
            chatSetting.setDestroy(cursor.getString(cursor.getColumnIndex("destroy_msg")).equals("1"));
            chatSetting.setShield(cursor.getString(cursor.getColumnIndex("shield_msg")).equals("1"));
            chatSetting.setChattheme(cursor.getString(cursor.getColumnIndex("chat_theme")));
            list.add(chatSetting);
        }
        cursor.close();
        db.close();
        return list;
    }


    /**
     * 消息免打扰
     *
     * @param username
     * @param friend
     * @param shieldMsg
     */
    public synchronized void updateShieldMsg(String username, String friend, boolean shieldMsg) {
        SQLiteDatabase db = ChatDBHelper.getInstance(context).getReadableDatabase();
        Cursor cursor = db.query("chat_setting", null, "username = ?  and friend = ?",
                new String[]{username, friend}, null, null, null);
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("friend", friend);
        values.put("shield_msg", shieldMsg);
        if (cursor.moveToNext()) {
            db.update("chat_setting", values, "_id=?", new String[]{cursor.getInt(0) + ""});
        } else {
            values.put("screenshot_notice", false);
            values.put("destroy_msg", false);
            values.put("chat_theme", "");
            db.insert("chat_setting", null, values);
        }
        MainMessage mainMessage = new MainMessage();
        mainMessage.setKey(Constants.CHATSETTING);
        ChatSettingEntity entity = query(username, friend);
        mainMessage.setValue(mainMessage.toJson(entity));
        EventBus.getDefault().postSticky(mainMessage);
        cursor.close();
        db.close();
    }

    /**
     * 阅后即焚
     *
     * @param username
     * @param friend
     * @param destroyMsg
     */
    public synchronized void updateDestroyMsg(String username, String friend, boolean destroyMsg) {
        SQLiteDatabase db = ChatDBHelper.getInstance(context).getReadableDatabase();
        Cursor cursor = db.query("chat_setting", null, "username = ?  and friend = ?",
                new String[]{username, friend}, null, null, null);
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("friend", friend);
        values.put("destroy_msg", destroyMsg);
        if (cursor.moveToNext()) {
            db.update("chat_setting", values, "_id=?", new String[]{cursor.getInt(0) + ""});
        } else {
            values.put("screenshot_notice", false);
            values.put("shield_msg", false);
            values.put("chat_theme", "");
            db.insert("chat_setting", null, values);
        }
        MainMessage mainMessage = new MainMessage();
        mainMessage.setKey(Constants.CHATSETTING);
        ChatSettingEntity entity = query(username, friend);
        mainMessage.setValue(mainMessage.toJson(entity));
        EventBus.getDefault().postSticky(mainMessage);
        cursor.close();
        db.close();
    }

    /**
     * 截屏通知
     *
     * @param username
     * @param friend
     * @param screenshot
     */
    public synchronized void updateScreenShot(String username, String friend, boolean screenshot) {
        SQLiteDatabase db = ChatDBHelper.getInstance(context).getReadableDatabase();
        Cursor cursor = db.query("chat_setting", null, "username = ?  and friend = ?",
                new String[]{username, friend}, null, null, null);
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("friend", friend);
        values.put("screenshot_notice", screenshot);
        if (cursor.moveToNext()) {
            db.update("chat_setting", values, "_id=?", new String[]{cursor.getInt(0) + ""});
        } else {
            values.put("destroy_msg", false);
            values.put("shield_msg", false);
            values.put("chat_theme", "");
            db.insert("chat_setting", null, values);
        }
        MainMessage mainMessage = new MainMessage();
        mainMessage.setKey(Constants.CHATSETTING);
        ChatSettingEntity entity = query(username, friend);
        mainMessage.setValue(mainMessage.toJson(entity));
        EventBus.getDefault().postSticky(mainMessage);
        cursor.close();
        db.close();
    }

    /**
     * 消息置顶
     *
     * @param username
     * @param friend
     * @param chat_top
     */
    public synchronized void updateChatTop(String username, String friend, boolean chat_top) {
        SQLiteDatabase db = ChatDBHelper.getInstance(context).getReadableDatabase();
        Cursor cursor = db.query("chat_setting", null, "username = ?  and friend = ?",
                new String[]{username, friend}, null, null, null);
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("friend", friend);
        values.put("chat_top", chat_top);
        if (cursor.moveToNext()) {
            db.update("chat_setting", values, "_id=?", new String[]{cursor.getInt(0) + ""});
        } else {
            values.put("destroy_msg", false);
            values.put("shield_msg", false);
            values.put("screenshot_notice", false);
            values.put("chat_theme", "");
            db.insert("chat_setting", null, values);
        }
        cursor.close();
        db.close();
    }

}
