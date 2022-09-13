package com.okhttp.utils;

import android.util.Base64;

import com.base.utils.AES256Utils;
import com.base.utils.GsonUtils;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HeadersUtils {

    public static Map<String, String> getHeaders(Map<String, String> paramMap, String url) {
        Map<String, String> headerMap = new HashMap<>();
        try {
            List<String> keys = new ArrayList<>();
            Set set = paramMap.keySet();
            Iterator iterator = set.iterator();
            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                keys.add(key);
            }
            Collections.sort(keys);
            String params = "";
            for (int i = 0; i < keys.size(); i++) {
                String key = keys.get(i);
                String value = paramMap.get(key);
                value = URLEncoder.encode(value, "utf-8");
                if (i == keys.size() - 1) {// 拼接时，不包括最后一个&字符
                    params += key + "=" + value;
                } else {
                    params += key + "=" + value + "&";
                }
            }

            Map<String, String> headers = new HashMap<>();
            headers.put("url", url);
            headers.put("time", String.valueOf(System.currentTimeMillis()));
            headers.put("params", params);
            String code = Base64.encodeToString(AES256Utils.encrypt(GsonUtils.toJson(headers).getBytes(), "01234567890123450123456789012345"), Base64.DEFAULT).replaceAll("\r|\n", "");

            headerMap.put("sign", code);
            return headerMap;
        } catch (Exception exception) {
            exception.getMessage();
        }
        return headerMap;

    }
}
