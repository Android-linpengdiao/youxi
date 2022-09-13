package com.okhttp;

import android.os.Build;
import android.util.Base64;
import android.util.Log;

import com.base.BaseApplication;
import com.base.utils.AES256Utils;
import com.base.utils.GsonUtils;
import com.okhttp.callbacks.Callback;
import com.okhttp.utils.APIUrls;
import com.okhttp.utils.HeadersUtils;
import com.okhttp.utils.OkHttpUtils;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SendRequest {
    private static String TAG = "SendRequest";

    /**
     * ======================================  中国视听  ================================================
     */

    /**
     * ======================================  登录 - 用户信息  ================================================
     */

    /**
     * 注册 ---- 发送注册验证码
     */
    public static void createAuthCodeOnReg(String phone, Callback call) {
        Map<String, String> map = new HashMap<>();
        map.put("phone", phone);
        OkHttpUtils.post()
                .headers(HeadersUtils.getHeaders(map, APIUrls.createAuthCodeOnReg))
                .params(map).url(APIUrls.createAuthCodeOnReg).tag(APIUrls.createAuthCodeOnReg).build().execute(call);

    }

    /**
     * 注册 ---- 手机号密码注册
     * string phone 手机号
     * string code 短信验证码
     * string password 密码
     * string againPassword 第二次输入密码
     * string phoneModel 手机型号
     * string platform platform
     * integer subPlatform subPlatform
     */
    public static void registerByPhoneAndPassword(String phone, String code, String password, Callback call) {
        Map<String, String> map = new HashMap<>();
        map.put("phone", phone);
        map.put("code", code);
        map.put("password", password);
        map.put("againPassword", password);
        map.put("phoneModel", Build.MODEL);
        OkHttpUtils.post()
                .headers(HeadersUtils.getHeaders(map, APIUrls.registerByPhoneAndPassword))
                .params(map).url(APIUrls.registerByPhoneAndPassword).tag(APIUrls.registerByPhoneAndPassword).build().execute(call);

    }

    /**
     * 手机号密码登录
     *
     * @param phone
     * @param password
     * @param call
     */
    public static void login(String phone, String password, Callback call) {
        Map<String, String> map = new HashMap<>();
        map.put("phone", phone);
        map.put("password", password);
        OkHttpUtils.post()
                .headers(HeadersUtils.getHeaders(map, APIUrls.login))
                .params(map).url(APIUrls.login).tag(APIUrls.login).build().execute(call);

    }


    /**
     * 发送修改密码的验证码
     */
    public static void createUpdateAuthCode(String phone, Callback call) {
        Map<String, String> map = new HashMap<>();
        map.put("phone", phone);
        OkHttpUtils.post()
                .headers(HeadersUtils.getHeaders(map, APIUrls.createUpdateAuthCode))
                .params(map).url(APIUrls.createUpdateAuthCode).tag(APIUrls.createUpdateAuthCode).build().execute(call);

    }

    /**
     * 注册 ---- 手机号密码注册
     * string phone 手机号
     * string code 验证码
     * string newPassword 新密码
     * string againNewPassword 再次输入新密码
     */
    public static void updatePassword(String phone, String code, String newPassword, String againNewPassword, Callback call) {
        Map<String, String> map = new HashMap<>();
        map.put("token", BaseApplication.getInstance().getUserInfo().getToken());
        map.put("phone", phone);
        map.put("code", code);
        map.put("newPassword", newPassword);
        map.put("againNewPassword", againNewPassword);
        OkHttpUtils.post()
                .headers(HeadersUtils.getHeaders(map, APIUrls.updatePassword))
                .params(map).url(APIUrls.updatePassword).tag(APIUrls.updatePassword).build().execute(call);

    }

    /**
     * 发送忘记密码的验证码(未登录) 不传token(用)
     */
    public static void createForgotAuthCode(String phone, Callback call) {
        Map<String, String> map = new HashMap<>();
        map.put("phone", phone);
        OkHttpUtils.post()
                .headers(HeadersUtils.getHeaders(map, APIUrls.createForgotAuthCode))
                .params(map).url(APIUrls.createForgotAuthCode).tag(APIUrls.createForgotAuthCode).build().execute(call);

    }

    /**
     * 注册 ---- 手机号密码注册
     * string phone 手机号
     * string code 验证码
     * string newPassword 新密码
     * string againNewPassword 再次输入新密码
     */
    public static void updatePasswordAndLogin(String phone, String code, String password, String againNewPassword, Callback call) {
        Map<String, String> map = new HashMap<>();
        map.put("token", BaseApplication.getInstance().getUserInfo().getToken());
        map.put("phone", phone);
        map.put("code", code);
        map.put("password", password);
        map.put("againPassword", againNewPassword);
        OkHttpUtils.post()
                .headers(HeadersUtils.getHeaders(map, APIUrls.updatePasswordAndLogin))
                .params(map).url(APIUrls.updatePasswordAndLogin).tag(APIUrls.updatePasswordAndLogin).build().execute(call);

    }

    /**
     * 用户注销
     *
     * @param token
     * @param call
     */
    public static void logout(String token, Callback call) {
        Map<String, String> map = new HashMap<>();
        map.put("token", token);
        OkHttpUtils.post()
                .headers(HeadersUtils.getHeaders(map, APIUrls.logout))
                .params(map).url(APIUrls.logout).build().execute(call);

    }

    /**
     * string icon 头像
     * string name 昵称
     * integer sex 性别
     * integer provinceId 省ID
     * integer cityId 城市ID
     * string info 签名
     * string birthdate 生日
     *
     * @param call
     */
    public static void updateUserInfo(Map<String, String> map, Callback call) {
        OkHttpUtils.post()
                .headers(HeadersUtils.getHeaders(map, APIUrls.updateUserInfo))
                .params(map).url(APIUrls.updateUserInfo).tag(APIUrls.updateUserInfo).build().execute(call);

    }

    public static void getUserInfo(Callback call) {
        Map<String, String> map = new HashMap<>();
        map.put("token", BaseApplication.getInstance().getUserInfo().getToken());
        OkHttpUtils.post()
                .headers(HeadersUtils.getHeaders(map, APIUrls.getUserInfo))
                .params(map).url(APIUrls.getUserInfo).tag(APIUrls.getUserInfo).build().execute(call);

    }

    public static void userLoad(String token, Long id, Callback call) {
        Map<String, String> map = new HashMap<>();
        map.put("token", token);
        map.put("id", String.valueOf(id));
        OkHttpUtils.post()
                .headers(HeadersUtils.getHeaders(map, APIUrls.userLoad))
                .params(map).url(APIUrls.userLoad).tag(APIUrls.userLoad).build().execute(call);

    }

    /*
    ======================================  栏目  ================================================
     */

    /**
     * 获取栏目轮播图
     *
     * @param columnId
     * @param size
     * @param call
     */
    public static void getColumnBanners(int columnId, int size, Callback call) {
        Map<String, String> map = new HashMap<>();
        map.put("token", BaseApplication.getInstance().getUserInfo().getToken());
        map.put("columnId", columnId + "");
        map.put("size", size + "");
        OkHttpUtils.post()
                .headers(HeadersUtils.getHeaders(map, APIUrls.getColumnBanners))
                .params(map).url(APIUrls.getColumnBanners).tag(APIUrls.getColumnBanners).build().execute(call);

    }

    public static void getRecommendBanners(int size, Callback call) {
        Map<String, String> map = new HashMap<>();
        map.put("token", BaseApplication.getInstance().getUserInfo().getToken());
        map.put("size", size + "");
        OkHttpUtils.post()
                .headers(HeadersUtils.getHeaders(map, APIUrls.getRecommendBanners))
                .params(map).url(APIUrls.getRecommendBanners).tag(APIUrls.getRecommendBanners).build().execute(call);

    }


    /*
    ======================================  栏目  ================================================
     */

    /**
     * 获取栏目列表
     *
     * @param section 新视界：1；悦动听：2；新思想：3；
     * @param pid     一级栏目 pid:0
     * @param call
     */
    public static void column_getAllList(int section, int pid, Callback call) {

        try {
            Map<String, String> map = new HashMap<>();
            map.put("token", BaseApplication.getInstance().getUserInfo().getToken());
            map.put("section", section + "");
            map.put("pid", pid + "");
            OkHttpUtils.post()
                    .headers(HeadersUtils.getHeaders(map, APIUrls.column_getAllList))
                    .params(map)
                    .url(APIUrls.column_getAllList)
                    .tag(APIUrls.column_getAllList)
                    .build()
                    .execute(call);

//            List<String> keys = new ArrayList<>();
//            keys.add("token");
//            keys.add("section");
//            keys.add("pid");
//            Collections.sort(keys);
//            String params = "";
//            for (int i = 0; i < keys.size(); i++) {
//                String key = keys.get(i);
//                String value = paramMap.get(key);
//                value = URLEncoder.encode(value, "utf-8");
//                if (i == keys.size() - 1) {// 拼接时，不包括最后一个&字符
//                    params += key + "=" + value;
//                } else {
//                    params += key + "=" + value + "&";
//                }
//            }
//
//            Map<String, String> headers = new HashMap<>();
//            headers.put("url", APIUrls.column_getAllList);
//            headers.put("time", String.valueOf(System.currentTimeMillis()));
//            headers.put("params", params);
//            String code = Base64.encodeToString(AES256Utils.encrypt(GsonUtils.toJson(headers).getBytes(), "01234567890123450123456789012345"), Base64.DEFAULT).replaceAll("\r|\n", "");
//            Log.i(TAG, "column_getAllList: code " + code);
//            Map<String, String> headerMap = new HashMap<>();
//            headerMap.put("sign", code);
//            OkHttpUtils.post().headers(headerMap).params(paramMap).url(APIUrls.column_getAllList).tag(APIUrls.column_getAllList).build().execute(call);

        } catch (Exception exception) {
            exception.getMessage();
        }

    }

    /**
     * 获取栏目详情
     *
     * @param id
     * @param call
     */
    public static void column_load(int id, Callback call) {
        Map<String, String> map = new HashMap<>();
        map.put("token", BaseApplication.getInstance().getUserInfo().getToken());
        map.put("id", id + "");
        OkHttpUtils.post()
                .headers(HeadersUtils.getHeaders(map, APIUrls.column_load))
                .params(map).url(APIUrls.column_load).tag(APIUrls.column_load).build().execute(call);

    }

    /**
     * 栏目内容分页
     *
     * @param columnId
     * @param cursor
     * @param call
     */
    public static void getColumnPager(int columnId, String cursor, Callback call) {
        Map<String, String> map = new HashMap<>();
        map.put("token", BaseApplication.getInstance().getUserInfo().getToken());
        map.put("columnId", columnId + "");
        map.put("cursor", cursor);
        OkHttpUtils.post()
                .headers(HeadersUtils.getHeaders(map, APIUrls.column_getColumnPager))
                .params(map).url(APIUrls.column_getColumnPager).tag(APIUrls.column_getColumnPager).build().execute(call);

    }

    public static void getColumnPager(int columnId, String cursor, String size, Callback call) {
        Map<String, String> map = new HashMap<>();
        map.put("token", BaseApplication.getInstance().getUserInfo().getToken());
        map.put("columnId", columnId + "");
        map.put("cursor", cursor);
        map.put("size", size);
        OkHttpUtils.post()
                .headers(HeadersUtils.getHeaders(map, APIUrls.column_getColumnPager))
                .params(map).url(APIUrls.column_getColumnPager).tag(APIUrls.column_getColumnPager).build().execute(call);

    }

    /**
     * 获取栏目内容列表
     *
     * @param columnId
     * @param size
     * @param call
     */
    public static void getColumnList(int columnId, int size, Callback call) {
        Map<String, String> map = new HashMap<>();
        map.put("token", BaseApplication.getInstance().getUserInfo().getToken());
        map.put("columnId", columnId + "");
        map.put("size", size + "");
        OkHttpUtils.post()
                .headers(HeadersUtils.getHeaders(map, APIUrls.column_getColumnList))
                .params(map).url(APIUrls.column_getColumnList).tag(APIUrls.column_getColumnList).build().execute(call);

    }


    public static void getColumnRowList(int columnId, int size, Callback call) {
        Map<String, String> map = new HashMap<>();
        map.put("token", BaseApplication.getInstance().getUserInfo().getToken());
        map.put("columnId", columnId + "");
        map.put("size", size + "");
        OkHttpUtils.post()
                .headers(HeadersUtils.getHeaders(map, APIUrls.column_getColumnRowList))
                .params(map).url(APIUrls.column_getColumnRowList).tag(APIUrls.column_getColumnRowList).build().execute(call);

    }

    /**
     * 获取栏目所有内容列表
     *
     * @param columnId
     * @param provinceId
     * @param cityId
     * @param call
     */
    public static void getColumnAllList(int columnId, int provinceId, int cityId, Callback call) {
        Map<String, String> map = new HashMap<>();
        map.put("token", BaseApplication.getInstance().getUserInfo().getToken());
        map.put("columnId", columnId + "");
        map.put("provinceId", provinceId + "");
        map.put("cityId", cityId + "");
        OkHttpUtils.post()
                .headers(HeadersUtils.getHeaders(map, APIUrls.column_getColumnAllList))
                .params(map).url(APIUrls.column_getColumnAllList).tag(APIUrls.column_getColumnAllList).build().execute(call);

    }

    /**
     * 本地台
     *
     * @param columnId
     * @param provinceId
     * @param cityId
     * @param call
     */
    public static void getColumnMergeAllList(int columnId, int provinceId, int cityId, Callback call) {
        Map<String, String> map = new HashMap<>();
        map.put("token", BaseApplication.getInstance().getUserInfo().getToken());
        map.put("columnId", columnId + "");
        map.put("provinceId", provinceId + "");
        map.put("cityId", cityId + "");
        Log.i(TAG, "getColumnMergeAllList: " + map.toString());
        OkHttpUtils.post()
                .headers(HeadersUtils.getHeaders(map, APIUrls.column_getColumntMergeAllList))
                .params(map).url(APIUrls.column_getColumntMergeAllList).tag(APIUrls.column_getColumntMergeAllList).build().execute(call);

    }

    /**
     * 栏目专题分页
     *
     * @param columnId
     * @param cursor
     * @param call
     */
    public static void getColumnSubjectPager(int columnId, String cursor, Callback call) {
        Map<String, String> map = new HashMap<>();
        map.put("token", BaseApplication.getInstance().getUserInfo().getToken());
        map.put("columnId", columnId + "");
        map.put("cursor", cursor);
        map.put("size", "10");
        OkHttpUtils.post()
                .headers(HeadersUtils.getHeaders(map, APIUrls.column_getColumnSubjectPager))
                .params(map).url(APIUrls.column_getColumnSubjectPager).tag(APIUrls.column_getColumnSubjectPager).build().execute(call);

    }

    /**
     * 栏目专题分页
     *
     * @param columnId
     * @param cursor
     * @param call
     */
    public static void getColumnAllPager(int columnId, String cursor, Callback call) {
        Map<String, String> map = new HashMap<>();
        map.put("token", BaseApplication.getInstance().getUserInfo().getToken());
        map.put("columnId", columnId + "");
        map.put("cursor", cursor);
        map.put("size", "10");
        OkHttpUtils.post()
                .headers(HeadersUtils.getHeaders(map, APIUrls.column_getColumnAllPager))
                .params(map).url(APIUrls.column_getColumnAllPager).tag(APIUrls.column_getColumnAllPager).build().execute(call);

    }

    /**
     * 获取省市
     *
     * @param call
     */
    public static void getProvinceCityList(Callback call) {
        Map<String, String> map = new HashMap<>();
        map.put("token", BaseApplication.getInstance().getUserInfo().getToken());
        OkHttpUtils.post()
                .headers(HeadersUtils.getHeaders(map, APIUrls.getProvinceCityList))
                .params(map).url(APIUrls.getProvinceCityList).tag(APIUrls.getProvinceCityList).build().execute(call);

    }

    /**
     * 获取市
     *
     * @param call
     */
    public static void getCityList(Callback call) {
        Map<String, String> map = new HashMap<>();
        map.put("token", BaseApplication.getInstance().getUserInfo().getToken());
        OkHttpUtils.post()
                .headers(HeadersUtils.getHeaders(map, APIUrls.getCityList))
                .params(map).url(APIUrls.getCityList).tag(APIUrls.getCityList).build().execute(call);

    }


    /*
    ======================================  媒资  ================================================
     */

//    public static void mediaasset_getColumnPager(int columnId, String cursor, Callback call) {
//        Map<String, String> map = new HashMap<>();
//        map.put("token", BaseApplication.getInstance().getUserInfo().getToken());
//        map.put("columnId", columnId + "");
//        map.put("cursor", cursor);
//        OkHttpUtils.post().params(map).url(APIUrls.mediaasset_getColumnPager).tag(APIUrls.mediaasset_getColumnPager).build().execute(call);
//
//    }
//
//
//    public static void mediaasset_getColumnMediaAssets(int columnId, int size, Callback call) {
//        Map<String, String> map = new HashMap<>();
//        map.put("token", BaseApplication.getInstance().getUserInfo().getToken());
//        map.put("columnId", columnId + "");
//        map.put("size", size + "");
//        OkHttpUtils.post().params(map).url(APIUrls.mediaasset_getColumnMediaAssets).tag(APIUrls.mediaasset_getColumnMediaAssets).build().execute(call);
//
//    }


    public static void mediaasset_getSearchPager(String title, String cursor, Callback call) {
        Map<String, String> map = new HashMap<>();
        map.put("token", BaseApplication.getInstance().getUserInfo().getToken());
        map.put("title", title);
        map.put("cursor", cursor);
        OkHttpUtils.post()
                .headers(HeadersUtils.getHeaders(map, APIUrls.mediaasset_getSearchPager))
                .params(map).url(APIUrls.mediaasset_getSearchPager).tag(APIUrls.mediaasset_getSearchPager).build().execute(call);

    }

    public static void mediaasset_getSearchRadioList(String title, Callback call) {
        Map<String, String> map = new HashMap<>();
        map.put("token", BaseApplication.getInstance().getUserInfo().getToken());
        map.put("title", title);
        OkHttpUtils.post()
                .headers(HeadersUtils.getHeaders(map, APIUrls.mediaasset_getSearchRadioList))
                .params(map).url(APIUrls.mediaasset_getSearchRadioList).tag(APIUrls.mediaasset_getSearchRadioList).build().execute(call);

    }

    public static void mediaasset_getSearchTvList(String title, Callback call) {
        Map<String, String> map = new HashMap<>();
        map.put("token", BaseApplication.getInstance().getUserInfo().getToken());
        map.put("title", title);
        OkHttpUtils.post()
                .headers(HeadersUtils.getHeaders(map, APIUrls.mediaasset_getSearchTvList))
                .params(map).url(APIUrls.mediaasset_getSearchTvList).tag(APIUrls.mediaasset_getSearchTvList).build().execute(call);

    }

    public static void mediaasset_load(int id, Callback call) {
        Map<String, String> map = new HashMap<>();
        map.put("token", BaseApplication.getInstance().getUserInfo().getToken());
        map.put("id", id + "");
        OkHttpUtils.post()
                .headers(HeadersUtils.getHeaders(map, APIUrls.mediaasset_load))
                .params(map).url(APIUrls.mediaasset_load).tag(APIUrls.mediaasset_load).build().execute(call);

    }

    public static void mediaasset_load(String id, Callback call) {
        Map<String, String> map = new HashMap<>();
        map.put("token", BaseApplication.getInstance().getUserInfo().getToken());
        map.put("id", id);
        Log.i(TAG, "mediaasset_load: " + map.toString());
        OkHttpUtils.post()
                .headers(HeadersUtils.getHeaders(map, APIUrls.mediaasset_load))
                .params(map).url(APIUrls.mediaasset_load).tag(APIUrls.mediaasset_load).build().execute(call);

    }

    public static void getItemPager(int maid, String cursor, String url, Callback call) {
        Map<String, String> map = new HashMap<>();
        map.put("token", BaseApplication.getInstance().getUserInfo().getToken());
        map.put("maid", maid + "");
        map.put("cursor", cursor);
        map.put("size", String.valueOf(50));
        OkHttpUtils.post()
                .headers(HeadersUtils.getHeaders(map, url))
                .params(map).url(url).tag(url).build().execute(call);

    }

    public static void getYuntingItemPager(Long albumId, String cursor, String url, Callback call) {
        Map<String, String> map = new HashMap<>();
        map.put("token", BaseApplication.getInstance().getUserInfo().getToken());
        map.put("albumId", albumId + "");
        map.put("cursor", cursor);
        map.put("size", String.valueOf(50));
        OkHttpUtils.post()
                .headers(HeadersUtils.getHeaders(map, url))
                .params(map).url(url).tag(url).build().execute(call);

    }


    public static void mediaassetitem_getItemPagerAsc(int maid, String cursor, Callback call) {
        Map<String, String> map = new HashMap<>();
        map.put("token", BaseApplication.getInstance().getUserInfo().getToken());
        map.put("maid", maid + "");
        map.put("cursor", cursor);
        OkHttpUtils.post()
                .headers(HeadersUtils.getHeaders(map, APIUrls.mediaassetitem_getItemPagerAsc))
                .params(map).url(APIUrls.mediaassetitem_getItemPagerAsc).tag(APIUrls.mediaassetitem_getItemPagerAsc).build().execute(call);

    }


    public static void recommend_getPager(String cursor, Callback call) {
        Map<String, String> map = new HashMap<>();
        map.put("token", BaseApplication.getInstance().getUserInfo().getToken());
        map.put("cursor", cursor);
        OkHttpUtils.post()
                .headers(HeadersUtils.getHeaders(map, APIUrls.recommend_getPager))
                .params(map).url(APIUrls.recommend_getPager).tag(APIUrls.recommend_getPager).build().execute(call);

    }

    public static void recommend_getAllPager(String cursor, Callback call) {
        Map<String, String> map = new HashMap<>();
        map.put("token", BaseApplication.getInstance().getUserInfo().getToken());
        map.put("cursor", cursor);
        OkHttpUtils.post()
                .headers(HeadersUtils.getHeaders(map, APIUrls.recommend_getAllPager))
                .params(map).url(APIUrls.recommend_getAllPager).tag(APIUrls.recommend_getAllPager).build().execute(call);

    }


    /*
    ======================================  媒资  ================================================
     */

    public static void systemmsg_getPager(String cursor, Callback call) {
        Map<String, String> map = new HashMap<>();
        map.put("token", BaseApplication.getInstance().getUserInfo().getToken());
        map.put("cursor", cursor);
        OkHttpUtils.post()
                .headers(HeadersUtils.getHeaders(map, APIUrls.systemmsg_getPager))
                .params(map).url(APIUrls.systemmsg_getPager).tag(APIUrls.systemmsg_getPager).build().execute(call);

    }

    public static void suggestion_add(String content, String imgsJson, Callback call) {
        Map<String, String> map = new HashMap<>();
        map.put("token", BaseApplication.getInstance().getUserInfo().getToken());
        map.put("content", content);
        map.put("imgsJson", imgsJson);
        Log.i(TAG, "suggestion_add: " + map.toString());
        OkHttpUtils.post()
                .headers(HeadersUtils.getHeaders(map, APIUrls.suggestion_add))
                .params(map).url(APIUrls.suggestion_add).tag(APIUrls.suggestion_add).build().execute(call);

    }

    /*
    ======================================  媒资  ================================================
     */

    public static void subject_getSubjectPager(long subjectId, String cursor, Callback call) {
        Map<String, String> map = new HashMap<>();
        map.put("token", BaseApplication.getInstance().getUserInfo().getToken());
        map.put("subjectId", subjectId + "");
        map.put("cursor", cursor);
        OkHttpUtils.post()
                .headers(HeadersUtils.getHeaders(map, APIUrls.subject_getSubjectPager))
                .params(map).url(APIUrls.subject_getSubjectPager).tag(APIUrls.subject_getSubjectPager).build().execute(call);

    }

    public static void subject_load(long id, Callback call) {
        Map<String, String> map = new HashMap<>();
        map.put("token", BaseApplication.getInstance().getUserInfo().getToken());
        map.put("id", id + "");
        OkHttpUtils.post()
                .headers(HeadersUtils.getHeaders(map, APIUrls.subject_load))
                .params(map).url(APIUrls.subject_load).tag(APIUrls.subject_load).build().execute(call);

    }



    /*
    ======================================  收藏  ================================================
     */

    /**
     * 收藏
     *
     * @param type   1:媒资 2:栏目 3:电视台 4:电台
     * @param typeId
     * @param call
     */
    public static void favorite_add(int type, int typeId, Callback call) {
        Map<String, String> map = new HashMap<>();
        map.put("token", BaseApplication.getInstance().getUserInfo().getToken());
        map.put("type", String.valueOf(type));
        map.put("typeId", String.valueOf(typeId));
        OkHttpUtils.post()
                .headers(HeadersUtils.getHeaders(map, APIUrls.favorite_add))
                .params(map).url(APIUrls.favorite_add).tag(APIUrls.favorite_add).build().execute(call);
    }

    /**
     * 取消收藏
     *
     * @param id
     * @param call
     */
    public static void favorite_delete(int id, Callback call) {
        Map<String, String> map = new HashMap<>();
        map.put("token", BaseApplication.getInstance().getUserInfo().getToken());
        map.put("id", String.valueOf(id));
        OkHttpUtils.post()
                .headers(HeadersUtils.getHeaders(map, APIUrls.favorite_delete))
                .params(map).url(APIUrls.favorite_delete).tag(APIUrls.favorite_delete).build().execute(call);
    }

    /**
     * 取消收藏
     *
     * @param type 1:媒资 2:栏目 3:电视台 4:电台
     * @param call
     */
    public static void favorite_refresh(int type, String typeIdsJson, Callback call) {
        Map<String, String> map = new HashMap<>();
        map.put("token", BaseApplication.getInstance().getUserInfo().getToken());
        map.put("type", String.valueOf(type));
        map.put("typeIdsJson", typeIdsJson);
        OkHttpUtils.post()
                .headers(HeadersUtils.getHeaders(map, APIUrls.favorite_refresh))
                .params(map).url(APIUrls.favorite_refresh).tag(APIUrls.favorite_refresh).build().execute(call);
    }

    /**
     * 获取全部收藏
     *
     * @param type 1:媒资 2:栏目 3:电视台 4:电台
     * @param call
     */
    public static void favorite_getAllList(int type, Callback call) {
        Map<String, String> map = new HashMap<>();
        map.put("token", BaseApplication.getInstance().getUserInfo().getToken());
        map.put("type", String.valueOf(type));
        OkHttpUtils.post()
                .headers(HeadersUtils.getHeaders(map, APIUrls.favorite_getAllList))
                .params(map).url(APIUrls.favorite_getAllList).tag(APIUrls.favorite_getAllList).build().execute(call);
    }

    /**
     * 获取全部收藏
     *
     * @param type 1:媒资 2:栏目 3:电视台 4:电台
     * @param call
     */
    public static void favorite_getPager(int type, String cursor, Callback call) {
        Map<String, String> map = new HashMap<>();
        map.put("token", BaseApplication.getInstance().getUserInfo().getToken());
        map.put("type", String.valueOf(type));
        map.put("cursor", cursor);
        OkHttpUtils.post()
                .headers(HeadersUtils.getHeaders(map, APIUrls.favorite_getPager))
                .params(map).url(APIUrls.favorite_getPager).tag(APIUrls.favorite_getPager).build().execute(call);
    }


    public static void browse_getPager(int type, int subType, String cursor, Callback call) {
        Map<String, String> map = new HashMap<>();
        map.put("token", BaseApplication.getInstance().getUserInfo().getToken());
        map.put("type", String.valueOf(type));
        map.put("subType", String.valueOf(subType));
        map.put("cursor", cursor);
        OkHttpUtils.post()
                .headers(HeadersUtils.getHeaders(map, APIUrls.browse_getPager))
                .params(map).url(APIUrls.browse_getPager).tag(APIUrls.browse_getPager).build().execute(call);
    }


    public static void browse_getAllList(int type, int subType, Callback call) {
        Map<String, String> map = new HashMap<>();
        map.put("token", BaseApplication.getInstance().getUserInfo().getToken());
        map.put("type", String.valueOf(type));
        map.put("subType", String.valueOf(subType));
        OkHttpUtils.post()
                .headers(HeadersUtils.getHeaders(map, APIUrls.browse_getAllList))
                .params(map).url(APIUrls.browse_getAllList).tag(APIUrls.browse_getAllList).build().execute(call);
    }


    /**
     * 获得配置信息
     *
     * @param call
     */
    public static void getListByType(Callback call) {
        Map<String, String> map = new HashMap<>();
        map.put("token", BaseApplication.getInstance().getUserInfo().getToken());
        OkHttpUtils.getInstance().post()
                .headers(HeadersUtils.getHeaders(map, APIUrls.dictionary_getList))
                .params(map).url(APIUrls.dictionary_getList).tag(APIUrls.dictionary_getList).build().execute(call);

    }


    /**
     * ======================================  中国视听  ================================================
     */

}
