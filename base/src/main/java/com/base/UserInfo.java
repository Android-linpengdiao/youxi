package com.base;

import android.util.Log;

import com.ecity.android.tinypinyin.Pinyin;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

public class UserInfo implements Serializable, Comparator<UserInfo> {

    private Long id;
    private String name;
    private String icon;
    private Long rand;
    private Long userType;
    private Long userRank;
    private String phone;
    private String password;
    private List<?> rids;
    private Long sid;
    private Long status;
    private Long sex;
    private Long age;
    private String birthdate;
    private Long invitationUid;
    private Long ctime;
    private Long utime;
    private Long check;
    private Long push;
    private String token;
    private Boolean register;

    public UserInfo() {
    }

    public UserInfo(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Long getRand() {
        return rand;
    }

    public void setRand(Long rand) {
        this.rand = rand;
    }

    public Long getUserType() {
        return userType;
    }

    public void setUserType(Long userType) {
        this.userType = userType;
    }

    public Long getUserRank() {
        return userRank;
    }

    public void setUserRank(Long userRank) {
        this.userRank = userRank;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<?> getRids() {
        return rids;
    }

    public void setRids(List<?> rids) {
        this.rids = rids;
    }

    public Long getSid() {
        return sid;
    }

    public void setSid(Long sid) {
        this.sid = sid;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    public Long getSex() {
        return sex;
    }

    public void setSex(Long sex) {
        this.sex = sex;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public Long getAge() {
        return age;
    }

    public void setAge(Long age) {
        this.age = age;
    }

    public Long getInvitationUid() {
        return invitationUid;
    }

    public void setInvitationUid(Long invitationUid) {
        this.invitationUid = invitationUid;
    }

    public Long getCtime() {
        return ctime;
    }

    public void setCtime(Long ctime) {
        this.ctime = ctime;
    }

    public Long getUtime() {
        return utime;
    }

    public void setUtime(Long utime) {
        this.utime = utime;
    }

    public Long getCheck() {
        return check;
    }

    public void setCheck(Long check) {
        this.check = check;
    }

    public Long getPush() {
        return push;
    }

    public void setPush(Long push) {
        this.push = push;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Boolean getRegister() {
        return register;
    }

    public void setRegister(Boolean register) {
        this.register = register;
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
