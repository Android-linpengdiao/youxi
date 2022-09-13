package com.base;

import com.ecity.android.tinypinyin.Pinyin;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

public class FriendBean  implements Serializable, Comparator<FriendBean> {
    /**
     * id : 11
     * rand : -1
     * userType : 0
     * userRank : 0
     * phone : +86-13521614827
     * rids : []
     * sid : 0
     * status : 0
     * checkStatus : 0
     * name : 用户+86-135****4827
     * icon : 123456
     * sex : 0
     * birthdate : 2020-09-28
     * age : 0
     * allSupportNum : 0
     * score : 0
     * country : 0
     * provinceId : 0
     * cityId : 0
     * districtId : 0
     * longitude : 0.0
     * latitude : 0.0
     * invitationUid : 0
     * push : 0
     * addFriendSetting : 0
     * ctime : 1601281115832
     * utime : 1601282552503
     * searchFriendSex : 0
     * searchFriendScope : 0
     * searchFriendMinAge : 0
     * searchFriendMaxAge : 0
     * searchFriendEmotionStatus : 0
     * remainingLikes : 0
     * favoritedNumber : 0
     * likesOtherNumbers : 0
     * releaseDynamicNumbers : 0
     * draftDynamicNumbers : 0
     * supportDynamicNumbers : 0
     * favoriteDynamicNumbers : 0
     * membersPurchasedNumbers : 0
     * token : jAJAxI8S4148TeyH9tyxROQEsNzxf9uqHU/etRMsIoc=
     */

    private int id;
    private String code;
    private int rand;
    private int userType;
    private int userRank;
    private String phone;
    private int sid;
    private int status;
    private int checkStatus;
    private String name;
    private String remark;
    private String icon;
    private int sex;
    private String info;
    private String birthdate;
    private String business;
    private String school;
    private String job;
    private String enBusiness;
    private String enJob;
    private String enSocialMind;
    private String enEmotionStatus;
    private String enAlternative;
    private String enSocialPreferences;
    private int age;
    private int friends;
    private int focused;
    private int superFocused;
    private int otherFocused;
    private int otherSuperFocused;
    private int allSupportNum;
    private int score;
    private int country;
    private int provinceId;
    private int cityId;
    private int districtId;
    private double longitude;
    private double latitude;
    private int invitationUid;
    private int push;
    private String rongToken;
    private int addFriendSetting;
    private long ctime;
    private long utime;
    private long endTime;
    private long lastLoginTime;
    private long fansNumber;
    private long focusNumbers;
    private int searchFriendSex;
    private int searchFriendScope;
    private int searchFriendMinAge;
    private int searchFriendMaxAge;
    private String searchFriendSocialMind;
    private String searchFriendEmotionStatus;
    private String searchFriendSocialPreferences;
    private int remainingLikes;
    private int favoritedNumber;
    private int likesOtherNumbers;
    private int releaseDynamicNumbers;
    private int draftDynamicNumbers;
    private int supportDynamicNumbers;
    private int favoriteDynamicNumbers;
    private int membersPurchasedNumbers;
    private String token;
    private List<?> rids;
    private String socialMind;
    private String emotionStatus;
    private String socialPreferences;
    private String setBackgroundImage;

    private List<String> album=new ArrayList<>();//相册
    private int hideActiveTime;//隐藏活跃时间
    private int hideLocation;//隐藏位置
    private List<FriendBean> hideUsers;//隐藏用户
    private int language;
    private List<String> tags=new ArrayList<>();//标签
    private List<String> socialAttributes=new ArrayList<>(); //社交属性
    private List<String> movieName=new ArrayList<>();//电影名字
    private List<String> musicName=new ArrayList<>();//音乐名称
    private List<String> bookName=new ArrayList<>();//书名称
    private List<String> foodName=new ArrayList<>();//美食名称
    private List<String> sportName=new ArrayList<>();//运动名称
    private List<String> enSocialAttributes=new ArrayList<>(); //社交属性
    private List<String> enMovieName=new ArrayList<>();//电影名字
    private List<String> enMusicName=new ArrayList<>();//音乐名称
    private List<String> enBookName=new ArrayList<>();//书名称
    private List<String> enFoodName=new ArrayList<>();//美食名称
    private List<String> enSportName=new ArrayList<>();//运动名称
    private int faceNum;
    private String pinyin;
    private int seat;//麦位
    private Integer clubMember;
    private Integer studentNum;

