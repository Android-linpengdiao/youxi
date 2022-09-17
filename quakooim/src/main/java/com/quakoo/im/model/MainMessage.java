package com.quakoo.im.model;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 用于消息体在主线程处理,之前的UniteUpdateDataModel消息体,如果在主线程处理,会照成卡顿,但有些重要的消息在异步线程处理有问题,所以此类用于消息主线程
 */
public class MainMessage {
    private String key;//
    private String value;//可以放入json格式的数据
    private Object param1;
    private Object param2;

    public Object getParam1() {
        return param1;
    }

    public void setParam1(Object param1) {
        this.param1 = param1;
    }

    public Object getParam2() {
        return param2;
    }

    public void setParam2(Object param2) {
        this.param2 = param2;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
    public JSONObject getValueJson(){
        try {
            JSONObject jsonObject = new JSONObject(value);
            return  jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
            return  null;
        }
    }
    public String toJson(Object o){
        String temp="";
        Gson gson = new Gson();
        temp = gson.toJson(o);
        return temp;
    }
    public  Object Json_Model(Class calss){
        Gson gson = new Gson();
        return gson.fromJson(getValue(),calss);
    }
}
