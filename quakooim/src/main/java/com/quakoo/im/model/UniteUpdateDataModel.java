package com.quakoo.im.model;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

public class UniteUpdateDataModel {
    private String key;//
    private String value;//可以放入json格式的数据

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
