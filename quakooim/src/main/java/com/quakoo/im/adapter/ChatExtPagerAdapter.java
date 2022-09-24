package com.quakoo.im.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class ChatExtPagerAdapter extends RecyclingPagerAdapter {

    private List<View> views = new ArrayList<View>();
    private Context mContext;

    public ChatExtPagerAdapter(Context context, List<View> views) {
        this.mContext = context;
        this.views = views;
    }

    @Override
    public int getCount() {
        return views.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup container) {
        return views.get(position);
    }

}
