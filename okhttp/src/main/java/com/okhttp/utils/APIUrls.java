package com.okhttp.utils;

public class APIUrls {


    public final static String URL_DOMAIN = "http://web.huixinbaokeji.com";

    /**
     * ======================================  爱游  ================================================
     */

    public final static String URL_UPLOAD_FILES = "http://store.huixinbaokeji.com/storage/handle";

    public final static String focusfansAdd = URL_DOMAIN + "web/focusfans/add";
    public final static String focusfansDel = URL_DOMAIN + "web/focusfans/del";
    //获取我的关注列表
    public final static String getFocusPager = URL_DOMAIN + "web/focusfans/getFocusPager";
    //获取粉丝列表
    public final static String getFansPager = URL_DOMAIN + "web/focusfans/getFansPager";


    /**
     * ======================================  爱游  ================================================
     */

    /**
     * 登录 - 用户信息
     */
    //注册 ---- 发送注册验证码
    public final static String createAuthCodeOnReg = URL_DOMAIN + "/register/createAuthCodeOnReg";
    //注册 ---- 手机号密码注册
    public final static String registerByPhoneAndPassword = URL_DOMAIN + "/register/registerByPhoneAndPassword";
    //手机号密码登录
    public final static String login = URL_DOMAIN + "/login/login";
    //发送修改密码的验证码
    public final static String createUpdateAuthCode = URL_DOMAIN + "/web/user/createUpdateAuthCode";
    //修改密码
    public final static String updatePassword = URL_DOMAIN + "/web/user/updatePassword";
    //更新用户信息
    public final static String updateUserInfo = URL_DOMAIN + "/web/user/updateUserInfo";
    //发送忘记密码的验证码(未登录) 不传token(用)
    public final static String createForgotAuthCode = URL_DOMAIN + "/web/user/createForgotAuthCode";
    //忘记密码 (还未登录在,找回密码) 不传token(用)
    public final static String updatePasswordAndLogin = URL_DOMAIN + "/web/user/updatePasswordAndLogin";
    //获取用户信息
    public final static String getUserInfo = URL_DOMAIN + "/web/user/getUserInfo";
    //获取用户信息
    public final static String userLoad = URL_DOMAIN + "/web/user/load";
    //注销账号
    public final static String logout = URL_DOMAIN + "/web/user/logout";

    public final static String storage_handle = URL_DOMAIN + "/storage/handle";

    /**
     * 轮播图
     */
    //获取栏目轮播图
    public final static String getColumnBanners = URL_DOMAIN + "/banner/getColumnBanners";
    public final static String getRecommendBanners = URL_DOMAIN + "/banner/getRecommendBanners";

    /**
     * 栏目
     */
    //获取栏目列表
    public final static String column_getAllList = URL_DOMAIN + "/column/getAllList";
    //获取栏目详情
    public final static String column_load = URL_DOMAIN + "/column/load";
    //栏目媒资分页
    public final static String column_getColumnPager = URL_DOMAIN + "/column/getColumnPager";
    //获取栏目媒资列表
    public final static String column_getColumnList = URL_DOMAIN + "/column/getColumnList";
    //获取栏目所有内容列表
    public final static String column_getColumnAllList = URL_DOMAIN + "/column/getColumnAllList";
    public final static String column_getColumntMergeAllList = URL_DOMAIN + "/column/getColumntMergeAllList";
    public final static String column_getColumnRowList = URL_DOMAIN + "/column/getColumnRowList";

    //栏目媒资分页
    public final static String column_getColumnSubjectPager = URL_DOMAIN + "/column/getColumnSubjectPager";
    public final static String column_getColumnAllPager = URL_DOMAIN + "/column/getColumnAllPager";

    //获取省市
    public final static String getProvinceCityList = URL_DOMAIN + "/provincecity/getProvinceCityList";
    public final static String getCityList = URL_DOMAIN + "/provincecity/getCityList";


    /**
     * 媒资
     */
    //获取专题详情
    public final static String subject_load = URL_DOMAIN + "/subject/load";
    //专题媒资分页
    public final static String subject_getSubjectPager = URL_DOMAIN + "/subject/getSubjectPager";

    /**
     * 媒资
     */
//    //栏目媒资分页
//    public final static String mediaasset_getColumnPager = URL_DOMAIN + "/mediaasset/getColumnPager";
//    //获取栏目媒资列表
//    public final static String mediaasset_getColumnMediaAssets = URL_DOMAIN + "/mediaasset/getColumnMediaAssets";


    //搜索媒资
    public final static String mediaasset_getSearchPager = URL_DOMAIN + "/mediaasset/getSearchPager";
    //搜索电台
    public final static String mediaasset_getSearchRadioList = URL_DOMAIN + "/mediaasset/getSearchRadioList";
    //搜索电视台
    public final static String mediaasset_getSearchTvList = URL_DOMAIN + "/mediaasset/getSearchTvList";
    //媒资详情
    public final static String mediaasset_load = URL_DOMAIN + "/mediaasset/load";

    //媒资条目升序分页
    public final static String mediaassetitem_getItemPagerAsc = URL_DOMAIN + "/mediaassetitem/getItemPagerAsc";
    //媒资条目降序分页
    public final static String mediaassetitem_getItemPagerDesc = URL_DOMAIN + "/mediaassetitem/getItemPagerDesc";

    //获取单曲分页正序
    public final static String yunting_getSinglePagerAsc = URL_DOMAIN + "/yunting/getSinglePagerAsc";
    //获取单曲分页倒序
    public final static String yunting_getSinglePagerDesc = URL_DOMAIN + "/yunting/getSinglePagerDesc";


    //推荐
    public final static String recommend_getPager = URL_DOMAIN + "/recommend/getPager";
    public final static String recommend_getAllPager = URL_DOMAIN + "/recommend/getAllPager";


    /**
     * 我的
     */
    public final static String systemmsg_getPager = URL_DOMAIN + "/systemmsg/getPager";
    //意见反馈
    public final static String suggestion_add = URL_DOMAIN + "/suggestion/add";


    /**
     * 收藏
     */
    //添加收藏
    public final static String favorite_add = URL_DOMAIN + "/favorite/add";
    //取消收藏
    public final static String favorite_delete = URL_DOMAIN + "/favorite/delete";
    //刷新收藏
    public final static String favorite_refresh = URL_DOMAIN + "/favorite/refresh";
    //获取全部收藏
    public final static String favorite_getAllList = URL_DOMAIN + "/favorite/getAllList";
    //收藏分页
    public final static String favorite_getPager = URL_DOMAIN + "/favorite/getPager";
    //获取全部浏览记录
    public final static String browse_getAllList = URL_DOMAIN + "/browse/getAllList";
    //浏览记录分页
    public final static String browse_getPager = URL_DOMAIN + "/browse/getPager";

    //获得配置信息
    public final static String dictionary_getList = URL_DOMAIN + "/dictionary/getList";


    /**
     * ======================================  爱游  ================================================
     */

}
