package com.quakoo.im.utils;

import android.os.Build;
import android.os.Bundle;

import com.base.BaseApplication;
import com.base.Constants;
import com.base.utils.CommonUtil;
import com.base.utils.LogUtil;
import com.base.utils.MsgCache;
import com.base.utils.SharedPreferencesUtils;
import com.quakoo.im.model.ChatMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.UUID;

public class ContentUtils {

    private static final String TAG1 = "SocketService";
    private static final String TAG2 = "RedSocketService";
    private static final String TAG3 = "MsgPushService";
    private static final String UUID_CACHE_KEY = "Account_uuid";

    public static final int SIZEOF_INT = Integer.SIZE / Byte.SIZE;

    private static String getUUID() {
        String uuid = MsgCache.get(BaseApplication.getInstance()).getAsString(UUID_CACHE_KEY);
        if (CommonUtil.isBlank(uuid)) {
            uuid = UUID.randomUUID().toString();
            MsgCache.get(BaseApplication.getInstance()).put(UUID_CACHE_KEY, uuid);
        }
        return uuid;
    }

//    @SuppressLint("MissingPermission")
    public static byte[] Register(String master) {
//        String uuid = ((TelephonyManager) BaseApplication.getInstance().getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
//        if (CommonUtil.isBlank(uuid)) {
//            WifiManager wifi = (WifiManager) BaseApplication.getInstance().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//            WifiInfo info = wifi.getConnectionInfo();
//            uuid = info.getMacAddress();
//
//            if (CommonUtil.isBlank(uuid)) {
//                uuid = getUUID();
//            }
//        }
        String uuid = getUUID();
        JSONObject json = new JSONObject();
        try {
            json.put("type", 1);
            json.put("uid", Integer.parseInt(master));
            json.put("platform", 2);
            if (Build.MANUFACTURER.equalsIgnoreCase(Constants.Xiaomi)) {
                json.put("brand", 1);
            } else if (Build.MANUFACTURER.equalsIgnoreCase(Constants.huawei)) {
                json.put("brand", 2);
                json.put("huaWeiToken", SharedPreferencesUtils.getHuaWeiToken());
                LogUtil.d(TAG3, "Register: huaWeiToken " + SharedPreferencesUtils.getHuaWeiToken());
            } else if (Build.MANUFACTURER.equalsIgnoreCase(Constants.Meizu)) {
                json.put("brand", 3);
                json.put("meiZuPushId", SharedPreferencesUtils.getMeizuPushId());
                LogUtil.d(TAG3, "Register: meiZuPushId " + SharedPreferencesUtils.getHuaWeiToken());
            } else {
                json.put("brand", 0);
                json.put("huaWeiToken", 0);
            }
            json.put("phoneSessionId", uuid);
            LogUtil.d(TAG3, "Register: " + json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return context(json);
    }

//    @SuppressLint("MissingPermission")
    public static byte[] Logout(String master) {
//        String uuid = ((TelephonyManager) BaseApplication.getInstance().getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
//        if (CommonUtil.isBlank(uuid)) {
//            WifiManager wifi = (WifiManager) BaseApplication.getInstance().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//            WifiInfo info = wifi.getConnectionInfo();
//            uuid = info.getMacAddress();
//
//            if (CommonUtil.isBlank(uuid)) {
//                uuid = getUUID();
//            }
//        }
        String uuid = getUUID();
        JSONObject json = new JSONObject();
        try {
            json.put("type", 5);
            json.put("uid", Integer.parseInt(master));
            json.put("platform", 2);
            if (Build.MANUFACTURER.equalsIgnoreCase(Constants.Xiaomi)) {
                json.put("brand", 1);
            } else if (Build.MANUFACTURER.equalsIgnoreCase(Constants.huawei)) {
                json.put("brand", 2);
            } else if (Build.MANUFACTURER.equalsIgnoreCase(Constants.Meizu)) {
                json.put("brand", 3);
            } else {
                json.put("brand", 0);
            }
            json.put("phoneSessionId", uuid);
            LogUtil.d(TAG3, "Logout: " + json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return context(json);
    }

//    @SuppressLint("MissingPermission")
    public static byte[] PushHeartbeat(String master) {
//        String uuid = ((TelephonyManager) BaseApplication.getInstance().getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
//        if (CommonUtil.isBlank(uuid)) {
//            WifiManager wifi = (WifiManager) BaseApplication.getInstance().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//            WifiInfo info = wifi.getConnectionInfo();
//            uuid = info.getMacAddress();
//
//            if (CommonUtil.isBlank(uuid)) {
//                uuid = getUUID();
//            }
//        }
        String uuid = getUUID();
        JSONObject json = new JSONObject();
        try {
            json.put("type", 2);
            json.put("uid", Integer.parseInt(master));
            json.put("platform", 2);
            json.put("brand", 0);
            json.put("phoneSessionId", uuid);
            LogUtil.d(TAG3, "Register: " + json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return context(json);
    }

    public static byte[] ImHeartbeat(String master, double maxStreamIndex) {
        JSONObject json = new JSONObject();
        try {
            json.put("token", BaseApplication.getUserInfo().getToken());
            json.put("type", 1);
            json.put("uid", Integer.parseInt(master));
            json.put("lastIndex", maxStreamIndex);
            LogUtil.d(TAG1, "ImHeartbeat: " + json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return context(json);
    }

    public static byte[] sendRecallMessage(ChatMessage cm) {
        JSONObject json = new JSONObject();
        try {
            json.put("token", BaseApplication.getUserInfo().getToken());
            json.put("type", 6);
            json.put("uid", Integer.parseInt(cm.getMaster()));
            json.put("clientId", cm.getClientId());
            if (cm.getType().equals(Constants.FRAGMENT_FRIEND)) {
                json.put("chatType", 1);
            } else if (cm.getType().equals(Constants.FRAGMENT_GROUP)) {
                json.put("chatType", 2);
            }
            if (cm.getFromuser().equals(cm.getMaster())) {
                json.put("thirdId", Integer.parseInt(cm.getTouser()));
            } else {
                json.put("thirdId", Integer.parseInt(cm.getFromuser()));
            }
            json.put("mid", cm.getServeId());

            LogUtil.d(TAG1, "sendRecallMessage: " + json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
            LogUtil.d(TAG1, "sendRecallMessage: e " + e.getMessage());
        }
        return context(json);
    }

    public static byte[] sendDeleteMessage(ChatMessage cm) {
        JSONObject json = new JSONObject();
        try {
            json.put("token", BaseApplication.getUserInfo().getToken());
            json.put("type", 3);
            json.put("uid", Integer.parseInt(cm.getMaster()));
            if (cm.getType().equals(Constants.FRAGMENT_FRIEND)) {
                json.put("chatType", 1);
            } else if (cm.getType().equals(Constants.FRAGMENT_GROUP)) {
                json.put("chatType", 2);
            }
            if (cm.getFromuser().equals(cm.getMaster())) {
                json.put("thirdId", Integer.parseInt(cm.getTouser()));
            } else {
                json.put("thirdId", Integer.parseInt(cm.getFromuser()));
            }
            json.put("mid", cm.getServeId());

            LogUtil.d(TAG1, "sendDeleteMessage: " + json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
            LogUtil.d(TAG1, "sendDeleteMessage: e " + e.getMessage());
        }
        return context(json);
    }

    public static byte[] sendMessage(ChatMessage cm) {
        JSONObject json = new JSONObject();
        try {
            json.put("token", BaseApplication.getUserInfo().getToken());
            if (cm.getContentType().equals(ChatMessage.CHAT_CONTENT_TYPE_RED)) {
                json.put("type", 10);
            } else {
                json.put("type", 2);
            }
            json.put("uid", Integer.parseInt(cm.getMaster()));
            json.put("clientId", cm.getClientId());
            if (cm.getType().equals(Constants.FRAGMENT_FRIEND)) {
                json.put("chatType", 1);
            } else if (cm.getType().equals(Constants.FRAGMENT_GROUP)) {
                json.put("chatType", 2);
            }
            if (cm.getContentType().equals(ChatMessage.CHAT_CONTENT_TYPE_TXT)) {
                json.put("word", cm.getContent());
                json.put("ext", cm.getExtra());
            } else if (cm.getContentType().equals(ChatMessage.CHAT_CONTENT_TYPE_PIC)) {
                json.put("picture", cm.getUrl());
            } else if (cm.getContentType().equals(ChatMessage.CHAT_CONTENT_TYPE_AUDIO)) {
                json.put("voice", cm.getUrl());
                json.put("voiceDuration", cm.getExtra());
            } else if (cm.getContentType().equals(ChatMessage.CHAT_CONTENT_TYPE_VIDEO)) {
                json.put("video", cm.getUrl());
                json.put("picture", cm.getThumbnailUrl());
            } else if (cm.getContentType().equals(ChatMessage.CHAT_CONTENT_TYPE_RED)) {
                LogUtil.d(TAG1, "sendMessage: " + cm.getContentType());
                JSONObject object = new JSONObject();
                try {
//                    JSONObject extra = new JSONObject();
//                    extra.put("title", cm.content);
//                    JSONObject jsonObject = new JSONObject(cm.getExtra());
//                    extra.put("money", jsonObject.optString("money"));
//                    extra.put("getUid", jsonObject.optString("getUid"));
//                    extra.put("redId", jsonObject.optString("redId"));
//                    extra.put("redId", cm.getExtra());
                    object.put("type", "1");// 1、发红包；2、截屏类型；3、截屏通知
                    object.put("extra", cm.getExtra());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                json.put("ext", object.toString());
            } else if (cm.getContentType().equals(ChatMessage.CHAT_CONTENT_TYPE_SCREEN)) {
                LogUtil.d(TAG1, "sendMessage: " + cm.getContentType());
                JSONObject object = new JSONObject();
                try {
                    JSONObject extra = new JSONObject();
                    extra.put("content", cm.content);
                    object.put("type", "2");// 1、发红包；2、截屏类型；3、截屏通知
                    object.put("extra", extra.toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                json.put("ext", object.toString());
            } else if (cm.getContentType().equals(ChatMessage.CHAT_CONTENT_TYPE_USER_SCREEN)) {
                LogUtil.d(TAG1, "sendMessage: " + cm.getContentType());
                JSONObject object = new JSONObject();
                try {
                    JSONObject extra = new JSONObject();
                    extra.put("content", cm.content);
                    object.put("type", "3");// 1、发红包；2、截屏类型；3、截屏通知
                    object.put("extra", extra.toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                json.put("ext", object.toString());
            } else if (cm.getContentType().equals(ChatMessage.CHAT_CONTENT_TYPE_DESTROY)) {
                LogUtil.d(TAG1, "sendMessage: " + cm.getContentType());
                JSONObject object = new JSONObject();
                try {
                    JSONObject extra = new JSONObject();
                    extra.put("content", cm.content);
                    object.put("type", "4");// 1、发红包；2、截屏类型；3、截屏通知；4、阅后即焚通知
                    object.put("extra", extra.toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                json.put("ext", object.toString());
            } else if (cm.getContentType().equals(ChatMessage.CHAT_CONTENT_TYPE_LOCATION)) {
                LogUtil.d(TAG1, "sendMessage: " + cm.getContentType());
                JSONObject object = new JSONObject();
                try {
                    object.put("type", "5");// 1、发红包；2、截屏类型；3、截屏通知；4、阅后即焚通知；5、位置消息
                    object.put("extra", cm.getExtra());
                    LogUtil.d(TAG1, "sendMessage: extra " + cm.getExtra());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                json.put("ext", object.toString());
            } else if (cm.getContentType().equals(ChatMessage.CHAT_CONTENT_TYPE_NAMECARD)) {
                LogUtil.d(TAG1, "sendMessage: " + cm.getContentType());
                JSONObject object = new JSONObject();
                try {
//                    JSONObject extra = new JSONObject();
//                    extra.put("content", cm.content);
//                    extra.put("icon", cm.getUrl());
//                    extra.put("uid", cm.getExtra());
                    object.put("type", "6");// 1、发红包；2、截屏类型；3、截屏通知；4、阅后即焚通知；5、位置消息；6、名片消息
                    object.put("extra", cm.getExtra());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                json.put("ext", object.toString());
            } else if (cm.getContentType().equals(ChatMessage.CHAT_CONTENT_TYPE_SHARE)) {
                LogUtil.d(TAG1, "sendMessage: " + cm.getContentType());
                JSONObject object = new JSONObject();
//                try {
//                    JSONObject extra = new JSONObject();
//                    extra.put("url", cm.getUrl());
//                    extra.put("title", cm.getContent());
//                    extra.put("desc", cm.getExtra());
//                    extra.put("thumUrl", cm.getThumbnailUrl());
//                    object.put("type", "7");// 1、发红包；2、截屏类型；3、截屏通知；4、阅后即焚通知；5、位置消息；6、名片消息；7、分享文本连接
//                    object.put("extra", extra.toString());
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }

                object.put("type", "7");// 1、发红包；2、截屏类型；3、截屏通知；4、阅后即焚通知；5、位置消息；6、名片消息；7、分享文本连接
                object.put("extra", cm.getExtra());

                json.put("ext", object.toString());
            } else if (cm.getContentType().equals(ChatMessage.CHAT_CONTENT_TYPE_OPEN_RED)) {
                LogUtil.d(TAG1, "sendMessage: " + cm.getContentType());
                JSONObject object = new JSONObject();
                try {
//                    JSONObject extra = new JSONObject();
//                    extra.put("title", cm.content);
//                    extra.put("redId", cm.getExtra());
//                    extra.put("uid", cm.getExtra());
                    object.put("type", "8");// 1、发红包；2、截屏类型；3、截屏通知；4、阅后即焚通知；5、位置消息；6、名片消息；7、分享文本连接；8、领取红包
                    object.put("extra", cm.getExtra());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                json.put("ext", object.toString());
            } else if (cm.getContentType().equals(ChatMessage.CHAT_CONTENT_TYPE_NOTICE)) {
                LogUtil.d(TAG1, "sendMessage: " + cm.getContentType());
//                JSONObject object = new JSONObject();
//                try {
//                    JSONObject extra = new JSONObject();
//                    extra.put("title", cm.content);
//                    object.put("type", "9");// 1、发红包；2、截屏类型；3、截屏通知；4、阅后即焚通知；5、位置消息；6、名片消息；7、分享文本连接；8、领取红包；9、通知类型
//                    object.put("extra", extra.toString());
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                json.put("ext", object.toString());
                json.put("ext", cm.getExtra());
            } else if (cm.getContentType().equals(ChatMessage.CHAT_CONTENT_TYPE_CHECKJOIN)) {
                LogUtil.d(TAG1, "sendMessage: " + cm.getContentType());
                JSONObject object = new JSONObject();
                try {
                    object.put("type", "16");// 1、发红包；2、截屏类型；3、截屏通知；4、阅后即焚通知；5、位置消息；6、名片消息；7、分享文本连接；8、领取红包；9、通知类型；16、加群审核
                    object.put("extra", cm.getExtra());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                json.put("ext", object.toString());
            } else if (cm.getContentType().equals(ChatMessage.CHAT_CONTENT_TYPE_SHARE_PLUGIND)) {
                LogUtil.d(TAG1, "sendMessage: " + cm.getContentType());
                JSONObject object = new JSONObject();
                try {
                    object.put("type", "10");// 1、发红包；2、截屏类型；3、截屏通知；4、阅后即焚通知；5、位置消息；6、名片消息；7、分享文本连接；8、领取红包；9、通知类型；16、加群审核；10、分享小程序
                    object.put("extra", cm.getExtra());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                json.put("ext", object.toString());
            } else if (cm.getContentType().equals(ChatMessage.CHAT_CONTENT_TYPE_EMOTION)) {
                LogUtil.d(TAG1, "sendMessage: " + cm.getContentType());
                JSONObject object = new JSONObject();
                try {
                    object.put("type", "11");// 1、发红包；2、截屏类型；3、截屏通知；4、阅后即焚通知；5、位置消息；6、名片消息；7、分享文本连接；8、领取红包；9、通知类型；16、加群审核；10、分享小程序；11、自定义表情
                    object.put("extra", cm.getExtra());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                json.put("ext", object.toString());
            }else if (cm.getContentType().equals(ChatMessage.CHAT_CONTENT_TYPE_PRODUCT)) {
                LogUtil.d(TAG1, "sendMessage: " + cm.getContentType());
                JSONObject object = new JSONObject();
                try {
                    object.put("type", "17");// 1、发红包；2、截屏类型；3、截屏通知；4、阅后即焚通知；5、位置消息；6、名片消息；7、分享文本连接；8、领取红包；9、通知类型；16、加群审核；10、分享小程序；11、自定义表情
                    object.put("extra", cm.getExtra());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                json.put("ext", object.toString());
            }

            json.put("thirdId", Integer.parseInt(cm.getTouser()));

            LogUtil.d(TAG1, "sendMessage: " + json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
            LogUtil.d(TAG1, "sendMessage: e " + e.getMessage());
        }
        return context(json);
    }

    public static byte[] sendRedMsg(Bundle bundle, String token, String roomNo) {
        JSONObject json = new JSONObject();
        try {
            json.put("action", "401");
            json.put("token", token);
            json.put("roomNo", roomNo);
            json.put("name", bundle.getString("name"));
            json.put("score", bundle.getString("score"));
            json.put("boom", bundle.getString("boom"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtil.d(TAG2, "sendRedMsg: " + json.toString());
        return context(json);
    }

    public static byte[] JoinRedRoom(String token, String roomNo) {
        JSONObject json = new JSONObject();
        try {
            json.put("action", "101");
            json.put("token", token);
            json.put("roomNo", roomNo);
            LogUtil.d(TAG2, "JoinRedRoom: " + json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return context(json);
    }

    public static byte[] raubenRedMsg(String token, String redId, String roomNo) {
        JSONObject json = new JSONObject();
        try {
            json.put("action", "402");
            json.put("token", token);
            json.put("redId", redId);
            json.put("roomNo", roomNo);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtil.d(TAG2, "raubenRedMsg: " + json.toString());
        return context(json);
    }

    public static byte[] sendRedMsg(Map<String, String> map, String token, String roomNo) {
        JSONObject json = new JSONObject();
        try {
            Map values = map;
            json.put("token", token);
            json.put("roomNo", roomNo);
            for (Object vk : values.keySet()) {
                json.put((String) vk, values.get(vk));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtil.d(TAG2, "sendRedMsg: " + json.toString());
        return context(json);
    }

    public static byte[] context(JSONObject obj) {
        String json = obj.toString();
        byte[] bytes = json.getBytes();
        byte[] head = putInt(bytes.length);
        byte[] result = new byte[bytes.length + head.length];
        for (int i = 0; i < head.length; i++) {
            result[i] = head[i];
        }
        for (int i = 0; i < bytes.length; i++) {
            result[i + head.length] = bytes[i];
        }
        return result;
    }

    public static void putInt(byte[] bb, int x, int index) {
        bb[index + 0] = (byte) (x >> 24);
        bb[index + 1] = (byte) (x >> 16);
        bb[index + 2] = (byte) (x >> 8);
        bb[index + 3] = (byte) (x >> 0);
    }

    public static byte[] putInt(int x) {
        byte[] bb = new byte[SIZEOF_INT];
        putInt(bb, x, 0);
        return bb;
    }

}
