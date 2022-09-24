package com.quakoo.im.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;

import com.base.utils.CommonUtil;
import com.quakoo.im.ChatDBHelper;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IMCommonUtil {
    public static SpannableString getExpressionString(Context context, String str) {
        SpannableString spannableString = new SpannableString(str);
        // 正则表达式比配字符串里是否含有表情，如： 我好[开心]啊
//	        String zhengze = "\\[[^\\]]+\\]";
        String zhengze = "\\[[\\u4e00-\\u9fa5a-z]{1,3}\\]";
        // 通过传入的正则表达式来生成一个pattern
        Pattern sinaPatten = Pattern.compile(zhengze, Pattern.CASE_INSENSITIVE);
        try {
            dealExpression(context, spannableString, sinaPatten, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return spannableString;
    }

    private static void dealExpression(Context context, SpannableString spannableString, Pattern patten, int start) throws Exception {
        HashMap<String, String> emotionMap = new HashMap<String, String>();
        int count = EmotionUtils.emotion_custom_key.length;
        for (int i = 0; i < count; i++) {
            emotionMap.put(EmotionUtils.emotion_custom_key[i], EmotionUtils.emotion_custom_value[i]);
        }

        HashMap<String, String> emotionKV = new HashMap<String, String>();
        SQLiteDatabase emotionDatabase = ChatDBHelper.getEmotionDatabase();
        Cursor cur = emotionDatabase.rawQuery("select * from emotion", null);
        while (cur.moveToNext()) {
            String emotionDescr = cur.getString(cur.getColumnIndex("e_descr"));
            String emotionFilename = cur.getString(cur.getColumnIndex("e_name"));
            emotionKV.put(emotionDescr, emotionFilename);
        }
        cur.close();
        emotionDatabase.close();
        Matcher matcher = patten.matcher(spannableString);
        while (matcher.find()) {
            String key = matcher.group();
            // 返回第一个字符的索引的文本匹配整个正则表达式,ture 则继续递归
            if (matcher.start() < start) {
                continue;
            }

            int size = CommonUtil.sp2px(context, 20);
            String value = emotionKV.get(key);
            if (CommonUtil.isBlank(value)) {
//                continue;
                value = emotionMap.get(key);
                size = CommonUtil.sp2px(context, 50);
                if (CommonUtil.isBlank(value)) {
                    continue;
                }
            }
            Drawable d = Drawable.createFromStream(context.getAssets().open("emotion/" + value), null);
//            int size = sp2px(context, 20);
            d.setBounds(0, 0, size, size);
            ImageSpan imageSpan = new ImageSpan(d);
            // ImageSpan imageSpan = new ImageSpan(bitmap);
            // 计算该图片名字的长度，也就是要替换的字符串的长度
            int end = matcher.start() + key.length();
            // 将该图片替换字符串中规定的位置中
            spannableString.setSpan(imageSpan, matcher.start(), end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            if (end < spannableString.length()) {
                // 如果整个字符串还未验证完，则继续。。
                dealExpression(context, spannableString, patten, end);
            }
            break;
        }
    }

    public static SpannableString getExpressionString(Context context, HashMap<String, String> emotionK, String str) {
        SpannableString spannableString = new SpannableString(str);
        // 正则表达式比配字符串里是否含有表情，如： 我好[开心]啊
//	        String zhengze = "\\[[^\\]]+\\]";
        String zhengze = "\\[[\\u4e00-\\u9fa5a-z]{1,3}\\]";
        // 通过传入的正则表达式来生成一个pattern
        Pattern sinaPatten = Pattern.compile(zhengze, Pattern.CASE_INSENSITIVE);
        try {
            dealExpression(context, emotionK, spannableString, sinaPatten, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return spannableString;
    }

    public static void dealExpression(Context context, HashMap<String, String> emotionKV, SpannableString spannableString, Pattern patten, int start) throws Exception {
        HashMap<String, String> emotionMap = new HashMap<String, String>();
        int count = EmotionUtils.emotion_custom_key.length;
        for (int i = 0; i < count; i++) {
            emotionMap.put(EmotionUtils.emotion_custom_key[i], EmotionUtils.emotion_custom_value[i]);
        }

        Matcher matcher = patten.matcher(spannableString);
        while (matcher.find()) {
            String key = matcher.group();
            // 返回第一个字符的索引的文本匹配整个正则表达式,ture 则继续递归
            if (matcher.start() < start) {
                continue;
            }
            int size = CommonUtil.sp2px(context, 20);
            String value = emotionKV.get(key);
            if (CommonUtil.isBlank(value)) {
//                continue;
                value = emotionMap.get(key);
                size = CommonUtil.sp2px(context, 50);
                if (CommonUtil.isBlank(value)) {
                    continue;
                }
            }
            Drawable d = Drawable.createFromStream(context.getAssets().open("emotion/" + value), null);
//            int size = sp2px(context, 20);
            d.setBounds(0, 0, size, size);
            ImageSpan imageSpan = new ImageSpan(d);
            // ImageSpan imageSpan = new ImageSpan(bitmap);
            // 计算该图片名字的长度，也就是要替换的字符串的长度
            int end = matcher.start() + key.length();
            // 将该图片替换字符串中规定的位置中
            spannableString.setSpan(imageSpan, matcher.start(), end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            if (end < spannableString.length()) {
                // 如果整个字符串还未验证完，则继续。。
                dealExpression(context, emotionKV, spannableString, patten, end);
            }
            break;
        }
    }
}
