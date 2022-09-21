package com.quakoo.im.utils;

import androidx.recyclerview.widget.DiffUtil;

import com.base.utils.CommonUtil;
import com.quakoo.im.model.ChatMessage;

import java.util.ArrayList;
import java.util.List;

//    原文：https://blog.csdn.net/zxt0601/article/details/52562770
public class MainMessageRecycleviewDiffUtil extends DiffUtil.Callback {

    private List<ChatMessage> NewList,OldList;

    public MainMessageRecycleviewDiffUtil(List<ChatMessage> newList, List<ChatMessage> oldList) {
        this.NewList = newList;
        this.OldList = oldList;
    }

    //旧数据源列表大小
    @Override
    public int getOldListSize() {
        return OldList==null?0:OldList.size();
    }
    //新数据源列表大小
    @Override
    public int getNewListSize() {
        return NewList==null?0:NewList.size();
    }
    /**
     * Called by the DiffUtil to decide whether two object represent the same Item.
     * 被DiffUtil调用，用来判断 两个对象是否是相同的Item。
     * For example, if your items have unique ids, this method should check their id equality.
     * 例如，如果你的Item有唯一的id字段，这个方法就 判断id是否相等。
     * 本例判断name字段是否一致
     *
     * @param oldItemPosition The position of the item in the old list
     * @param newItemPosition The position of the item in the new list
     * @return True if the two items represent the same object or false if they are different.
     */
    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        if (OldList.get(oldItemPosition).getFriendUser().getId().equals(NewList.get(newItemPosition).getFriendUser().getId())){
            return true;
        }
        return false;

    }
    /**
     * Called by the DiffUtil when it wants to check whether two items have the same data.
     * 被DiffUtil调用，用来检查 两个item是否含有相同的数据
     * DiffUtil uses this information to detect if the contents of an item has changed.
     * DiffUtil用返回的信息（true false）来检测当前item的内容是否发生了变化
     * DiffUtil uses this method to check equality instead of {@link Object#equals(Object)}
     * DiffUtil 用这个方法替代equals方法去检查是否相等。
     * so that you can change its behavior depending on your UI.
     * 所以你可以根据你的UI去改变它的返回值
     * For example, if you are using DiffUtil with a
     * {@link android.support.v7.widget.RecyclerView.Adapter RecyclerView.Adapter}, you should
     * return whether the items' visual representations are the same.
     * 例如，如果你用RecyclerView.Adapter 配合DiffUtil使用，你需要返回Item的视觉表现是否相同。
     * This method is called only if {@link #areItemsTheSame(int, int)} returns
     * {@code true} for these items.
     * 这个方法仅仅在areItemsTheSame()返回true时，才调用。
     * @param oldItemPosition The position of the item in the old list
     * @param newItemPosition The position of the item in the new list which replaces the
     *                        oldItem
     * @return True if the contents of the items are the same or false if they are different.
     */
    //false 不同, true 相同
    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        ChatMessage beanOld = OldList.get(oldItemPosition);
        ChatMessage beanNew = NewList.get(newItemPosition);
        boolean Result=true;
        if (!beanOld.getClientId().equals(beanNew.getClientId())){ //本地ID
            Result= false;
        }
        if (!CommonUtil.isBlank(beanOld.getContent()) && !CommonUtil.isBlank(beanNew.getContent()) &&!beanOld.getContent().equals(beanNew.getContent())){ //内容
            Result= false;
        }
        if (!CommonUtil.isBlank(beanOld.getSendMsgState()) && !CommonUtil.isBlank(beanNew.getSendMsgState()) && !beanOld.getSendMsgState().equals(beanNew.getSendMsgState())){//状态
            Result= false;
        }
        if (!CommonUtil.isBlank(beanOld.getUnreadCount()) &&!CommonUtil.isBlank(beanNew.getUnreadCount()) && beanOld.getUnreadCount()!=beanNew.getUnreadCount()){ // 未读消息个数
            Result= false;
        }
        if (beanOld.getSettingEntity()==null && beanNew.getSettingEntity()!=null){
            Result= false;
        }else if (beanOld.getSettingEntity()!=null && beanNew.getSettingEntity()==null){
            Result= false;
        }else if (beanOld.getSettingEntity()==null && beanNew.getSettingEntity()==null){
            Result= true;
        }
        if ((beanOld.getSettingEntity()!=null && beanNew.getSettingEntity()!=null)&&!beanOld.getSettingEntity().IsSame(beanNew.getSettingEntity())){//好友设置
            Result= false;
        }
        return Result;
    }
    /**
     * When {@link #areItemsTheSame(int, int)} returns {@code true} for two items and
     * {@link #areContentsTheSame(int, int)} returns false for them, DiffUtil
     * calls this method to get a payload about the change.
     *
     * 当{@link #areItemsTheSame(int, int)} 返回true，且{@link #areContentsTheSame(int, int)} 返回false时，DiffUtils会回调此方法，
     * 去得到这个Item（有哪些）改变的payload。
     *
     * particular field that changed in the item and your
     * {@link android.support.v7.widget.RecyclerView.ItemAnimator ItemAnimator} can use that
     * information to run the correct animation.
     *
     * 例如，如果你用RecyclerView配合DiffUtils，你可以返回  这个Item改变的那些字段，
     * {@link android.support.v7.widget.RecyclerView.ItemAnimator ItemAnimator} 可以用那些信息去执行正确的动画
     *
     * Default implementation returns {@code null}.\
     * 默认的实现是返回null
     *
     * @param oldItemPosition The position of the item in the old list
     * @param newItemPosition The position of the item in the new list
     * @return A payload object that represents the change between the two items.
     * 返回 一个 代表着新老item的改变内容的 payload对象，
     */
//    @Override

    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        ChatMessage newBean = NewList.get(newItemPosition);
        if (!areContentsTheSame(oldItemPosition, newItemPosition)) {
            List list = new ArrayList();
            list.add(newBean);
            return list;
        }
        return null;
    }
}
