package com.quakoo.im.manager;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.base.BaseApplication;
import com.base.Constants;
import com.base.UserInfo;
import com.base.utils.CommonUtil;
import com.base.utils.MsgCache;
import com.base.utils.ToastUtils;
import com.quakoo.im.ChatDBHelper;
import com.quakoo.im.R;
import com.quakoo.im.aiyou.ImSocketService;
import com.quakoo.im.model.ChatMessage;
import com.quakoo.im.model.ChatSettingEntity;
import com.quakoo.im.model.UniteUpdateDataModel;
import com.quakoo.im.screen.User;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;


/**
 * Created by liang on 2017/5/27.
 */

public class IMChatManager {

    private static final String TAG = "IMChatManager";

    private static IMChatManager mInstance;

    private Context context;

    private LocalBroadcastManager localBroadcastManager = null;

    private Socket socket;

//    private static ShieldDaoImpl shieldDao;


    public static IMChatManager getInstance(Context context) {
        if (null == mInstance) {
            mInstance = new IMChatManager(context);
        }
//        if (null == shieldDao) {
//            shieldDao = new ShieldDaoImpl(context);
//        }
        return mInstance;
    }

    private IMChatManager(Context context) {
        this.context = context;
        this.localBroadcastManager = LocalBroadcastManager.getInstance(context);
    }

    /**
     * 获取更多的收藏列表
     *
     * @param username
     * @param limit
     * @return
     */
    public List<ChatMessage> getMoreChatCollect(String username, int curCount, int limit) {
        List<ChatMessage> list = new ArrayList<>();
        ChatDBHelper helper = new ChatDBHelper(context);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT * FROM (SELECT * FROM chat_collect WHERE username='" + username + "' ORDER BY _id DESC LIMIT " + curCount + "," + limit + ")", null);
        while (cur.moveToNext()) {
            list.add(cursorToChatMessage(cur));
        }
        cur.close();
        db.close();
        return list;
    }

    /**
     * 获取收藏列表
     *
     * @param username
     * @param limit
     * @return
     */
    public List<ChatMessage> getChatCollect(String username, int limit) {
        List<ChatMessage> list = new ArrayList<>();
        ChatDBHelper helper = new ChatDBHelper(context);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT * FROM (SELECT * FROM chat_collect WHERE username='" + username + "' ORDER BY _id DESC LIMIT 0," + limit + ")", null);
        while (cur.moveToNext()) {
            list.add(cursorToChatMessage(cur));
        }
        cur.close();
        db.close();
        return list;
    }

    // 将消息收藏数据库
    public void chatCollect(String master, ChatMessage cm) {

        ChatDBHelper dbHelper = new ChatDBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", master);
        values.put("msg_from", cm.getFromuser());
        values.put("msg_fromnick", cm.getFromusernick());
        values.put("msg_fromremark", cm.getFromuserremark());
        values.put("msg_to", cm.getTouser());
        values.put("msg_tonick", cm.getTousernick());
        values.put("msg_toremark", cm.getTouserremark());
        values.put("msg_content", cm.getContent());
        values.put("msg_type", cm.getType());
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        values.put("msg_time", df.format(new Date()));
        values.put("msg_serveId", cm.getServeId());
        values.put("msg_clientId", cm.getClientId());
        values.put("msg_avatar", cm.getAvatar());
        values.put("msg_username", cm.getUsername());
        values.put("msg_usericon", cm.getUserIcon());
        values.put("msg_nickname", cm.getNickname());
        values.put("msg_userremark", cm.getUserremark());
        values.put("msg_state", cm.getMsgState());
        values.put("msg_media_url", cm.getUrl());
        values.put("msg_audio_state", cm.getAudioState());
        values.put("msg_content_type", cm.getContentType());
        values.put("msg_destroy", cm.getDestroy());
        values.put("msg_media_extra", cm.getExtra());
        values.put("msg_media_thumbnail", cm.getThumbnailUrl());
        values.put("msg_progress", cm.getProgress());
        values.put("msg_prompt", cm.getPrompt());
//        values.put("msg_label", cm.getLabel());

        long result = db.insert("chat_collect", null, values);

        db.close();
    }

