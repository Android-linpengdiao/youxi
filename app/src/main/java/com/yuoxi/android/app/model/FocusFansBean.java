package com.yuoxi.android.app.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class FocusFansBean {

    private Integer uid;
    private Integer type;
    private Integer typeId;
    private Long ctime;
    private Long utime;
    private UserBean user;
    private TypeUserBean typeUser;
    private Integer focus;
    private Integer otherFocus;

    @NoArgsConstructor
    @Data
    public static class UserBean {
        private Integer id;
        private Integer userType;
        private Integer userRank;
        private String name;
        private String icon;
        private String phone;
        private Integer sex;
        private Integer friends;
        private Integer ctime;
    }

    @NoArgsConstructor
    @Data
    public static class TypeUserBean {
        private Integer id;
        private Integer userType;
        private Integer userRank;
        private String name;
        private String icon;
        private String phone;
        private Integer sex;
        private String info;
        private Integer friends;
        private Integer ctime;
    }
}
