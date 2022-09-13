package com.yuoxi.android.app.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.base.Constants;
import com.base.UserInfo;
import com.base.utils.CommonUtil;
import com.base.utils.MsgCache;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseRecyclerAdapter<T, B extends ViewDataBinding> extends RecyclerView.Adapter {

    protected Context mContext;
    protected List<T> mList = new ArrayList<>();

    public BaseRecyclerAdapter(Context context) {
        this.mContext = context;
    }

    public BaseRecyclerAdapter(Context context, List<T> list) {
        this.mContext = context;
        this.mList = list;
    }

    public List<T> getList() {
        return mList;
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    public void refreshData(List<T> list) {
        if (null != list) {
            this.mList = list;
            notifyDataSetChanged();
        }
    }

    public void loadMoreData(List<T> list) {
        if (null != list) {
            this.mList.addAll(list);
            notifyDataSetChanged();
        }
    }

    public void removeData(int position) {
        this.mList.remove(position);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        B bing = DataBindingUtil.inflate(LayoutInflater.from(mContext), getLayoutResId(viewType), parent, false);
        return new RecyclerHolder(bing.getRoot());
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        B binding = DataBindingUtil.getBinding(holder.itemView);
        final T t = mList.get(position);
        onBindItem(binding, t, position);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List payloads) {
        super.onBindViewHolder(holder, position, payloads);
    }

    protected abstract @LayoutRes
    int getLayoutResId(int viewType);

    protected abstract void onBindItem(B binding, T t, int position);

    static class RecyclerHolder extends RecyclerView.ViewHolder {
        public RecyclerHolder(View itemView) {
            super(itemView);
        }
    }

    public void openActivity(Class<?> mClass) {
        openActivity(mClass, null);
    }

    public void openActivity(Class<?> mClass, Bundle mBundle) {
        Intent intent = new Intent(mContext, mClass);
        if (mBundle != null) {
            intent.putExtras(mBundle);
        }
        mContext.startActivity(intent);
    }

    public UserInfo getUserInfo() {
        try {
            UserInfo userinfo = (UserInfo) MsgCache.get(mContext).getAsObject(Constants.USER_INFO);
            if (!CommonUtil.isBlank(userinfo)) {
                return userinfo;
            }
        }catch (Exception e){
            e.printStackTrace();
            return new UserInfo();
        }
        return new UserInfo();
    }

    public void setTypeface(TextView textView) {
//        Typeface typeface = BaseApplication.getInstance().getTypeface();
//        textView.setTypeface(typeface);
    }

}