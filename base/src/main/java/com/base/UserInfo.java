package com.base;

import com.ecity.android.tinypinyin.Pinyin;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.Comparator;
import java.util.regex.Pattern;

public class UserInfo implements Serializable, Comparator<UserInfo> {

    public String id = ""; //id
    public String phone = ""; //手机号
    public String name = ""; //昵称
    public String remark = ""; //备注
    public String token = ""; //
    public String icon = ""; //图片地址
    public String info = ""; //签名
    public String money = ""; //钱
    public int diamond = 0;//
    public int status = 0; //状态
    public int friends = 0;//是否是好友 1
    public int sex = 1;//性别
    public String number = "";// 鸡毛信号
    public String utime = "";
    public String ctime = "";
    public int check = 1; //0没有；1未选；2选中；3不可选
    public int addFriendSetting = 0; //0:好友验证，1不需要验证
    public String inviteInfo = "";
    private int mute = 0;//0解禁，1禁言



    public boolean link = false;
    public String password; //
    public String initialLetter = "";
    public String type = Constants.FRAGMENT_FRIEND;

    public static UserInfo getVideoInstanceFromJson(JSONObject json) {
        UserInfo userInfo = new UserInfo();
        userInfo.id = json.optString("id");
        userInfo.icon = json.optString("icon");
        return userInfo;
    }

    public static UserInfo getInstanceFromJson(JSONObject json) {

        UserInfo userInfo = new UserInfo();
        userInfo.id = json.optString("id");
        userInfo.token = json.optString("token");
        userInfo.diamond = json.optInt("diamond");
        userInfo.money = json.optString("money");
        userInfo.status = json.optInt("status");
        userInfo.friends = json.optInt("friends");
        userInfo.sex = json.optInt("sex");
        userInfo.utime = json.optString("utime");
        userInfo.ctime = json.optString("ctime");

        userInfo.addFriendSetting = json.optInt("addFriendSetting");
        userInfo.inviteInfo = json.optString("inviteInfo");
        userInfo.mute = json.optInt("mute");

        userInfo.name = json.optString("name");
        userInfo.remark = json.optString("remark");
        userInfo.phone = json.optString("phone");
        userInfo.friends = json.optInt("friends");
        userInfo.icon = json.optString("icon");
        userInfo.info = json.optString("info");
        userInfo.number = json.optString("number");
        return userInfo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public int getDiamond() {
        return diamond;
    }

    public void setDiamond(int diamond) {
        this.diamond = diamond;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getFriends() {
        return friends;
    }

    public void setFriends(int friends) {
        this.friends = friends;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getUtime() {
        return utime;
    }

    public void setUtime(String utime) {
        this.utime = utime;
    }

    public String getCtime() {
        return ctime;
    }

    public void setCtime(String ctime) {
        this.ctime = ctime;
    }

    public int getCheck() {
        return check;
    }

    public void setCheck(int check) {
        this.check = check;
    }

    public int getAddFriendSetting() {
        return addFriendSetting;
    }

    public void setAddFriendSetting(int addFriendSetting) {
        this.addFriendSetting = addFriendSetting;
    }

    public String getInviteInfo() {
        return inviteInfo;
    }

    public void setInviteInfo(String inviteInfo) {
        this.inviteInfo = inviteInfo;
    }

    public int getMute() {
        return mute;
    }

    public void setMute(int mute) {
        this.mute = mute;
    }

    public boolean isLink() {
        return link;
    }

    public void setLink(boolean link) {
        this.link = link;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getInitialLetter() {
        return initialLetter;
    }

    public void setInitialLetter(String initialLetter) {
        this.initialLetter = initialLetter;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    private String pinyin;

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public String getPinyin() {
        if (pinyin != null && !pinyin.equals("")) {
            return pinyin;
        }
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < name.length(); i++) {
            String sortString = Pinyin.toPinyin(name.charAt(i));
            if (sortString.substring(0, 1).toUpperCase().matches("[A-Z]")) {
                buffer.append(sortString);
            } else {
                buffer.append(name.charAt(i));
            }
        }
        pinyin = buffer.toString().toUpperCase();
        if (Pattern.compile("[0-9]*").matcher(pinyin).matches() || !checkPinYin(pinyin)) {
            pinyin = "#";
        }
        return pinyin;
    }

    public static boolean checkPinYin(String fstrData) {
        char c = fstrData.charAt(0);
        if (((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z'))) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int compare(UserInfo o1, UserInfo o2) {
        if (o1.getPinyin().equals("@")
                || o2.getPinyin().equals("#")) {
            return -1;
        } else if (o1.getPinyin().equals("#")
                || o2.getPinyin().equals("@")) {
            return 1;
        } else {
            return o1.getPinyin().compareTo(o2.getPinyin());
        }
    }
}