    // 将消息保存到数据库
    public void save(ChatMessage cm) {
        if (cm.getContentType().equals(ChatMessage.CHAT_CONTENT_TYPE_NOTICE)){
            JSONObject json = null;
            try {
                json = new JSONObject(cm.getExtra());
                String ext_type = json.optString("type");
                ChatSettingEntity entity = ChatSettingManager.getInstance().query(cm.getMaster(),cm.getMaster().endsWith(cm.getFromuser()) ? cm.getTouser() : cm.getFromuser());
                if ( ext_type.equals("3") && !entity.getScreenshot()){ // 等于截屏通知 外加 自己没有开启截屏通知 隐藏
                    return;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        ChatDBHelper dbHelper = new ChatDBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", cm.getMaster());
        values.put("msg_from", cm.getFromuser());
        values.put("msg_fromnick", cm.getFromusernick());
        values.put("msg_fromremark", cm.getFromuserremark());
        values.put("msg_to", cm.getTouser());
        values.put("msg_tonick", cm.getTousernick());
        values.put("msg_toremark", cm.getTouserremark());
        values.put("msg_content", cm.getContent());
        values.put("msg_type", cm.getType());
        values.put("msg_time", cm.getTime());
        values.put("msg_serveId", cm.getServeId());
        values.put("msg_clientId", cm.getClientId());
        values.put("msg_avatar", cm.getAvatar());
        values.put("msg_username", cm.getUsername());
        values.put("msg_usericon", cm.getUserIcon());
        values.put("msg_nickname", cm.getNickname());
        values.put("msg_userremark", cm.getUserremark());
        values.put("msg_state", cm.getMsgState());
        values.put("msg_media_url", cm.getUrl());
        values.put("msg_audio_state", cm.getAudioState());
        values.put("msg_content_type", cm.getContentType());
        values.put("msg_destroy", cm.getDestroy());
        values.put("msg_media_extra", cm.getExtra());
        values.put("msg_media_thumbnail", cm.getThumbnailUrl());
        values.put("msg_progress", cm.getProgress());
        values.put("msg_prompt", cm.getPrompt());
        values.put("msg_send_state", cm.getSendMsgState());
        values.put("msg_redopen_state", cm.getRedOpenState());
        Cursor cur = null ;
        String[] data =null;
        String sql="";
        if (!cm.getServeId().isEmpty()){ //判断传入的服务器ID是否为空 不为空,判断条件算上服务器id
            sql = "SELECT * FROM chat_history WHERE msg_clientId ='" + cm.getClientId() + "' or msg_serveId ='" + cm.getServeId() + "'";
            data = new String[]{cm.getClientId(),cm.getServeId()};
        }else {
            sql = "SELECT * FROM chat_history WHERE msg_clientId ='" + cm.getClientId() + "'";
            data = new String[]{cm.getClientId()};
        }
        cur = db.rawQuery(sql, null);
        if (cur!=null && cur.moveToNext()) {
            Log.i(TAG,"更新:"+ cm.toString());
            db.update("chat_history", values, "msg_clientId = ? or msg_serveId = ?" , data);;
        } else {
            Log.i(TAG,"添加:"+ cm.toString());
            db.insert("chat_history", null, values);
        }
        // 将消息更新至最近聊天列表
        saveCMtoRecent(cm);
        // 发送更新通知
        cur.close();
        db.close();
    }

    // 将消息保存到数据库
    public void save(List<ChatMessage> list) {

        ChatDBHelper dbHelper = new ChatDBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            for (int i = 0; i < list.size(); i++) {
                ChatMessage cm = list.get(i);
                ContentValues values = new ContentValues();
                values.put("username", cm.getMaster());
                values.put("msg_from", cm.getFromuser());
                values.put("msg_fromnick", cm.getFromusernick());
                values.put("msg_fromremark", cm.getFromuserremark());
                values.put("msg_to", cm.getTouser());
                values.put("msg_tonick", cm.getTousernick());
                values.put("msg_toremark", cm.getTouserremark());
                values.put("msg_content", cm.getContent());
                values.put("msg_type", cm.getType());
                values.put("msg_time", cm.getTime());
                values.put("msg_serveId", cm.getServeId());
                values.put("msg_clientId", cm.getClientId());
                values.put("msg_avatar", cm.getAvatar());
                values.put("msg_username", cm.getUsername());
                values.put("msg_usericon", cm.getUserIcon());
                values.put("msg_nickname", cm.getNickname());
                values.put("msg_userremark", cm.getUserremark());
                values.put("msg_state", cm.getMsgState());
                values.put("msg_media_url", cm.getUrl());
                values.put("msg_audio_state", cm.getAudioState());
                values.put("msg_content_type", cm.getContentType());
                values.put("msg_destroy", cm.getDestroy());
                values.put("msg_media_extra", cm.getExtra());
                values.put("msg_media_thumbnail", cm.getThumbnailUrl());
                values.put("msg_progress", cm.getProgress());
                values.put("msg_prompt", cm.getPrompt());
                values.put("msg_send_state", cm.getSendMsgState());
                values.put("msg_redopen_state", cm.getRedOpenState());

                db.insert("chat_history", null, values);

                // 将消息更新至最近聊天列表
//                saveCMtoRecent(cm);
                // 发送更新通知
//        Intent intent = new Intent("CHAT_RECENT_MESSAGE");
//        this.localBroadcastManager.sendBroadcast(intent);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            throw e;
        } finally {
            //加上的代码
            db.endTransaction();
        }
        db.close();
    }

    // 更新信息到最近聊天数据库
    public void saveCMtoRecent(ChatMessage cm) {
        ContentValues values = new ContentValues();
        values.put("username", cm.getMaster());
//        if (cm.getFromuser().equals(cm.getMaster())) {
//            values.put("msg_from", cm.getTouser());
//            values.put("msg_fromnick", cm.getTousernick());
//            values.put("msg_fromremark", cm.getTouserremark());
//            values.put("msg_to", cm.getFromuser());
//            values.put("msg_tonick", cm.getFromusernick());
//            values.put("msg_avatar", cm.getAvatar());
//        } else {
//            values.put("msg_from", cm.getFromuser());
//            values.put("msg_fromnick", cm.getFromusernick());
//            values.put("msg_fromremark", cm.getFromuserremark());
//            values.put("msg_to", cm.getTouser());
//            values.put("msg_tonick", cm.getTousernick());
//            values.put("msg_toremark", cm.getTouserremark());
//            values.put("msg_avatar", cm.getAvatar());
//        }
        values.put("msg_from", cm.getFromuser());
        values.put("msg_fromnick", cm.getFromusernick());
        values.put("msg_fromremark", cm.getFromuserremark());
        values.put("msg_to", cm.getTouser());
        values.put("msg_tonick", cm.getTousernick());
        values.put("msg_toremark", cm.getTouserremark());
        values.put("msg_nickname", cm.getNickname());
        values.put("msg_avatar", cm.getAvatar());

        values.put("msg_userremark", cm.getUserremark());
        values.put("msg_username", cm.getUsername());
        values.put("msg_usericon", cm.getUserIcon());
        values.put("msg_content", cm.getContent());
        values.put("msg_type", cm.getType());
        values.put("msg_time", cm.getTime());
        values.put("msg_serveId", cm.getServeId());
        values.put("msg_clientId", cm.getClientId());
        values.put("msg_media_url", cm.getUrl());
        values.put("msg_audio_state", cm.getAudioState());
        values.put("msg_content_type", cm.getContentType());
        values.put("msg_destroy", cm.getDestroy());
        values.put("msg_media_extra", cm.getExtra());
        values.put("msg_media_thumbnail", cm.getThumbnailUrl());
        values.put("msg_progress", cm.getProgress());
        values.put("msg_prompt", cm.getPrompt());
        values.put("msg_redopen_state", cm.getRedOpenState());

        ChatDBHelper helper = new ChatDBHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        // 计算未读消息数量
        int unreadCount = 0;
        Cursor cur = db.rawQuery("SELECT COUNT(*) FROM chat_history WHERE username='" + cm.getMaster() + "' " +
                "AND msg_type='" + cm.getType() + "' " +
                "AND msg_state='unread' " +
                "AND ((msg_from='" + cm.getFromuser() + "' AND msg_to='" + cm.getTouser() + "') OR (msg_from='" + cm.getTouser() + "' AND msg_to='" + cm.getFromuser() + "'))", null);
        if (cur.moveToNext()) {
            unreadCount = cur.getInt(0);
        }
        values.put("msg_unread_count", unreadCount);
        // 将最近聊天数据更新至数据库
        cur = db.rawQuery("SELECT _id FROM chat_recent WHERE username='" + cm.getMaster() + "' " +
                "AND msg_type='" + cm.getType() + "' " +
                "AND ((msg_from='" + cm.getFromuser() + "' AND msg_to='" + cm.getTouser() + "') OR (msg_from='" + cm.getTouser() + "' AND msg_to='" + cm.getFromuser() + "'))", null);
        if (cur.moveToNext()) {
            if (cm.getContentType().equals(ChatMessage.CHAT_CONTENT_TYPE_TXT)
                    && (cm.getExtra().indexOf(getUserInfo().id) != -1)) {
                values.put("msg_hint_state", "unread");
            }
            db.update("chat_recent", values, "_id=?", new String[]{cur.getInt(0) + ""});
        } else {
            if (cm.getContentType().equals(ChatMessage.CHAT_CONTENT_TYPE_TXT)
                    && (cm.getExtra().indexOf(getUserInfo().id) != -1)) {
                values.put("msg_hint_state", "unread");
            } else {
                values.put("msg_hint_state", "read");
            }
            db.insert("chat_recent", null, values);
        }

        cur.close();
        db.close();
    }

    // 更新信息到最近聊天数据库
    public void saveCMtoRecent(List<ChatMessage> list) {
        ChatDBHelper dbHelper = new ChatDBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cur = null;
        db.beginTransaction();
        try {
            for (int i = 0; i < list.size(); i++) {
                ChatMessage cm = list.get(i);

                ContentValues values = new ContentValues();
                values.put("username", cm.getMaster());
                if (cm.getFromuser().equals(cm.getMaster())) {
                    values.put("msg_from", cm.getTouser());
                    values.put("msg_fromnick", cm.getTousernick());
                    values.put("msg_fromremark", cm.getTouserremark());
                    values.put("msg_to", cm.getFromuser());
                    values.put("msg_tonick", cm.getFromusernick());
                    values.put("msg_toremark", cm.getFromuserremark());
                    values.put("msg_avatar", cm.getAvatar());
                } else {
                    values.put("msg_from", cm.getFromuser());
                    values.put("msg_fromnick", cm.getFromusernick());
                    values.put("msg_fromremark", cm.getFromuserremark());
                    values.put("msg_to", cm.getTouser());
                    values.put("msg_tonick", cm.getTousernick());
                    values.put("msg_toremark", cm.getTouserremark());
                    values.put("msg_avatar", cm.getAvatar());
                }
                values.put("msg_nickname", cm.getNickname());
                values.put("msg_userremark", cm.getUserremark());
                values.put("msg_username", cm.getUsername());
                values.put("msg_usericon", cm.getUserIcon());
                values.put("msg_content", cm.getContent());
                values.put("msg_type", cm.getType());
                values.put("msg_time", cm.getTime());
                values.put("msg_serveId", cm.getServeId());
                values.put("msg_clientId", cm.getClientId());
                values.put("msg_media_url", cm.getUrl());
                values.put("msg_audio_state", cm.getAudioState());
                values.put("msg_content_type", cm.getContentType());
                values.put("msg_destroy", cm.getDestroy());
                values.put("msg_media_extra", cm.getExtra());
                values.put("msg_media_thumbnail", cm.getThumbnailUrl());
                values.put("msg_progress", cm.getProgress());
                values.put("msg_prompt", cm.getPrompt());
                values.put("msg_redopen_state", cm.getRedOpenState());

//              ChatDBHelper helper = new ChatDBHelper(context);
//              SQLiteDatabase db = helper.getWritableDatabase();
                // 计算未读消息数量
                int unreadCount = 0;
                cur = db.rawQuery("SELECT COUNT(*) FROM chat_history WHERE username='" + cm.getMaster() + "' " +
                        "AND msg_type='" + cm.getType() + "' " +
                        "AND msg_state='unread' " +
                        "AND ((msg_from='" + cm.getFromuser() + "' AND msg_to='" + cm.getTouser() + "') OR (msg_from='" + cm.getTouser() + "' AND msg_to='" + cm.getFromuser() + "'))", null);
                if (cur.moveToNext()) {
                    unreadCount = cur.getInt(0);
                }
                values.put("msg_unread_count", unreadCount);
                // 将最近聊天数据更新至数据库
                cur = db.rawQuery("SELECT _id FROM chat_recent WHERE username='" + cm.getMaster() + "' " +
                        "AND msg_type='" + cm.getType() + "' " +
                        "AND ((msg_from='" + cm.getFromuser() + "' AND msg_to='" + cm.getTouser() + "') OR (msg_from='" + cm.getTouser() + "' AND msg_to='" + cm.getFromuser() + "'))", null);
                if (cur.moveToNext()) {
                    if (cm.getContentType().equals(ChatMessage.CHAT_CONTENT_TYPE_TXT)
                            && (cm.getExtra().indexOf(getUserInfo().id) != -1)) {
                        values.put("msg_hint_state", "unread");
                    }
                    db.update("chat_recent", values, "_id=?", new String[]{cur.getInt(0) + ""});
                } else {
                    if (cm.getContentType().equals(ChatMessage.CHAT_CONTENT_TYPE_TXT)
                            && (cm.getExtra().indexOf(getUserInfo().id) != -1)) {
                        values.put("msg_hint_state", "unread");
                    } else {
                        values.put("msg_hint_state", "read");
                    }
                    db.insert("chat_recent", null, values);
                }
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            throw e;
        } finally {
            //加上的代码
            db.endTransaction();
        }
        cur.close();
        db.close();

//        cur.close();
//        db.close();
    }

    // 收到消息的处理
    public void onRevMsg(ChatMessage cm, String master) {
        List<User> users = DBManager.getInstance().getShield().queryThreadAll(master + "");
        boolean shield = false;
        if (users.size() > 0) {
            for (User info : users) {
                if (cm.getFromuser().equals(info.userId) && !(cm.getExtra().indexOf(getUserInfo().id) != -1)) {
                    shield = true;
                    break;
                } else {
                    shield = false;
                }
            }
        }
        if (null != cm) {
            // 依次保存，通知，广播
            save(cm);
            if (!shield) {
                notice(cm);
            }
            sendBroadCast(cm);
        }
    }

    // 收到消息的处理
    public void onRevMsgs(List<ChatMessage> list, String master) {
//        List<User> users = DBManager.getInstance().getShield().queryThreadAll(master + "");
        save(list);
        boolean shield = false;
//        if (users.size() > 0) {
//            for (User info : users) {
//                if (list.get(list.size() - 1).getFromuser().equals(info.userId) && !(list.get(list.size() - 1).getExtra().indexOf(getUserInfo().id) != -1)) {
//                    shield = true;
//                    break;
//                } else {
//                    shield = false;
//                }
//            }
//        }
//        if (!shield) {
//            notice(list.get(list.size() - 1));
//        }

        if (ChatSettingManager.getInstance().query(master, list.get(list.size() - 1).getFromuser()).getShield() && !(list.get(list.size() - 1).getExtra().indexOf(getUserInfo().id) != -1)) {

        } else {
            notice(list.get(list.size() - 1));
        }

        ChatMessage chatMessage = list.get(list.size() - 1);
        for (int i = 0; i < list.size(); i++) {
            ChatMessage msg = list.get(i);
            if (msg.getContentType().equals(ChatMessage.CHAT_CONTENT_TYPE_TXT)
                    && (msg.getExtra().indexOf(getUserInfo().id) != -1)) {
                chatMessage.setExtra(msg.getExtra());
            }
        }
        saveCMtoRecent(chatMessage);

//        saveCMtoRecent(list.get(list.size() - 1));
//        saveCMtoRecent(list);
        for (int i = 0; i < list.size(); i++) {
            ChatMessage cm = list.get(i);
//            boolean shield = false;
//            if (users.size() > 0) {
//                for (User info : users) {
//                    if (cm.getFromuser().equals(info.userId) && !(cm.getExtra().indexOf(getUserInfo().id) != -1)) {
//                        shield = true;
//                        break;
//                    } else {
//                        shield = false;
//                    }
//                }
//            }
            if (null != cm) {
                // 依次保存，通知，广播
//                save(cm);
//                if (!shield) {
//                    notice(cm);
//                }
                sendBroadCast(cm);
            }
        }

    }

    private static final int NOTIFY_NEW_MSG = 90;

    NotificationManager notificationManager;

    // 将消息提示到通知栏
    private void notice(ChatMessage cm) {
        if (!CommonUtil.isForeground(context, "com.queke.im.activity.IMChatActivity")) {
            if (cm.getContentType().equals(ChatMessage.CHAT_CONTENT_TYPE_OPEN_RED)) {
                return;
            }
            Class aClass = null;
            try {
                if (cm.getType().equals(Constants.FRAGMENT_FRIEND) || cm.getType().equals(Constants.FRAGMENT_GROUP)) {
                    aClass = Class.forName("com.quakoo.im.activity.IMChatActivity");
                } else if (cm.getType().equals(Constants.FRAGMENT_NOTICE)) {
                    aClass = Class.forName("com.queke.im.activity.ImNoticeActivity");
                } else {
                    aClass = Class.forName("com.queke.im.MainActivity");
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            Intent intent = new Intent(context, aClass);
            Bundle bundle = new Bundle();
            if (cm.getType().equals(Constants.FRAGMENT_FRIEND)) {
                bundle.putString("chat_type", Constants.FRAGMENT_FRIEND);
            } else if (cm.getType().equals(Constants.FRAGMENT_GROUP)) {
                bundle.putString("chat_type", Constants.FRAGMENT_GROUP);
            } else if (cm.getType().equals(Constants.FRAGMENT_NOTICE)) {
                bundle.putString("chat_type", Constants.FRAGMENT_NOTICE);
            }
            bundle.putString("master", getUserInfo().getId());
            bundle.putString("avator", getUserInfo().getIcon());
            bundle.putString("chat_friend", cm.getFromuser());
            bundle.putString("chat_friendNick", cm.getFromusernick());
            bundle.putString("chat_friendicon", cm.getAvatar());
            bundle.putString("msgTime", cm.getTime());
            bundle.putString("msgContent", cm.getContent());
            UserInfo ChatFriend = new UserInfo();
            ChatFriend.setId(cm.getFromuser());
            ChatFriend.setName(cm.getFromusernick());
            ChatFriend.setIcon(cm.getAvatar());
            intent.putExtra("userInfo", ChatFriend); //获取聊天对象
            intent.putExtras(bundle);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, UUID.randomUUID().hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

            notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            Notification notification = null;
            Uri sound = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.strong_message);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel mChannel = new NotificationChannel(context.getPackageName() + ".chat", context.getResources().getString(R.string.app_new_msg), NotificationManager.IMPORTANCE_HIGH);
                mChannel.setSound(sound, Notification.AUDIO_ATTRIBUTES_DEFAULT);
                notificationManager.createNotificationChannel(mChannel);
                notification = new Notification.Builder(context)
                        .setChannelId(context.getPackageName() + ".chat")
                        .setTicker(cm.getFromusernick() + ":" + cm.getContentDescr())
                        .setContentTitle("有新消息")
                        .setContentText(cm.getFromusernick() + ":" + cm.getContentDescr())
                        .setSmallIcon(R.drawable.icon_app)
//                        .setDefaults(NotificationCompat.DEFAULT_SOUND)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent)
                        .build();
            } else {
                NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
                builder.setTicker(cm.getFromusernick() + ":" + cm.getContentDescr());
                builder.setContentTitle("有新消息");
                builder.setContentText(cm.getFromusernick() + ":" + cm.getContentDescr());
                builder.setSmallIcon(R.drawable.icon_app);
//                builder.setDefaults(NotificationCompat.DEFAULT_SOUND);
                builder.setAutoCancel(true);
                builder.setContentIntent(pendingIntent);
                builder.setSound(sound);
                builder.setLights(Color.GREEN, 1000, 1000);
                notification = builder.build();
            }
            notificationManager.notify(NOTIFY_NEW_MSG, notification);
        }
    }

    public void notificationCancelAll() {
        if (null != notificationManager) {
            notificationManager.cancelAll();
        }
    }

    // 将好友申请信息更新到最近消息列表
    public void saveFriendsNoticetoRecent(ChatMessage cm) {
//        sendBroadCast(cm);
        ContentValues values = new ContentValues();
        values.put("username", cm.getMaster());
        values.put("msg_from", cm.getFromuser());
        values.put("msg_fromnick", cm.getFromusernick());
        values.put("msg_fromremark", cm.getFromuserremark());
        values.put("msg_to", cm.getTouser());
        values.put("msg_tonick", cm.getTousernick());
        values.put("msg_toremark", cm.getTouserremark());
        values.put("msg_avatar", cm.getAvatar());
        values.put("msg_nickname", cm.getNickname());
        values.put("msg_userremark", cm.getUserremark());
        values.put("msg_username", cm.getUsername());
        values.put("msg_usericon", cm.getUserIcon());
        values.put("msg_content", cm.getContent());
        values.put("msg_type", cm.getType());
        values.put("msg_time", cm.getTime());
        values.put("msg_serveId", cm.getServeId());
        values.put("msg_clientId", cm.getClientId());
        values.put("msg_media_url", cm.getUrl());
        values.put("msg_audio_state", cm.getAudioState());
        values.put("msg_content_type", cm.getContentType());
        values.put("msg_destroy", cm.getDestroy());
        values.put("msg_media_extra", cm.getExtra());
        values.put("msg_media_thumbnail", cm.getThumbnailUrl());
        values.put("msg_audio_state", cm.getAudioState());
        values.put("msg_progress", cm.getProgress());
        values.put("msg_prompt", cm.getPrompt());
        values.put("msg_unread_count", "1");
        ChatDBHelper helper = new ChatDBHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        // 将最近聊天数据更新至数据库
        Cursor cur = db.rawQuery("SELECT _id FROM chat_recent WHERE username='" + cm.getMaster() + "' " +
                "AND msg_type='" + cm.getType() + "' ", null);
        if (cur.moveToNext()) {
            db.update("chat_recent", values, "_id=?", new String[]{cur.getInt(0) + ""});
        } else {
            db.insert("chat_recent", null, values);
        }
        cur.close();
        db.close();
    }

    //更新单条发送成功的聊天消息
    public void updateRecallSuccess(ChatMessage msg) {
        ChatDBHelper helper = new ChatDBHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("msg_content", msg.getContent());
        values.put("msg_content_type", msg.getContentType());
        Cursor cur = db.rawQuery("SELECT _id FROM chat_history WHERE username='" + msg.getMaster() + "' " +
                "AND msg_type='" + msg.getType() + "' " +
                "AND msg_serveId='" + msg.getServeId() + "' " +
                "AND ((msg_from='" + msg.getFromuser() + "' AND msg_to='" + msg.getTouser() + "') OR (msg_from='" + msg.getTouser() + "' AND msg_to='" + msg.getFromuser() + "'))", null);
        if (cur.moveToNext()) {
            db.update("chat_history", values, "_id=?", new String[]{cur.getInt(0) + ""});
        }
        cur.close();
        db.close();

        // 将消息更新至最近聊天列表
        updateRecent(msg);
    }

    //更新单条发送回执成功的聊天消息
    public void updateSendServiceSuccess(String master, String clientId) {
        ChatDBHelper helper = new ChatDBHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("msg_progress", "false");
        values.put("msg_prompt", "false");
        values.put("msg_audio_state", "read");
        values.put("msg_send_state", Constants.SEND_CHATMESSAGE_SUCCESS);

        Cursor cur = db.rawQuery("SELECT * FROM (SELECT * FROM chat_history WHERE username='" + master + "' AND msg_clientId='" + clientId + "')", null);

        if (cur.moveToNext()) {
            db.update("chat_history", values, "_id=?", new String[]{cur.getInt(0) + ""});
            // 将消息更新至最近聊天列表
            updateRecent(cursorToChatMessage(cur));
            sendBroadcast(cursorToChatMessage(cur), Constants.SEND_CHATMESSAGE_SUCCESS);
        }
        cur.close();
        db.close();
    }

    //更新单条发送成功的聊天消息
    public void updateSendSuccess(ChatMessage msg) {
        ChatDBHelper helper = new ChatDBHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("msg_nickname", msg.getNickname());
        values.put("msg_userremark", msg.getUserremark());
        values.put("msg_usericon", msg.getUserIcon());
        values.put("msg_avatar", msg.getAvatar());
        values.put("msg_serveId", msg.getServeId());
        values.put("msg_media_url", msg.getUrl());
        values.put("msg_progress", "false");
        values.put("msg_prompt", "false");
        values.put("msg_audio_state", "read");
        values.put("msg_destroy", msg.getDestroy());
        values.put("msg_send_state", msg.getSendMsgState());
        values.put("msg_media_extra", msg.getExtra());
        values.put("msg_time", msg.getTime());
        Cursor cur = db.rawQuery("SELECT _id FROM chat_history WHERE username='" + msg.getMaster() + "' " +
                "AND msg_type='" + msg.getType() + "' " +
                "AND msg_clientId='" + msg.getClientId() + "' " +
                "AND ((msg_from='" + msg.getFromuser() + "' AND msg_to='" + msg.getTouser() + "') OR (msg_from='" + msg.getTouser() + "' AND msg_to='" + msg.getFromuser() + "'))", null);
        if (cur.moveToNext()) {
            db.update("chat_history", values, "_id=?", new String[]{cur.getInt(0) + ""});
        }
        cur.close();
        db.close();

        sendBroadcast(msg, Constants.SEND_CHATMESSAGE_SUCCESS);
        // 将消息更新至最近聊天列表
        updateRecent(msg);
    }

    // 更新回执信息到最近聊天数据库
    public void updateRecent(ChatMessage cm) {
        ContentValues values = new ContentValues();
        values.put("msg_serveId", cm.getServeId());
        values.put("msg_content_type", cm.getContentType());
        values.put("msg_content", cm.getContent());
        values.put("msg_avatar", cm.getAvatar());
        values.put("msg_prompt", "false");
        values.put("msg_send_state", cm.getSendMsgState());

        ChatDBHelper helper = new ChatDBHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        if (cm.getContentType().equals(ChatMessage.CHAT_CONTENT_TYPE_RECALLMSG)) {
            // 将最近聊天数据更新至数据库
            Cursor cur = db.rawQuery("SELECT _id FROM chat_recent WHERE username='" + cm.getMaster() + "' " +
                    "AND msg_type='" + cm.getType() + "' " +
                    "AND msg_serveId='" + cm.getServeId() + "' " +
                    "AND ((msg_from='" + cm.getFromuser() + "' AND msg_to='" + cm.getTouser() + "') OR (msg_from='" + cm.getTouser() + "' AND msg_to='" + cm.getFromuser() + "'))", null);
            if (cur.moveToNext()) {
                db.update("chat_recent", values, "_id=?", new String[]{cur.getInt(0) + ""});
            }
            cur.close();
        } else {
            // 将最近聊天数据更新至数据库
            Cursor cur = db.rawQuery("SELECT _id FROM chat_recent WHERE username='" + cm.getMaster() + "' " +
                    "AND msg_type='" + cm.getType() + "' " +
                    "AND msg_clientId='" + cm.getClientId() + "' " +
                    "AND ((msg_from='" + cm.getFromuser() + "' AND msg_to='" + cm.getTouser() + "') OR (msg_from='" + cm.getTouser() + "' AND msg_to='" + cm.getFromuser() + "'))", null);
            if (cur.moveToNext()) {
                db.update("chat_recent", values, "_id=?", new String[]{cur.getInt(0) + ""});
            }
            cur.close();
        }
        db.close();
    }

    //更新单条发送失败的聊天消息
    public void updateSendFail(ChatMessage msg) {
        ChatDBHelper helper = new ChatDBHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("msg_media_url", msg.getUrl());
        values.put("msg_progress", "false");
        values.put("msg_prompt", "true");
        values.put("msg_send_state", msg.getSendMsgState());
        Cursor cur = db.rawQuery("SELECT _id FROM chat_history WHERE username='" + msg.getMaster() + "' " +
                "AND msg_type='" + msg.getType() + "' " +
                "AND msg_clientId='" + msg.getClientId() + "' " +
                "AND ((msg_from='" + msg.getFromuser() + "' AND msg_to='" + msg.getTouser() + "') OR (msg_from='" + msg.getTouser() + "' AND msg_to='" + msg.getFromuser() + "'))", null);
        if (cur.moveToNext()) {
            db.update("chat_history", values, "_id=?", new String[]{cur.getInt(0) + ""});
        }

        cur = db.rawQuery("SELECT _id FROM chat_recent WHERE username='" + msg.getMaster() + "' " +
                "AND msg_type='" + msg.getType() + "' " +
                "AND msg_clientId='" + msg.getClientId() + "' " +
                "AND ((msg_from='" + msg.getFromuser() + "' AND msg_to='" + msg.getTouser() + "') OR (msg_from='" + msg.getTouser() + "' AND msg_to='" + msg.getFromuser() + "'))", null);
        if (cur.moveToNext()) {
            db.update("chat_recent", values, "_id=?", new String[]{cur.getInt(0) + ""});
        }

        cur.close();
        db.close();
    }

    //更新单条重新发送聊天消息
    public void updateResSend(ChatMessage msg) {
        ChatDBHelper helper = new ChatDBHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("msg_media_url", msg.getUrl());
        values.put("msg_progress", "false");
        values.put("msg_prompt", "true");
        values.put("msg_send_state", msg.getSendMsgState());
        values.put("msg_content", msg.getContent());
        values.put("msg_time", msg.getTime());
        values.put("msg_type", msg.getType());
        values.put("msg_content_type", msg.getContentType());
        values.put("msg_clientId", msg.getClientId());
        Cursor cur = db.rawQuery("SELECT _id FROM chat_history WHERE username='" + msg.getMaster() + "' " +
                "AND msg_type='" + msg.getType() + "' " +
                "AND msg_clientId='" + msg.getClientId() + "' " +
                "AND ((msg_from='" + msg.getFromuser() + "' AND msg_to='" + msg.getTouser() + "') OR (msg_from='" + msg.getTouser() + "' AND msg_to='" + msg.getFromuser() + "'))", null);
        if (cur.moveToNext()) {
            db.update("chat_history", values, "_id=?", new String[]{cur.getInt(0) + ""});
        }

        cur = db.rawQuery("SELECT _id FROM chat_recent WHERE username='" + msg.getMaster() + "' " +
                "AND msg_type='" + msg.getType() + "' " +
                "AND ((msg_from='" + msg.getFromuser() + "' AND msg_to='" + msg.getTouser() + "') OR (msg_from='" + msg.getTouser() + "' AND msg_to='" + msg.getFromuser() + "'))", null);
        if (cur.moveToNext()) {
            db.update("chat_recent", values, "_id=?", new String[]{cur.getInt(0) + ""});
        }

        cur.close();
        db.close();
    }

    //更新单条多媒体消息url聊天消息
    public void updateMediaSuccess(ChatMessage msg) {
        ChatDBHelper helper = new ChatDBHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("msg_media_url", msg.getUrl());
        values.put("msg_prompt", "false");
        Cursor cur = db.rawQuery("SELECT _id FROM chat_history WHERE username='" + msg.getMaster() + "' " +
                "AND msg_type='" + msg.getType() + "' " +
                "AND msg_clientId='" + msg.getClientId() + "' " +
                "AND ((msg_from='" + msg.getFromuser() + "' AND msg_to='" + msg.getTouser() + "') OR (msg_from='" + msg.getTouser() + "' AND msg_to='" + msg.getFromuser() + "'))", null);
        if (cur.moveToNext()) {
            db.update("chat_history", values, "_id=?", new String[]{cur.getInt(0) + ""});
        }
        cur.close();
        db.close();
    }

    //更新单条多媒体消息url聊天消息
    public void updateMediaFail(ChatMessage msg) {
        ChatDBHelper helper = new ChatDBHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("msg_media_url", msg.getUrl());
        values.put("msg_progress", "false");
        values.put("msg_prompt", "true");
        values.put("msg_send_state", msg.getSendMsgState());
        Cursor cur = db.rawQuery("SELECT _id FROM chat_history WHERE username='" + msg.getMaster() + "' " +
                "AND msg_type='" + msg.getType() + "' " +
                "AND msg_clientId='" + msg.getClientId() + "' " +
                "AND ((msg_from='" + msg.getFromuser() + "' AND msg_to='" + msg.getTouser() + "') OR (msg_from='" + msg.getTouser() + "' AND msg_to='" + msg.getFromuser() + "'))", null);
        if (cur.moveToNext()) {
            db.update("chat_history", values, "_id=?", new String[]{cur.getInt(0) + ""});
        }

        cur = db.rawQuery("SELECT _id FROM chat_recent WHERE username='" + msg.getMaster() + "' " +
                "AND msg_type='" + msg.getType() + "' " +
                "AND msg_clientId='" + msg.getClientId() + "' " +
                "AND ((msg_from='" + msg.getFromuser() + "' AND msg_to='" + msg.getTouser() + "') OR (msg_from='" + msg.getTouser() + "' AND msg_to='" + msg.getFromuser() + "'))", null);
        if (cur.moveToNext()) {
            db.update("chat_recent", values, "_id=?", new String[]{cur.getInt(0) + ""});
        }

        cur.close();
        db.close();
    }

    //语音状态更新
    public void updateAudioState(ChatMessage msg) {
        ChatDBHelper helper = new ChatDBHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("msg_audio_state", "read");
        Cursor cur = db.rawQuery("SELECT _id FROM chat_history WHERE username='" + msg.getMaster() + "' " +
                "AND msg_type='" + msg.getType() + "' " +
                "AND msg_clientId='" + msg.getClientId() + "' " +
                "AND ((msg_from='" + msg.getFromuser() + "' AND msg_to='" + msg.getTouser() + "') OR (msg_from='" + msg.getTouser() + "' AND msg_to='" + msg.getFromuser() + "'))", null);
        if (cur.moveToNext()) {
            db.update("chat_history", values, "_id=?", new String[]{cur.getInt(0) + ""});
        }
        cur.close();
        db.close();
    }

    //语音状态更新
    public void updateThumbnail(ChatMessage msg, String path) {
        ChatDBHelper helper = new ChatDBHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("msg_media_thumbnail", path);
        Cursor cur = db.rawQuery("SELECT _id FROM chat_history WHERE username='" + msg.getMaster() + "' " +
                "AND msg_type='" + msg.getType() + "' " +
                "AND msg_clientId='" + msg.getClientId() + "' " +
                "AND ((msg_from='" + msg.getFromuser() + "' AND msg_to='" + msg.getTouser() + "') OR (msg_from='" + msg.getTouser() + "' AND msg_to='" + msg.getFromuser() + "'))", null);
        if (cur.moveToNext()) {
            db.update("chat_history", values, "_id=?", new String[]{cur.getInt(0) + ""});
        }
        cur.close();
        db.close();
    }

    //加群审核状态更新
    public void updateCheckState(ChatMessage msg) {
        ChatDBHelper helper = new ChatDBHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("msg_media_extra", msg.getExtra());
        Cursor cur = db.rawQuery("SELECT _id FROM chat_history WHERE username='" + msg.getMaster() + "' " +
                "AND msg_type='" + msg.getType() + "' " +
                "AND msg_clientId='" + msg.getClientId() + "' " +
                "AND ((msg_from='" + msg.getFromuser() + "' AND msg_to='" + msg.getTouser() + "') OR (msg_from='" + msg.getTouser() + "' AND msg_to='" + msg.getFromuser() + "'))", null);
        if (cur.moveToNext()) {
            db.update("chat_history", values, "_id=?", new String[]{cur.getInt(0) + ""});
        }
        cur.close();
        db.close();
    }

    //更新聊天收藏标签
    public void updateChatCollecLabel(ChatMessage msg) {
        ChatDBHelper helper = new ChatDBHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("msg_label", msg.getLabel());
        Cursor cur = db.rawQuery("SELECT _id FROM chat_collect WHERE username='" + msg.getMaster() + "' " +
                "AND msg_type='" + msg.getType() + "' " +
                "AND msg_clientId='" + msg.getClientId() + "' " +
                "AND ((msg_from='" + msg.getFromuser() + "' AND msg_to='" + msg.getTouser() + "') OR (msg_from='" + msg.getTouser() + "' AND msg_to='" + msg.getFromuser() + "'))", null);
        if (cur.moveToNext()) {
            db.update("chat_collect", values, "_id=?", new String[]{cur.getInt(0) + ""});
        }
        cur.close();
        db.close();
    }

    // 以广播的方式将消息发送出去
    public void sendBroadCast(ChatMessage cm) {
        Log.i(TAG, "进入SEND_CHATMESSAGE_SUCCESS方法"); //6-17修改将以前发送的方式换成EventBus专门给红包使用
        UniteUpdateDataModel model = new UniteUpdateDataModel();
        model.setKey(Constants.CHAT_NEW_MESSAGE);
        model.setValue(model.toJson(cm));
        EventBus.getDefault().postSticky(model);

    }

    //删除群组和好友聊天记录
    public void deleteChat(String master, ChatMessage cm) {
        ChatDBHelper helper = new ChatDBHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("DELETE FROM chat_history WHERE username='" + master + "' " +
                "AND msg_type='" + cm.getType() + "' " +
                "AND ((msg_from='" + cm.getFromuser() + "' AND msg_to='" + cm.getTouser() + "') OR (msg_from='" + cm.getTouser() + "' AND msg_to='" + cm.getFromuser() + "'))");

        ContentValues values = new ContentValues();
//        values.put("msg_from", cm.getFromuser());
//        values.put("msg_nickname", "");
        values.put("msg_content", "");
        values.put("msg_content_type", ChatMessage.CHAT_CONTENT_TYPE_DELETE);
        Cursor cur = db.rawQuery("SELECT _id FROM chat_recent WHERE username='" + master + "' " +
                "AND msg_type='" + cm.getType() + "' " +
                "AND ((msg_from='" + cm.getFromuser() + "' AND msg_to='" + cm.getTouser() + "') OR (msg_from='" + cm.getTouser() + "' AND msg_to='" + cm.getFromuser() + "'))", null);
        if (cur.moveToNext()) {
            db.update("chat_recent", values, "_id=?", new String[]{cur.getInt(0) + ""});
        }
        cur.close();
        db.close();

    }

    // 更改群名信息更新到最近消息列表
    public void updateGroupName(String master, ChatMessage cm) {
        ContentValues values = new ContentValues();
        values.put("msg_fromnick", cm.getFromusernick());
        ChatDBHelper helper = new ChatDBHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        // 将最近聊天数据更新至数据库
        Cursor cur = db.rawQuery("SELECT _id FROM chat_recent WHERE username='" + master + "' " +
                "AND msg_from='" + cm.getFromuser() + "' " +
                "AND msg_type='" + cm.getType() + "' ", null);
        if (cur.moveToNext()) {
            db.update("chat_recent", values, "_id=?", new String[]{cur.getInt(0) + ""});
        } else {
            db.insert("chat_recent", null, values);
        }
        cur.close();
        db.close();
    }

    // 更改备注名信息更新到最近消息列表
    public void updateUserRemark(String master, ChatMessage cm) {
        ContentValues values = new ContentValues();
        values.put("msg_fromnick", cm.getFromusernick());
        ChatDBHelper helper = new ChatDBHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        // 将最近聊天数据更新至数据库
        Cursor cur = db.rawQuery("SELECT _id FROM chat_recent WHERE username='" + master + "' " +
                "AND msg_from='" + cm.getFromuser() + "' " +
                "AND msg_type='" + cm.getType() + "' ", null);
        if (cur.moveToNext()) {
            db.update("chat_recent", values, "_id=?", new String[]{cur.getInt(0) + ""});
        } else {
            db.insert("chat_recent", null, values);
        }
        cur.close();
        db.close();
    }

    //删除群组和好友,聊天记录也删除
    public void deleteChatFriend(String master, ChatMessage cm) {
        ChatDBHelper helper = new ChatDBHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("DELETE FROM chat_recent WHERE username='" + master + "' " +
                "AND msg_type='" + cm.getType() + "' " +
                "AND ((msg_from='" + cm.getFromuser() + "' AND msg_to='" + cm.getTouser() + "') OR (msg_from='" + cm.getTouser() + "' AND msg_to='" + cm.getFromuser() + "'))");
        // 1月16号版本要求删除最近聊天的同时删除聊天记录
        db.execSQL("DELETE FROM chat_history WHERE username='" + master + "' " +
                "AND ((msg_from='" + cm.getFromuser() + "' AND msg_to='" + cm.getTouser() + "') OR (msg_from='" + cm.getTouser() + "' AND msg_to='" + cm.getFromuser() + "'))");
        db.close();
    }

    //删除最近聊天记录
    public void deleteRecentChat(String master, ChatMessage cm) {
        ChatDBHelper helper = new ChatDBHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("DELETE FROM chat_recent WHERE username='" + master + "' " +
                        "AND msg_type='" + cm.getType() + "' " +
                        "AND msg_content=? " +
                        "AND msg_time='" + cm.getTime() + "' " +
                        "AND ((msg_from='" + cm.getFromuser() + "' AND msg_to='" + cm.getTouser() + "') OR (msg_from='" + cm.getTouser() + "' AND msg_to='" + cm.getFromuser() + "'))",
                new Object[]{cm.getContent()});
        // 1月16号版本要求删除最近聊天的同时删除聊天记录
        db.execSQL("DELETE FROM chat_history WHERE username='" + master + "' " +
                "AND msg_type='" + cm.getType() + "' " +
                "AND ((msg_from='" + cm.getFromuser() + "' AND msg_to='" + cm.getTouser() + "') OR (msg_from='" + cm.getTouser() + "' AND msg_to='" + cm.getFromuser() + "'))");
        db.close();
    }

    // 未读聊天消息数改为0
    public void updateRecentChatUnread(String master, ChatMessage cm) {
        ChatDBHelper helper = new ChatDBHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("UPDATE chat_recent SET msg_unread_count=0 WHERE username='" + master + "' " +
                        "AND msg_type='" + cm.getType() + "' " +
                        "AND msg_content=? " +
                        "AND msg_time='" + cm.getTime() + "' " +
                        "AND ((msg_from='" + cm.getFromuser() + "' AND msg_to='" + cm.getTouser() + "') OR (msg_from='" + cm.getTouser() + "' AND msg_to='" + cm.getFromuser() + "'))",
                new Object[]{cm.getContent()});
        db.close();
    }

    // 将@好友消息改为已读
    public void updateHintUserToRead(String master, ChatMessage msg) {
        if (msg==null){
            return;
        }
        ChatDBHelper helper = new ChatDBHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("msg_hint_state", "read");
        Log.i(TAG, "updateHintUserToRead: "+msg.toString());
        if (msg.getType() == null) {
            Log.i(TAG, "数据:" + msg.toString());
        }
        Cursor cur = db.rawQuery("SELECT _id FROM chat_recent WHERE username='" + master + "' " +
                "AND msg_type='" + msg.getType() + "' " +
                "AND msg_clientId='" + msg.getClientId() + "' " +
                "AND ((msg_from='" + msg.getFromuser() + "' AND msg_to='" + msg.getTouser() + "') OR (msg_from='" + msg.getTouser() + "' AND msg_to='" + msg.getFromuser() + "'))", null);
        if (cur.moveToNext()) {
            db.update("chat_recent", values, "_id=?", new String[]{cur.getInt(0) + ""});
        }
        cur.close();
        db.close();
    }

    // 打开红包更新状态
    public void updateRedOpenState(String master, ChatMessage msg) {
        msg.setRedOpenState("true");
        ChatDBHelper helper = new ChatDBHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("msg_redopen_state", "true");
        values.put("msg_receive_state", msg.getRedReceiveState());
        Cursor cur = db.rawQuery("SELECT _id FROM chat_history WHERE username='" + master + "' " +
                "AND msg_type='" + msg.getType() + "' " +
                "AND msg_clientId='" + msg.getClientId() + "' " +
                "AND ((msg_from='" + msg.getFromuser() + "' AND msg_to='" + msg.getTouser() + "') OR (msg_from='" + msg.getTouser() + "' AND msg_to='" + msg.getFromuser() + "'))", null);
        if (cur.moveToNext()) {
            db.update("chat_history", values, "_id=?", new String[]{cur.getInt(0) + ""});
        }
        cur.close();
        db.close();
    }

    // 未读通知消息数改为0
    public void updateRecentNoticeUnread(ChatMessage cm) {
        ContentValues values = new ContentValues();
        values.put("msg_unread_count", "0");
        ChatDBHelper helper = new ChatDBHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        // 将最近聊天数据更新至数据库
        Cursor cur = db.rawQuery("SELECT _id FROM chat_recent WHERE username='" + cm.getMaster() + "' " +
                "AND msg_type='" + cm.getType() + "' ", null);
        if (cur.moveToNext()) {
            db.update("chat_recent", values, "_id=?", new String[]{cur.getInt(0) + ""});
        } else {
            db.insert("chat_recent", null, values);
        }
        cur.close();
        db.close();
    }

    //阅后即焚
    public void destroyChat(String master, ChatMessage cm) {
        ChatDBHelper helper = new ChatDBHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();
//        db.execSQL("DELETE FROM chat_recent WHERE username='" + master + "' " +
//                "AND ((msg_from='" + cm.getFromuser() + "' AND msg_to='" + cm.getTouser() + "') OR (msg_from='" + cm.getTouser() + "' AND msg_to='" + cm.getFromuser() + "'))");

        ContentValues values = new ContentValues();
        values.put("msg_content", "");
        values.put("msg_content_type", ChatMessage.CHAT_CONTENT_TYPE_HIDE);
        Cursor cur = db.rawQuery("SELECT _id FROM chat_recent WHERE username='" + master + "' " +
                "AND ((msg_from='" + cm.getFromuser() + "' AND msg_to='" + cm.getTouser() + "') OR (msg_from='" + cm.getTouser() + "' AND msg_to='" + cm.getFromuser() + "'))", null);
        if (cur.moveToNext()) {
            db.update("chat_recent", values, "_id=?", new String[]{cur.getInt(0) + ""});
            ChatMessage chatMessage = getOneChatRecent(master,cur.getInt(0) + "");
//            chatMessage.setSettingEntity(ChatSettingManager.getInstance().query(master,chatMessage.getTouser()));
            UniteUpdateDataModel model = new UniteUpdateDataModel();
            model.setKey(ChatMessage.CHAT_CONTENT_TYPE_HIDE);
            model.setValue(model.toJson(chatMessage));
            EventBus.getDefault().postSticky(model);
        }
        db.execSQL("DELETE FROM chat_history WHERE username='" + master + "' " +
                "AND msg_destroy='" + cm.getDestroy() + "' " +
                "AND ((msg_from='" + cm.getFromuser() + "' AND msg_to='" + cm.getTouser() + "') OR (msg_from='" + cm.getTouser() + "' AND msg_to='" + cm.getFromuser() + "'))");
        db.close();
        cur.close();
    }

    //取最新十条消息
    public List<ChatMessage> getNewChatMsg10(String masterId) {
        List<ChatMessage> chatMsgs = new ArrayList<>();
        try {
            ChatDBHelper helper = new ChatDBHelper(context);
            SQLiteDatabase db = helper.getReadableDatabase();
            Cursor cur = db.rawQuery("SELECT * FROM (SELECT * FROM chat_history WHERE username='" + masterId + "' ORDER BY _id DESC LIMIT 0,10) ORDER BY msg_time", null);
            while (cur.moveToNext()) {
                ChatMessage cm = new ChatMessage();
                String master = cur.getString(cur.getColumnIndex("username"));
                String from = cur.getString(cur.getColumnIndex("msg_from"));
                String fromnick = cur.getString(cur.getColumnIndex("msg_fromnick"));
                String to = cur.getString(cur.getColumnIndex("msg_to"));
                String tonick = cur.getString(cur.getColumnIndex("msg_tonick"));
                String content = cur.getString(cur.getColumnIndex("msg_content"));
                String time = cur.getString(cur.getColumnIndex("msg_time"));
                String serveId = cur.getString(cur.getColumnIndex("msg_serveId"));
                String clientId = cur.getString(cur.getColumnIndex("msg_clientId"));
                String nickname = cur.getString(cur.getColumnIndex("msg_nickname"));
                String username = cur.getString(cur.getColumnIndex("msg_username"));
                String usericon = cur.getString(cur.getColumnIndex("msg_usericon"));
                String type = cur.getString(cur.getColumnIndex("msg_type"));
                String avatar = cur.getString(cur.getColumnIndex("msg_avatar"));
                String url = cur.getString(cur.getColumnIndex("msg_media_url"));
                String extra = cur.getString(cur.getColumnIndex("msg_media_extra"));
                String thumbnail = cur.getString(cur.getColumnIndex("msg_media_thumbnail"));
                String audioState = cur.getString(cur.getColumnIndex("msg_audio_state"));
                String progress = cur.getString(cur.getColumnIndex("msg_progress"));
                String prompt = cur.getString(cur.getColumnIndex("msg_prompt"));
                String contentType = cur.getString(cur.getColumnIndex("msg_content_type"));
                String destroy = cur.getString(cur.getColumnIndex("msg_destroy"));

                String fromremark = cur.getString(cur.getColumnIndex("msg_fromremark"));
                String toremark = cur.getString(cur.getColumnIndex("msg_toremark"));
                String userremark = cur.getString(cur.getColumnIndex("msg_userremark"));
                String  redreceivestate= cur.getString(cur.getColumnIndex("msg_receive_state"));
                cm.setRedReceiveState(redreceivestate);
                cm.setFromuserremark(fromremark);
                cm.setTouserremark(toremark);
                cm.setUserremark(userremark);

                cm.setMaster(master);
                cm.setNickname(nickname);
                cm.setUsername(username);
                cm.setUserIcon(usericon);
                cm.setType(type);
                cm.setAvatar(avatar);
                cm.setFromuser(from);
                cm.setFromusernick(fromnick);
                cm.setTouser(to);
                cm.setTousernick(tonick);
                cm.setContent(content);
                cm.setTime(time);
                cm.setServeId(serveId);
                cm.setClientId(clientId);
                cm.setUrl(url);
                cm.setExtra(extra);
                cm.setThumbnailUrl(thumbnail);
                cm.setAudioState(audioState);
                cm.setProgress(progress);
                cm.setPrompt(prompt);
                cm.setContentType(contentType);
                cm.setDestroy(destroy);
                chatMsgs.add(cm);
            }
            cur.close();
            db.close();
        } catch (IllegalStateException e) {
            e.getMessage();
        }
        return chatMsgs;
    }

    //从数据库加到消息实例中
    public ChatMessage setCursorMessage(Cursor cur, ChatMessage cm) {
        String from = cur.getString(cur.getColumnIndex("msg_from"));
        String fromnick = cur.getString(cur.getColumnIndex("msg_fromnick"));
        String to = cur.getString(cur.getColumnIndex("msg_to"));
        String tonick = cur.getString(cur.getColumnIndex("msg_tonick"));
        String content = cur.getString(cur.getColumnIndex("msg_content"));
        String time = cur.getString(cur.getColumnIndex("msg_time"));
        String clientId = cur.getString(cur.getColumnIndex("msg_clientId"));
        String nickname = cur.getString(cur.getColumnIndex("msg_nickname"));
        String username = cur.getString(cur.getColumnIndex("msg_username"));
        String usericon = cur.getString(cur.getColumnIndex("msg_usericon"));
        String avatar = cur.getString(cur.getColumnIndex("msg_avatar"));
        String url = cur.getString(cur.getColumnIndex("msg_media_url"));
        String extra = cur.getString(cur.getColumnIndex("msg_media_extra"));
        String thumbnail = cur.getString(cur.getColumnIndex("msg_media_thumbnail"));
        String audioState = cur.getString(cur.getColumnIndex("msg_audio_state"));
        String progress = cur.getString(cur.getColumnIndex("msg_progress"));
        String prompt = cur.getString(cur.getColumnIndex("msg_prompt"));
        String contentType = cur.getString(cur.getColumnIndex("msg_content_type"));
        String destroy = cur.getString(cur.getColumnIndex("msg_destroy"));
        String label = cur.getString(cur.getColumnIndex("msg_label"));
        String fromremark = cur.getString(cur.getColumnIndex("msg_fromremark"));
        String toremark = cur.getString(cur.getColumnIndex("msg_toremark"));
        String userremark = cur.getString(cur.getColumnIndex("msg_userremark"));

        String receiveState = cur.getString(cur.getColumnIndex("msg_receive_state"));
        cm.setRedReceiveState(receiveState);

        cm.setFromuserremark(fromremark);
        cm.setTouserremark(toremark);
        cm.setUserremark(userremark);

        cm.setUsername(username);
        cm.setNickname(nickname);
        cm.setUserIcon(usericon);
        cm.setAvatar(avatar);
        cm.setFromuser(from);
        cm.setFromusernick(fromnick);
        cm.setTouser(to);
        cm.setTousernick(tonick);
        cm.setContent(content);
        cm.setTime(time);
        cm.setClientId(clientId);
        cm.setUrl(url);
        cm.setExtra(extra);
        cm.setThumbnailUrl(thumbnail);
        cm.setAudioState(audioState);
        cm.setProgress(progress);
        cm.setPrompt(prompt);
        cm.setContentType(contentType);
        cm.setDestroy(destroy);
        cm.setLabel(label);
        return cm;
    }

    public UserInfo getUserInfo() {
        UserInfo userinfo = (UserInfo) MsgCache.get(BaseApplication.getInstance()).getAsObject(Constants.USER_INFO);
        if (!CommonUtil.isBlank(userinfo)) {
            return userinfo;
        }
        return new UserInfo();
    }

    public void delete(String master, ChatMessage cm, List<ChatMessage> list, boolean end) {
        if (null != cm) {
            ChatDBHelper helper = new ChatDBHelper(context);
            SQLiteDatabase db = helper.getWritableDatabase();
            db.execSQL("DELETE FROM chat_history WHERE username='" + master + "' " +
                    "AND msg_type='" + cm.getType() + "' " +
                    "AND msg_content='" + cm.getContent() + "' " +
                    "AND msg_clientId='" + cm.getClientId() + "' " +
                    "AND ((msg_from='" + cm.getFromuser() + "' AND msg_to='" + cm.getTouser() + "') OR (msg_from='" + cm.getTouser() + "' AND msg_to='" + cm.getFromuser() + "'))");
            //更新最近聊天
            if (end && list.size() >= 2) {
                ChatMessage msg = list.get(list.size() - 2);
                ContentValues values = new ContentValues();
                values.put("msg_content", msg.getContent());
                values.put("msg_type", msg.getType());
                values.put("msg_time", msg.getTime());
                values.put("msg_serveId", msg.getServeId());
                values.put("msg_clientId", msg.getClientId());
                values.put("msg_media_url", msg.getUrl());
                values.put("msg_content_type", msg.getContentType());
                values.put("msg_media_extra", msg.getExtra());
                values.put("msg_media_thumbnail", msg.getThumbnailUrl());
                values.put("msg_progress", msg.getProgress());
                values.put("msg_prompt", msg.getPrompt());
                values.put("msg_receive_state", msg.getRedReceiveState());
                Cursor cur = db.rawQuery("SELECT _id FROM chat_recent WHERE username='" + cm.getMaster() + "' " +
                        "AND msg_type='" + cm.getType() + "' " +
                        "AND msg_clientId='" + cm.getClientId() + "' " +
                        "AND ((msg_from='" + cm.getFromuser() + "' AND msg_to='" + cm.getTouser() + "') OR (msg_from='" + cm.getTouser() + "' AND msg_to='" + cm.getFromuser() + "'))", null);
                if (cur.moveToNext()) {
                    db.update("chat_recent", values, "_id=?", new String[]{cur.getInt(0) + ""});
                }
                cur.close();
            } else if (list.size() == 1) {
                db.execSQL("DELETE FROM chat_recent WHERE username='" + master + "' " +
                                "AND msg_type='" + cm.getType() + "' " +
                                "AND msg_content=? " +
                                "AND msg_time='" + cm.getTime() + "' " +
                                "AND ((msg_from='" + cm.getFromuser() + "' AND msg_to='" + cm.getTouser() + "') OR (msg_from='" + cm.getTouser() + "' AND msg_to='" + cm.getFromuser() + "'))",
                        new Object[]{cm.getContent()});
            }
            db.close();
        }
    }

    // 将信息状态设为已读
    public void read(String master, ChatMessage cm) {
        if (null != cm) {
            ChatDBHelper helper = new ChatDBHelper(context);
            SQLiteDatabase db = helper.getWritableDatabase();
            db.execSQL("UPDATE chat_history SET msg_state='read' WHERE msg_from='" + cm.getFromuser() + "' AND msg_to='" + cm.getTouser() + "' AND msg_content=? AND msg_time='" + cm.getTime() + "' AND msg_type='" + cm.getType() + "' AND username='" + master + "'", new Object[]{cm.getContent()});
            // 未读消息数改为0
            db.execSQL("UPDATE chat_recent SET msg_unread_count=0 WHERE username='" + master + "' " +
                            "AND msg_type='" + cm.getType() + "' " +
                            "AND msg_content=? " +
                            "AND msg_time='" + cm.getTime() + "' " +
                            "AND ((msg_from='" + cm.getFromuser() + "' AND msg_to='" + cm.getTouser() + "') OR (msg_from='" + cm.getTouser() + "' AND msg_to='" + cm.getFromuser() + "'))",
                    new Object[]{cm.getContent()});
            db.close();
        }
    }

    //保存数据
    public void SaveChatMessage(ChatMessage cm) {
        ChatMessage msg = new ChatMessage();
        msg.setMaster(cm.getMaster());
        msg.setContent(cm.getContent());
        msg.setFromuser(cm.getFromuser());
        msg.setFromusernick(cm.getFromusernick());
        msg.setTouser(cm.getTouser());
        msg.setTousernick(cm.getTousernick());
        msg.setTime(cm.getTime());
        msg.setClientId(cm.getClientId());
        msg.setType(cm.getType());
        msg.setAvatar(cm.getAvatar());
        msg.setNickname(cm.getNickname());
        msg.setUsername(cm.getUsername());
        msg.setUserIcon(cm.getUserIcon());
        msg.setUrl(cm.getUrl());
        msg.setExtra(cm.getExtra());
        msg.setThumbnailUrl(cm.getThumbnailUrl());
        msg.setContentType(cm.getContentType());
        msg.setProgress(cm.getProgress());
        msg.setPrompt(cm.getPrompt());
        msg.setSendMsgState(cm.getSendMsgState());
        msg.setRedReceiveState(cm.getRedReceiveState());
//        msg.setPrompt("true");
        IMChatManager.getInstance(context).save(msg);
    }

    public synchronized boolean sendZhuanfaBroadcast(ChatMessage cm) {
        if (CommonUtil.isWorked(context, ImSocketService.IM_SERVICE_PACKAGE)) {
            Intent intent = new Intent(Constants.SEND_CHATMESSAGE);
            Bundle bundle = new Bundle();
            bundle.putSerializable("ChatMessage", cm);
            intent.putExtras(bundle);
            localBroadcastManager.sendBroadcast(intent);
            return true;
        } else {
            cm.setProgress("false");
            cm.setPrompt("true");
            cm.setSendMsgState(ChatMessage.SEND_FAIL);
            IMChatManager.getInstance(context).updateSendFail(cm);
            ImSocketService.startImService(context);
            ToastUtils.showShort(context, "未连接服务器");
            return false;
        }
    }

    //发送通知消息
    public boolean sendNoticeBroadcast(ChatMessage cm) {
        if (CommonUtil.isWorked(context, ImSocketService.IM_SERVICE_PACKAGE)) {
            Intent intent = new Intent(Constants.SEND_CHATMESSAGE);
            Bundle bundle = new Bundle();
            bundle.putSerializable("ChatMessage", cm);
            intent.putExtras(bundle);
            localBroadcastManager.sendBroadcast(intent);
            return true;
        } else {
            cm.setProgress("false");
            cm.setPrompt("true");
            cm.setSendMsgState(ChatMessage.SEND_FAIL);
            IMChatManager.getInstance(context).updateSendFail(cm);
            ImSocketService.startImService(context);
            return false;
        }
    }

    public ChatMessage pp(ChatMessage msg) {
        ChatDBHelper helper = new ChatDBHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cur = db.rawQuery("SELECT COUNT(*) FROM chat_history WHERE username='" + msg.getMaster() + "' " +
                "AND msg_type='" + msg.getType() + "' " +
                "AND msg_clientId='" + msg.getClientId() + "' " +
                "AND ((msg_from='" + msg.getFromuser() + "' AND msg_to='" + msg.getTouser() + "') OR (msg_from='" + msg.getTouser() + "' AND msg_to='" + msg.getFromuser() + "'))", null);
        ChatMessage cm = new ChatMessage();
        if (cur.moveToNext()) {
            String from = cur.getString(cur.getColumnIndex("msg_from"));
            String fromnick = cur.getString(cur.getColumnIndex("msg_fromnick"));
            String to = cur.getString(cur.getColumnIndex("msg_to"));
            String tonick = cur.getString(cur.getColumnIndex("msg_tonick"));
            String content = cur.getString(cur.getColumnIndex("msg_content"));
            String time = cur.getString(cur.getColumnIndex("msg_time"));
            String serveId = cur.getString(cur.getColumnIndex("msg_serveId"));
            String clientId = cur.getString(cur.getColumnIndex("msg_clientId"));
            String nickname = cur.getString(cur.getColumnIndex("msg_nickname"));
            String username = cur.getString(cur.getColumnIndex("msg_username"));
            String usericon = cur.getString(cur.getColumnIndex("msg_usericon"));
            String avatar = cur.getString(cur.getColumnIndex("msg_avatar"));
            String url = cur.getString(cur.getColumnIndex("msg_media_url"));
            String extra = cur.getString(cur.getColumnIndex("msg_media_extra"));
            String thumbnail = cur.getString(cur.getColumnIndex("msg_media_thumbnail"));
            String audioState = cur.getString(cur.getColumnIndex("msg_audio_state"));
            String progress = cur.getString(cur.getColumnIndex("msg_progress"));
            String prompt = cur.getString(cur.getColumnIndex("msg_prompt"));
            String contentType = cur.getString(cur.getColumnIndex("msg_content_type"));
            String destroy = cur.getString(cur.getColumnIndex("msg_destroy"));
            String sendState = cur.getString(cur.getColumnIndex("msg_send_state"));

            String fromremark = cur.getString(cur.getColumnIndex("msg_fromremark"));
            String toremark = cur.getString(cur.getColumnIndex("msg_toremark"));
            String userremark = cur.getString(cur.getColumnIndex("msg_userremark"));
            String receiveState = cur.getString(cur.getColumnIndex("msg_receive_state"));
            cm.setRedReceiveState(receiveState);
            cm.setFromuserremark(fromremark);
            cm.setTouserremark(toremark);
            cm.setUserremark(userremark);

            cm.setUsername(username);
            cm.setNickname(nickname);
            cm.setUserIcon(usericon);
            cm.setAvatar(avatar);
            cm.setFromuser(from);
            cm.setFromusernick(fromnick);
            cm.setTouser(to);
            cm.setTousernick(tonick);
            cm.setContent(content);
            cm.setTime(time);
            cm.setServeId(serveId);
            cm.setClientId(clientId);
            cm.setUrl(url);
            cm.setExtra(extra);
            cm.setThumbnailUrl(thumbnail);
            cm.setAudioState(audioState);
            cm.setProgress(progress);
            cm.setPrompt(prompt);
            cm.setContentType(contentType);
            cm.setDestroy(destroy);
            cm.setSendMsgState(sendState);
        }
        return cm;
    }

    //将数据转对象
    public ChatMessage cursorToChatMessage(Cursor cur) {
        ChatMessage cm = new ChatMessage();
        String master = cur.getString(cur.getColumnIndex("username"));
        String from = cur.getString(cur.getColumnIndex("msg_from"));
        String fromnick = cur.getString(cur.getColumnIndex("msg_fromnick"));
        String to = cur.getString(cur.getColumnIndex("msg_to"));
        String tonick = cur.getString(cur.getColumnIndex("msg_tonick"));
        String content = cur.getString(cur.getColumnIndex("msg_content"));
        String time = cur.getString(cur.getColumnIndex("msg_time"));
        String serveId = cur.getString(cur.getColumnIndex("msg_serveId"));
        String clientId = cur.getString(cur.getColumnIndex("msg_clientId"));
        String nickname = cur.getString(cur.getColumnIndex("msg_nickname"));
        String username = cur.getString(cur.getColumnIndex("msg_username"));
        String usericon = cur.getString(cur.getColumnIndex("msg_usericon"));
        String type = cur.getString(cur.getColumnIndex("msg_type"));
        String avatar = cur.getString(cur.getColumnIndex("msg_avatar"));
        String url = cur.getString(cur.getColumnIndex("msg_media_url"));
        String extra = cur.getString(cur.getColumnIndex("msg_media_extra"));
        String thumbnail = cur.getString(cur.getColumnIndex("msg_media_thumbnail"));
        String audioState = cur.getString(cur.getColumnIndex("msg_audio_state"));
        String progress = cur.getString(cur.getColumnIndex("msg_progress"));
        String prompt = cur.getString(cur.getColumnIndex("msg_prompt"));
        String contentType = cur.getString(cur.getColumnIndex("msg_content_type"));
        String destroy = cur.getString(cur.getColumnIndex("msg_destroy"));
        String sendState = cur.getString(cur.getColumnIndex("msg_send_state"));
        String redState = cur.getString(cur.getColumnIndex("msg_redopen_state"));
        if (cur.getColumnIndex("msg_hint_state") != -1) {
            String hintState = cur.getString(cur.getColumnIndex("msg_hint_state"));
            cm.setHintState(hintState);
        }
        //Caused by: java.lang.IllegalStateException: Couldn't read row 0, col -1 from CursorWindow.  Make sure the Cursor is initialized correctly before accessing data from it.
        if (cur.getColumnIndex("msg_unread_count") != -1) {
            int unreadCount = cur.getInt(cur.getColumnIndex("msg_unread_count")); //未读消息
            cm.setUnreadCount(unreadCount);//未读消息计数
        }
        String fromremark = cur.getString(cur.getColumnIndex("msg_fromremark"));
        String toremark = cur.getString(cur.getColumnIndex("msg_toremark"));
        String userremark = cur.getString(cur.getColumnIndex("msg_userremark"));

        String receiveState = cur.getString(cur.getColumnIndex("msg_receive_state"));
        cm.setRedReceiveState(receiveState);

        cm.setFromuserremark(fromremark);
        cm.setTouserremark(toremark);
        cm.setUserremark(userremark);

        cm.setMaster(master);
        cm.setNickname(nickname);
        cm.setUsername(username);
        cm.setUserIcon(usericon);
        cm.setType(type);
        cm.setAvatar(avatar);
        cm.setFromuser(from);
        cm.setFromusernick(fromnick);
        cm.setTouser(to);
        cm.setTousernick(tonick);
        cm.setContent(content);
        cm.setTime(time);
        cm.setServeId(serveId);
        cm.setClientId(clientId);
        cm.setUrl(url);
        cm.setExtra(extra);
        cm.setThumbnailUrl(thumbnail);
        cm.setAudioState(audioState);
        cm.setRedOpenState(redState);
        cm.setProgress(progress);
        cm.setPrompt(prompt);
        cm.setContentType(contentType);
        cm.setDestroy(destroy);
        cm.setSendMsgState(sendState);
        return cm;

    }


    /**
     * 通过 ClientId 获取数据库里的消息
     *
     * @param clientId
     * @return
     */
    public ChatMessage ByClientIdMessage(String clientId, String Master) {//username='" + msg.getMaster() + "'
        ChatDBHelper helper = new ChatDBHelper(context);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT * FROM chat_recent WHERE    msg_clientId ='" + clientId + "' and username = '" + Master + "'", null);
        if (cur.moveToNext()) {
            ChatMessage c = cursorToChatMessage(cur);
            cur.close();
            db.close();
            return c;
        } else {
            cur.close();
            db.close();
            return null;
        }

    }

    private void sendBroadcast(ChatMessage cm, String type) {
        Intent intent = new Intent(type);
        Bundle bundle = new Bundle();
        bundle.putSerializable("ChatMessage", cm);
        intent.putExtras(bundle);
        localBroadcastManager.sendBroadcast(intent);
    }

    //好友成功进群通知
    public ChatMessage sendNoticeMessage(String uid, String uname, String uicon, String notice) {
        ChatMessage cm = new ChatMessage();
        cm.setMaster(getUserInfo().getId());
        cm.setFromuser(getUserInfo().getId());
        cm.setFromusernick(getUserInfo().getName());
        cm.setTouser(uid);
        cm.setTousernick(uname);
        cm.setAvatar(uicon);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        cm.setTime(df.format(new Date()));
        String clientId = CommonUtil.getStringToDate(cm.getTime());
        cm.setClientId(clientId);
        cm.setType(Constants.FRAGMENT_GROUP);
        cm.setContentType(ChatMessage.CHAT_CONTENT_TYPE_NOTICE);
        cm.setUsername(getUserInfo().getId());
        cm.setNickname(getUserInfo().getName());
        cm.setUserIcon(getUserInfo().getIcon());
        cm.setMsgState("read");
        cm.setProgress("true");
        cm.setPrompt("false");
        cm.setContent(notice);
        //保存数据
        SaveChatMessage(cm);
        sendNoticeBroadcast(cm);

        return cm;

    }


    public List<ChatMessage> getMainChatList(String username) {
        List<ChatMessage> list = new ArrayList<>();

        ChatDBHelper helper = new ChatDBHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cur = db.rawQuery("SELECT * FROM chat_recent WHERE username ='" + username + "'and msg_type != '"+Constants.FRIENDS_NOTICE+"' ORDER BY msg_unread_count and msg_time DESC,msg_time DESC", null);
        while (cur.moveToNext()) {
            ChatMessage chatMessage = cursorToChatMessage(cur);
            ChatSettingEntity entity = ChatSettingManager.getInstance().query(chatMessage.getMaster(),chatMessage.getMaster().endsWith(chatMessage.getFromuser()) ? chatMessage.getTouser() : chatMessage.getFromuser());
            chatMessage.setSettingEntity(entity);
            if (entity.isChatTop()){
                list.add(0,chatMessage);
            }else {
                list.add(chatMessage);
            }
        }
        cur.close();
        db.close();
        return list;
    }



    public ChatMessage getOneChatRecent(String username,String id) {
        ChatDBHelper helper = new ChatDBHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cur = db.rawQuery("SELECT * FROM chat_recent WHERE username ='" + username + "'and msg_type != '"+Constants.FRIENDS_NOTICE+"' and _id = '" +id+"' ORDER BY msg_time DESC", null);
        ChatMessage chatMessage=null;
        while (cur.moveToNext()) {
            chatMessage = cursorToChatMessage(cur);
            ChatSettingEntity entity = ChatSettingManager.getInstance().query(chatMessage.getMaster(),chatMessage.getMaster().endsWith(chatMessage.getFromuser()) ? chatMessage.getTouser() : chatMessage.getFromuser());
            chatMessage.setSettingEntity(entity);
        }
        cur.close();
        db.close();
        return chatMessage;
    }


    /**
     * * 获取消息最近聊天消息(20条)
     *
     * @param master   消息拥有者id
     * @param chatType 聊天类型
     * @param friend   对方id
     * @param index    游标
     * @return
     */
    public List<ChatMessage> getMoreChatHistory(String master, String chatType, String friend, int index) {
        List<ChatMessage> list = new ArrayList<>();
        ChatDBHelper helper = new ChatDBHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cur = db.rawQuery("SELECT * FROM (SELECT * FROM chat_history WHERE username='" + master + "' AND msg_type='" + chatType + "' AND ((msg_from='" + master + "' AND msg_to='" + friend + "') OR (msg_from='" + friend + "' AND msg_to='" + master + "')) ORDER BY _id DESC LIMIT " + 0 + " , " + index + ") ORDER BY msg_time,msg_serveId", null);
        while (cur.moveToNext()) {
            ChatMessage chatMessage = cursorToChatMessage(cur);
            list.add(chatMessage);
        }
        cur.close();
        db.close();
        return list;
    }

    /**
     * * 获取消息最近聊天消息(20条)
     *
     * @param master   消息拥有者id
     * @param chatType 聊天类型
     * @param friend   对方id
     * @param index    游标
     * @return
     */
    public List<ChatMessage> getChatHistory(String master, String chatType, String friend, int index) {
        List<ChatMessage> list = new ArrayList<>();
        ChatDBHelper helper = new ChatDBHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cur = db.rawQuery("SELECT * FROM (SELECT * FROM chat_history WHERE username='" + master + "' AND msg_type='" + chatType + "' AND ((msg_from='" + master + "' AND msg_to='" + friend + "') OR (msg_from='" + friend + "' AND msg_to='" + master + "')) ORDER BY _id DESC LIMIT " + index + " , " + 20 + ") ORDER BY msg_time,msg_serveId", null);
        while (cur.moveToNext()) {
            ChatMessage chatMessage = cursorToChatMessage(cur);
            list.add(chatMessage);
        }
        cur.close();
        db.close();
        return list;
    }

    /**
     * 获取与好友最近未读消息
     *
     * @param master   消息拥有者id
     * @param chatType 聊天类型
     * @param friend   对方id
     */
    public List<ChatMessage> getMessageUnreadRead(String master, String chatType, String friend) {
        List<ChatMessage> list = new ArrayList<>();
        ChatDBHelper helper = new ChatDBHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cur = db.rawQuery("SELECT * FROM chat_recent WHERE username='" + master + "' AND msg_type='" + chatType + "' AND ((msg_from='" + master + "' AND msg_to='" + friend + "') OR (msg_from='" + friend + "' AND msg_to='" + master + "'))", null);
        while (cur.moveToNext()) {
            ChatMessage chatMessage = cursorToChatMessage(cur);
            list.add(chatMessage);
        }
        cur.close();
        db.close();
        return list;

    }

    /**
     * 将消息更新,未读->已读
     *
     * @param master   消息拥有者id
     * @param chatType 聊天类型
     * @param friend   对方id
     */
    public void UpdateMessageUnreadRead(String master, String chatType, String friend) {
        ChatDBHelper helper = new ChatDBHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("UPDATE chat_history SET msg_state='read' WHERE username='" + master + "' AND msg_type='" + chatType + "' AND msg_state='unread' AND ((msg_from='" + master + "' AND msg_to='" + friend + "') OR (msg_from='" + friend + "' AND msg_to='" + master + "'))");
        db.close();

    }

    /**
     * 统计 好友添加请求未读个数
     * CountMsgTypeUnread(ImApplication.userInfo.getId(),Constants.FRIENDS_NOTICE,"unread");
     * @param master
     * @param chatType
     * @param state
     */
    public int CountFriendUnread(String master, String chatType, String state) {
        ChatDBHelper helper = new ChatDBHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cur = db.rawQuery("SELECT COUNT(*) FROM chat_history  WHERE username='" + master + "' AND msg_type='" + chatType + "' AND msg_state='" + state + "' ", null);
        int  count =0;
        if (cur!=null && cur.moveToNext()) {
            count = cur.getInt(0);
        }
        cur.close();
        db.close();
        return count;
    }
    public int CountMsgTypeUnread(String master, String chatType, String state) {
        ChatDBHelper helper = new ChatDBHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();
         List<ChatSettingEntity>  ChatSettingEntity= ChatSettingManager.getInstance().queryAll(master);
         StringBuffer buffer = new StringBuffer();
        for (ChatSettingEntity entity :ChatSettingEntity){
            if (entity.getShield()){
                buffer.append(""+entity.getFriend()+",");
            }
        }

        String friend="";
        if (buffer.length()> 1){
            friend =   buffer.substring(0,buffer.length()-1).toString();
        }
        Cursor cur = db.rawQuery("SELECT COUNT(*) FROM chat_history  WHERE username='" + master + "'AND ((msg_from='" + master + "' AND msg_to NOT in ('" + friend + "')) OR (msg_from NOT in('" + friend + "') AND msg_to='" + master + "')) AND msg_type !='" + chatType + "' AND msg_state='" + state + "' ", null);
        int  count =0;
        if (cur!=null && cur.moveToNext()) {
            count = cur.getInt(0);
        }
        cur.close();
        db.close();
        return count;

    }
    public void UpdataFriendUnreadMsgState(String master, String chatType){
        ChatDBHelper helper = new ChatDBHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("UPDATE chat_history SET msg_state='read' WHERE username='" + master + "' AND msg_type='" + chatType + "' AND msg_state='unread'");
        db.close();
    }




}
