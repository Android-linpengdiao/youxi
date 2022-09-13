package com.okhttp.callbacks;

import android.content.Intent;

import com.base.MessageBus;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Response;


public abstract class StringCallback extends Callback<String> {
    @Override
    public String parseNetworkResponse(Response response, int id) throws IOException {
        String result = response.body().string();

        try {
            JSONObject json = new JSONObject(result);
            if (json.has("success")) {
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
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
