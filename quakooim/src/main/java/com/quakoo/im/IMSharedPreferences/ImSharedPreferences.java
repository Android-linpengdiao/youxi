package com.quakoo.im.IMSharedPreferences;

import android.content.Context;
import android.content.SharedPreferences;

import com.base.BaseApplication;
import com.base.UserInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Set;

public class ImSharedPreferences {
    //KEY
    public static final String PREFERENCE_NAME = "saveInfo";
    public static final String KEY_NOWUSERINFO="nowUserInfo"; //当前帐号
    public static final String KEY_HISTORYUSERINFO="historyUserInfo"; //历史帐号
    public static final String KEY_KEYBOARHEIGHT="keyboardheight";//键盘高度




    public static SharedPreferences mSharedPreferences=null;
    public static void init(Context context){
        mSharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        String user = mSharedPreferences.getString(KEY_NOWUSERINFO,null);
        if (user!=null && !user.equals("")){
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(user);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            UserInfo userInfo =UserInfo.getInstanceFromJson(jsonObject);
            BaseApplication.getInstance().setUserInfo(userInfo);
        }
        int keyboardheight = mSharedPreferences.getInt(KEY_KEYBOARHEIGHT,0);
        if (keyboardheight!=0){
            BaseApplication.keyboardheight = keyboardheight;
        }
    }
    public static boolean setValue(String key,Object o){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        if (key.equals(KEY_NOWUSERINFO)){//帐号存储操作 存储"当前帐号",需要先判断 xml 里有没有值,有,将之前存储的值给 "历史帐号"后才为 "当前帐号"赋值
            String temp =  mSharedPreferences.getString(KEY_NOWUSERINFO,"");
            if (!temp.equals("")){
                try {
                    JSONObject historyjsonObject = new JSONObject(temp); //本地存储的
                    UserInfo historyUserInfo = UserInfo.getInstanceFromJson(historyjsonObject);//本地存储的
                    JSONObject jsonObject1 = new JSONObject((String) o) ;//当前需要存储的
                    UserInfo nowUserInfo = UserInfo.getInstanceFromJson(jsonObject1);
                    if (historyUserInfo.getId().equals( nowUserInfo.getId())){//帐号id一致,一致表示是更新,不是切换帐号
                        editor.putString(key, (String)o);;
                        return editor.commit();
                    }else{
                        editor.putString(KEY_HISTORYUSERINFO,temp);
                        editor.putString(KEY_NOWUSERINFO, (String) o);
                        return editor.commit();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        if (o instanceof  Integer) {
            editor.putInt(key, (int)o);
        }else if (o instanceof String){
            editor.putString(key, (String)o);
        }else if (o instanceof Boolean){
            editor.putBoolean(key,(Boolean)o);
        }else if (o instanceof Float){
            editor.putFloat(key,(Float)o);
        }else if (o instanceof  Long){
            editor.putLong(key,(Long)o);
        }else if (o instanceof Set){
            editor.putStringSet(key,(Set<String>)o);
        }
        return editor.commit();
    }

    public static int getKeyboardHeight(){
        return mSharedPreferences.getInt(KEY_KEYBOARHEIGHT, 820);
    }

    public static void setKeyboardHeight(int keyboardheight){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(KEY_KEYBOARHEIGHT,keyboardheight);
        editor.commit();
    }

    public static void exitLogin(){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(KEY_NOWUSERINFO,null);
        editor.putString("MaxStreamIndex",null); //清除 游标
        editor.commit();
    }

    //获取历史帐号
    public static UserInfo getHistoryUserInfo(){
        String json = mSharedPreferences.getString(KEY_HISTORYUSERINFO,"");
        if(!json.equals("")){
            try {
                JSONObject jsonObject =new JSONObject(json);
                UserInfo historyUserInfo = UserInfo.getInstanceFromJson(jsonObject);
                return  historyUserInfo;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    //设置历史帐号
    public static boolean setHistoryUserInfo(Object o) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(KEY_HISTORYUSERINFO, (String) o);
        return editor.commit();
    }


}
