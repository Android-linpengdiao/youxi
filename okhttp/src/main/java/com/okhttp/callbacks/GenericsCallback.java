package com.okhttp.callbacks;

import android.content.Intent;

import com.base.MessageBus;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.Response;


public abstract class GenericsCallback<T> extends Callback<T> {
    IGenericsSerializator mGenericsSerializator;

    public GenericsCallback(IGenericsSerializator serializator) {
        mGenericsSerializator = serializator;
    }

    @Override
    public T parseNetworkResponse(Response response, int id) throws IOException {
        String result=response.body().string();

        try{
            JSONObject json = new JSONObject(result);
            if(json.has("success")){
                boolean success = json.optBoolean("success");
                int code = json.optInt("code");
                if (!success && (code == 400 || code == 401 || code == 404)) {
                    MessageBus.Builder builder = new MessageBus.Builder();
                    MessageBus messageBus = builder
                            .codeType(MessageBus.msgId_logout)
                            .message(code)
                            .build();
                    EventBus.getDefault().post(messageBus);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

//        Class<T> entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
//        if (entityClass == String.class) {
//            return (T) result;
//        }
//        T bean = mGenericsSerializator.transform(result, entityClass);

        Type type=((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        T bean;
        try {
            Class<T> entityClass = (Class<T>) type;
            if (entityClass == String.class) {
                return (T) result;
            }
            bean = mGenericsSerializator.transform(result, entityClass);
        }catch (Exception e){
            bean = mGenericsSerializator.transform(result, type);
        }

        return bean;
    }

}
