package com.quakoo.im.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.base.BaseApplication;
import com.base.utils.CommonUtil;
import com.quakoo.im.IMSharedPreferences.ImSharedPreferences;
import com.quakoo.im.R;
import com.quakoo.im.utils.EmotionUtilsTemp;

import java.util.ArrayList;
import java.util.List;

public class ChatExtView extends RelativeLayout {

    private static final int NUM_PER_PAGE = 8;
    private int[] chatExtList;
    private ViewPager viewPager = null;
//    private CirclePageIndicator circlePageIndicator = null;
    private ChatExtPagerAdapter chatExtPagerAdapter;

    public interface OnChatExtClickListener {
        void onChatExtClick(String descr);
    }
    private OnChatExtClickListener onChatExtClickListener = null;

    public void setOnClickListener(OnChatExtClickListener l) {
        this.onChatExtClickListener = l;
    }

    public ChatExtView(Context context) {
        super(context);
        init(context);
    }

    public ChatExtView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ChatExtView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private int getInputHeight() {
        //修改后
        if (BaseApplication.keyboardheight==0){
            return ImSharedPreferences.getKeyboardHeight();
        }else {
            return BaseApplication.keyboardheight;
        }
    }

    private void init(Context context) {
        List<View> views = new ArrayList<View>();
        chatExtList = EmotionUtilsTemp.itemChatExtKey;
        int count = chatExtList.length;
        int page = chatExtList.length / NUM_PER_PAGE;
        if (count % NUM_PER_PAGE != 0) {
            page++;
        }
        for (int i = 0; i < page; i++) {
            GridView gridView = new GridView(getContext());
            gridView.setNumColumns(4);
            gridView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
            gridView.setVerticalSpacing(CommonUtil.dip2px(getContext(), 20));
            gridView.setGravity(Gravity.CENTER);
            ViewPager.LayoutParams gridParamas = new ViewPager.LayoutParams();
            gridParamas.height = getInputHeight();
            gridParamas.gravity = Gravity.CENTER;
            gridView.setLayoutParams(gridParamas);
            int paddingSize = CommonUtil.dip2px(getContext(), 20);
            gridView.setPadding(paddingSize, paddingSize + ((getInputHeight()-CommonUtil.dip2px(getContext(), 240)) / 2), paddingSize, paddingSize + ((getInputHeight()-CommonUtil.dip2px(getContext(), 240)) / 2));
            gridView.setSelector(R.color.transparent);
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String descr = ((ChatExtGridAdapter) parent.getAdapter()).getDescr(position);
                    if (onChatExtClickListener != null) {
                        onChatExtClickListener.onChatExtClick(descr);
                    }
                }
            });

            ChatExtGridAdapter adapter = new ChatExtGridAdapter(context, NUM_PER_PAGE, (i + 1) * NUM_PER_PAGE);
            gridView.setAdapter(adapter);

            views.add(gridView);
        }

        viewPager = new ViewPager(getContext());
        viewPager.setOverScrollMode(ViewPager.OVER_SCROLL_NEVER);
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        viewPager.setLayoutParams(layoutParams);
        chatExtPagerAdapter = new ChatExtPagerAdapter(views);
        viewPager.setAdapter(chatExtPagerAdapter);

//        circlePageIndicator = new CirclePageIndicator(getContext());
//        LayoutParams indicatorParams = new LayoutParams(-2, -2);
//        indicatorParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//        indicatorParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
//        circlePageIndicator.setLayoutParams(indicatorParams);
//        int paddingSize = CommonUtil.dip2px(getContext(), 20);
//        circlePageIndicator.setPadding(paddingSize, paddingSize, paddingSize, paddingSize);
//        circlePageIndicator.setViewPager(viewPager);
//        circlePageIndicator.setFillColor(getResources().getColor(R.color.title_color_normal));
//        circlePageIndicator.setStrokeColor(getResources().getColor(R.color.title_color_normal));

        addView(viewPager);
//        addView(circlePageIndicator);
    }

    public class ChatExtPagerAdapter extends PagerAdapter {

        private List<View> views = null;

        public ChatExtPagerAdapter(List<View> views) {
            this.views = views;
        }

        @Override
        public int getCount() {
            return this.views.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == (arg1);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager) container).removeView(this.views.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            if (this.views.get(position).getParent() == null) {
                ((ViewPager) container).addView(this.views.get(position), 0);
            } else {
                ((ViewGroup) this.views.get(position).getParent()).removeView(this.views.get(position));
                ((ViewPager) container).addView(this.views.get(position), 0);
            }

            return views.get(position);
        }
    }

    public class ChatExtGridAdapter extends BaseAdapter {

        // 表情总数
        private int total;
        // 当前表情index
        private int index;
        // 表情资源列表
        private List<Drawable> imgDrawable = new ArrayList<Drawable>();

        private List<String> descr = new ArrayList<String>();

        private Context context;

        public ChatExtGridAdapter(Context context, int total, int index) {
            this.total = total;
            this.index = index;
            this.context = context;
            init();
        }

        // 初始化表情资源
        private void init() {
            try {
                for (int i = index - total; i < index; i++) {
                    if (i + 1 > chatExtList.length) {
                        break;
                    }
                    this.descr.add(getResources().getString(chatExtList[i]));
                    this.imgDrawable.add(getResources().getDrawable(EmotionUtilsTemp.itemChatExtValue[i]));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public String getDescr(int position) {
            return this.descr.get(position);
        }

        @Override
        public int getCount() {
            return descr.size();
        }

        @Override
        public Object getItem(int position) {
            return this.descr.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (null == convertView) {
                convertView = LayoutInflater.from(context).inflate(R.layout.view_chat_ext_list_item, null);
                holder = new ViewHolder();
                holder.imageView = convertView.findViewById(R.id.imageView);
                holder.descr = convertView.findViewById(R.id.descr);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.descr.setText(descr.get(position));
            holder.imageView.setImageDrawable(imgDrawable.get(position));
            return convertView;

        }

    }

    static class ViewHolder {
        ImageView imageView;
        TextView descr;
    }
}
