package com.quakoo.im.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.quakoo.im.R;

import java.util.ArrayList;
import java.util.List;

public class EmotionPagerAdapter extends RecyclingPagerAdapter {

    private List<Integer> mImageView = new ArrayList<Integer>();
    private List<View> mlist = new ArrayList<View>();
    private Context mContext;

    public EmotionPagerAdapter(Context context,List<View> list, List<Integer> imageView) {
        this.mContext = context;
        this.mlist = list;
        this.mImageView = imageView;
    }

    @Override
    public int getCount() {
        return mlist.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup container) {
        return mlist.get(position);
    }


    public View getTabView(int position) {
        //首先为子tab布置一个布局
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_tab, null);
        ImageView iv = view.findViewById(R.id.tab_Dial_Img);
        iv.setImageResource(mImageView.get(position));
        return view;
    }

}