    public Integer getStudentNum() {
        return studentNum;
    }

    public void setStudentNum(Integer studentNum) {
        this.studentNum = studentNum;
    }

    public Integer getClubMember() {
        return clubMember;
    }

    public void setClubMember(Integer clubMember) {
        this.clubMember = clubMember;
    }

    public String getPinyin() {
        if (pinyin!=null && !pinyin.equals("")){
            return pinyin;
        }
        String nickname;
        if (getRemark() ==null || getRemark().equals("")){ //先判断 备注 是否为空,空 根据昵称 排序,否则已备注排序
            nickname = this.name;
        }else{
            nickname = getRemark();
        }
        if (nickname.equals("@") || nickname.equals("#")){
            return nickname;
        }
        StringBuffer buffer = new StringBuffer();
        for (int i=0;i<nickname.length();i++){
            String sortString = Pinyin.toPinyin(nickname.charAt(i));
            if (sortString.substring(0, 1).toUpperCase().matches("[A-Z]")) {
                buffer.append(sortString);
            }else{
                buffer.append(nickname.charAt(i));
            }
        }
        pinyin = buffer.toString().toUpperCase();
        if (Pattern.compile("[0-9]*").matcher(pinyin).matches()||!checkPingin(pinyin)) {
            pinyin = "#";
        }
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public static boolean checkPingin(String fstrData) {
        char c = fstrData.charAt(0);
        if (((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z'))) {
            return true;
        } else {
            return false;
        }
    }

    public int getSeat() {
        return seat;
    }

    public void setSeat(int seat) {
        this.seat = seat;
    }

    public int getFaceNum() {
        return faceNum;
    }

    public void setFaceNum(int faceNum) {
        this.faceNum = faceNum;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public long getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(long lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public long getFansNumber() {
        return fansNumber;
    }

    public void setFansNumber(long fansNumber) {
        this.fansNumber = fansNumber;
    }

    public long getFocusNumbers() {
        return focusNumbers;
    }

    public void setFocusNumbers(long focusNumbers) {
        this.focusNumbers = focusNumbers;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public String getSearchFriendSocialMind() {
        return searchFriendSocialMind;
    }

    public void setSearchFriendSocialMind(String searchFriendSocialMind) {
        this.searchFriendSocialMind = searchFriendSocialMind;
    }

    public String getSearchFriendEmotionStatus() {
        return searchFriendEmotionStatus;
    }

    public void setSearchFriendEmotionStatus(String searchFriendEmotionStatus) {
        this.searchFriendEmotionStatus = searchFriendEmotionStatus;
    }

    public String getSearchFriendSocialPreferences() {
        return searchFriendSocialPreferences;
    }

    public void setSearchFriendSocialPreferences(String searchFriendSocialPreferences) {
        this.searchFriendSocialPreferences = searchFriendSocialPreferences;
    }

    private int remainingFocus;
    private int remainingSuperFocus;

    public int getRemainingFocus() {
        return remainingFocus;
    }

    public void setRemainingFocus(int remainingFocus) {
        this.remainingFocus = remainingFocus;
    }


    public int getRemainingSuperFocus() {
        return remainingSuperFocus;
    }

    public void setRemainingSuperFocus(int remainingSuperFocus) {
        this.remainingSuperFocus = remainingSuperFocus;
    }

    public String getSetBackgroundImage() {
        return setBackgroundImage;
    }

    public void setSetBackgroundImage(String setBackgroundImage) {
        this.setBackgroundImage = setBackgroundImage;
    }

    public String getSocialMind() {
        return socialMind;
    }

    public void setSocialMind(String socialMind) {
        this.socialMind = socialMind;
    }

    public String getEmotionStatus() {
        return emotionStatus;
    }

    public void setEmotionStatus(String emotionStatus) {
        this.emotionStatus = emotionStatus;
    }

    public String getSocialPreferences() {
        return socialPreferences;
    }

    public void setSocialPreferences(String socialPreferences) {
        this.socialPreferences = socialPreferences;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public int getRand() {
        return rand;
    }

    public void setRand(int rand) {
        this.rand = rand;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public int getUserRank() {
        return userRank;
    }

    public void setUserRank(int userRank) {
        this.userRank = userRank;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getSid() {
        return sid;
    }

    public void setSid(int sid) {
        this.sid = sid;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getCheckStatus() {
        return checkStatus;
    }

    public void setCheckStatus(int checkStatus) {
        this.checkStatus = checkStatus;
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

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getBusiness() {
        return business;
    }

    public void setBusiness(String business) {
        this.business = business;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getFriends() {
        return friends;
    }

    public void setFriends(int friends) {
        this.friends = friends;
    }

    public int getFocused() {
        return focused;
    }

    public void setFocused(int focused) {
        this.focused = focused;
    }

    public int getSuperFocused() {
        return superFocused;
    }

    public void setSuperFocused(int superFocused) {
        this.superFocused = superFocused;
    }

    public int getOtherFocused() {
        return otherFocused;
    }

    public void setOtherFocused(int otherFocused) {
        this.otherFocused = otherFocused;
    }

    public int getOtherSuperFocused() {
        return otherSuperFocused;
    }

    public void setOtherSuperFocused(int otherSuperFocused) {
        this.otherSuperFocused = otherSuperFocused;
    }

    public int getAllSupportNum() {
        return allSupportNum;
    }

    public void setAllSupportNum(int allSupportNum) {
        this.allSupportNum = allSupportNum;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getCountry() {
        return country;
    }

    public void setCountry(int country) {
        this.country = country;
    }

    public int getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public int getDistrictId() {
        return districtId;
    }

    public void setDistrictId(int districtId) {
        this.districtId = districtId;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public int getInvitationUid() {
        return invitationUid;
    }

    public void setInvitationUid(int invitationUid) {
        this.invitationUid = invitationUid;
    }

    public int getPush() {
        return push;
    }

    public void setPush(int push) {
        this.push = push;
    }

    public String getRongToken() {
        return rongToken;
    }

    public void setRongToken(String rongToken) {
        this.rongToken = rongToken;
    }

    public int getAddFriendSetting() {
        return addFriendSetting;
    }

    public void setAddFriendSetting(int addFriendSetting) {
        this.addFriendSetting = addFriendSetting;
    }

    public long getCtime() {
        return ctime;
    }

    public void setCtime(long ctime) {
        this.ctime = ctime;
    }

    public long getUtime() {
        return utime;
    }

    public void setUtime(long utime) {
        this.utime = utime;
    }

    public int getSearchFriendSex() {
        return searchFriendSex;
    }

    public void setSearchFriendSex(int searchFriendSex) {
        this.searchFriendSex = searchFriendSex;
    }

    public int getSearchFriendScope() {
        return searchFriendScope;
    }

    public void setSearchFriendScope(int searchFriendScope) {
        this.searchFriendScope = searchFriendScope;
    }

    public int getSearchFriendMinAge() {
        return searchFriendMinAge;
    }

    public void setSearchFriendMinAge(int searchFriendMinAge) {
        this.searchFriendMinAge = searchFriendMinAge;
    }

    public int getSearchFriendMaxAge() {
        return searchFriendMaxAge;
    }

    public void setSearchFriendMaxAge(int searchFriendMaxAge) {
        this.searchFriendMaxAge = searchFriendMaxAge;
    }

    public int getRemainingLikes() {
        return remainingLikes;
    }

    public void setRemainingLikes(int remainingLikes) {
        this.remainingLikes = remainingLikes;
    }

    public int getFavoritedNumber() {
        return favoritedNumber;
    }

    public void setFavoritedNumber(int favoritedNumber) {
        this.favoritedNumber = favoritedNumber;
    }

    public int getLikesOtherNumbers() {
        return likesOtherNumbers;
    }

    public void setLikesOtherNumbers(int likesOtherNumbers) {
        this.likesOtherNumbers = likesOtherNumbers;
    }

    public int getReleaseDynamicNumbers() {
        return releaseDynamicNumbers;
    }

    public void setReleaseDynamicNumbers(int releaseDynamicNumbers) {
        this.releaseDynamicNumbers = releaseDynamicNumbers;
    }

    public int getDraftDynamicNumbers() {
        return draftDynamicNumbers;
    }

    public void setDraftDynamicNumbers(int draftDynamicNumbers) {
        this.draftDynamicNumbers = draftDynamicNumbers;
    }

    public int getSupportDynamicNumbers() {
        return supportDynamicNumbers;
    }

    public void setSupportDynamicNumbers(int supportDynamicNumbers) {
        this.supportDynamicNumbers = supportDynamicNumbers;
    }

    public int getFavoriteDynamicNumbers() {
        return favoriteDynamicNumbers;
    }

    public void setFavoriteDynamicNumbers(int favoriteDynamicNumbers) {
        this.favoriteDynamicNumbers = favoriteDynamicNumbers;
    }

    public int getMembersPurchasedNumbers() {
        return membersPurchasedNumbers;
    }

    public void setMembersPurchasedNumbers(int membersPurchasedNumbers) {
        this.membersPurchasedNumbers = membersPurchasedNumbers;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public List<?> getRids() {
        return rids;
    }

    public void setRids(List<?> rids) {
        this.rids = rids;
    }

    public int getHideActiveTime() {
        return hideActiveTime;
    }

    public void setHideActiveTime(int hideActiveTime) {
        this.hideActiveTime = hideActiveTime;
    }

    public int getHideLocation() {
        return hideLocation;
    }

    public void setHideLocation(int hideLocation) {
        this.hideLocation = hideLocation;
    }

    public List<FriendBean> getHideUsers() {
        return hideUsers;
    }

    public void setHideUsers(List<FriendBean> hideUsers) {
        this.hideUsers = hideUsers;
    }

    public int getLanguage() {
        return language;
    }

    public void setLanguage(int language) {
        this.language = language;
    }

    public List<String> getAlbum() {
        return album;
    }

    public void setAlbum(List<String> album) {
        this.album = album;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<String> getSocialAttributes() {
        return socialAttributes;
    }

    public void setSocialAttributes(List<String> socialAttributes) {
        this.socialAttributes = socialAttributes;
    }

    public List<String> getMovieName() {
        return movieName;
    }

    public void setMovieName(List<String> movieName) {
        this.movieName = movieName;
    }

    public List<String> getMusicName() {
        return musicName;
    }

    public void setMusicName(List<String> musicName) {
        this.musicName = musicName;
    }

    public List<String> getBookName() {
        return bookName;
    }

    public void setBookName(List<String> bookName) {
        this.bookName = bookName;
    }

    public List<String> getFoodName() {
        return foodName;
    }

    public void setFoodName(List<String> foodName) {
        this.foodName = foodName;
    }

    public List<String> getSportName() {
        return sportName;
    }

    public void setSportName(List<String> sportName) {
        this.sportName = sportName;
    }

    public String getEnBusiness() {
        return enBusiness;
    }

    public void setEnBusiness(String enBusiness) {
        this.enBusiness = enBusiness;
    }

    public String getEnJob() {
        return enJob;
    }

    public void setEnJob(String enJob) {
        this.enJob = enJob;
    }

    public String getEnSocialMind() {
        return enSocialMind;
    }

    public void setEnSocialMind(String enSocialMind) {
        this.enSocialMind = enSocialMind;
    }

    public String getEnEmotionStatus() {
        return enEmotionStatus;
    }

    public void setEnEmotionStatus(String enEmotionStatus) {
        this.enEmotionStatus = enEmotionStatus;
    }

    public String getEnAlternative() {
        return enAlternative;
    }

    public void setEnAlternative(String enAlternative) {
        this.enAlternative = enAlternative;
    }

    public String getEnSocialPreferences() {
        return enSocialPreferences;
    }

    public void setEnSocialPreferences(String enSocialPreferences) {
        this.enSocialPreferences = enSocialPreferences;
    }

    public List<String> getEnSocialAttributes() {
        return enSocialAttributes;
    }

    public void setEnSocialAttributes(List<String> enSocialAttributes) {
        this.enSocialAttributes = enSocialAttributes;
    }

    public List<String> getEnMovieName() {
        return enMovieName;
    }

    public void setEnMovieName(List<String> enMovieName) {
        this.enMovieName = enMovieName;
    }

    public List<String> getEnMusicName() {
        return enMusicName;
    }

    public void setEnMusicName(List<String> enMusicName) {
        this.enMusicName = enMusicName;
    }

    public List<String> getEnBookName() {
        return enBookName;
    }

    public void setEnBookName(List<String> enBookName) {
        this.enBookName = enBookName;
    }

    public List<String> getEnFoodName() {
        return enFoodName;
    }

    public void setEnFoodName(List<String> enFoodName) {
        this.enFoodName = enFoodName;
    }

    public List<String> getEnSportName() {
        return enSportName;
    }

    public void setEnSportName(List<String> enSportName) {
        this.enSportName = enSportName;
    }

    @Override
    public int compare(FriendBean o1, FriendBean o2) {
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
