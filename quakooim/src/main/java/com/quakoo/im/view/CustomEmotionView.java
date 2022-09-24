package com.quakoo.im.view;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.base.BaseApplication;
import com.base.utils.CommonUtil;
import com.base.utils.GlideLoader;
import com.quakoo.im.IMSharedPreferences.ImSharedPreferences;
import com.quakoo.im.R;
import com.quakoo.im.model.EmotionEntity;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by daniel on 15-7-7.
 */
public class CustomEmotionView extends RelativeLayout {

    private static final String TAG = "CustomEmotionView";

    private static final int EMOTION_NUM_PER_PAGE = 8;

    public interface OnCustomItemClickListener {

        void onCustomItemClicked(EmotionEntity.DataBean dataBean, int position);

    }

    private OnCustomItemClickListener onCustomItemClickListener = null;

    public void setOnItemClickListener(OnCustomItemClickListener l) {
        this.onCustomItemClickListener = l;
    }

    private ViewPager viewPager = null;

//    private CirclePageIndicator circlePageIndicator = null;

    private EmotionPagerAdapter emotionPagerAdapter = null;

    private List<EmotionEntity.DataBean> emotionList = new ArrayList<>();

    public CustomEmotionView(Context context, List<EmotionEntity.DataBean> emotionList) {
        super(context);
        init(context, emotionList);
    }

    public void refreshData(Context context, List<EmotionEntity.DataBean> emotionList) {
        removeView(viewPager);
//        removeView(circlePageIndicator);
        init(context, emotionList);
    }

    private int getInputHeight() {
//        int softInputHeight = CommonUtil.getSupportSoftInputHeight(IMChatActivity.activity);
//        if (softInputHeight <= 0) {
//            softInputHeight = PreferenceManager.getInstance().getSoftKeybardHeight();
//        }
//        return softInputHeight;

        //修改后
        if (BaseApplication.keyboardheight==0){
            return ImSharedPreferences.getKeyboardHeight();
        }else {
            return BaseApplication.keyboardheight;
        }
    }

    private void init(final Context context, List<EmotionEntity.DataBean> emotionList) {
        Log.d(TAG, "init: emotionList "+emotionList.size());
        this.emotionList.clear();
        this.emotionList = emotionList;
        int count = emotionList.size();

        int page = count / EMOTION_NUM_PER_PAGE;
        if (count % EMOTION_NUM_PER_PAGE != 0) {
            page++;
        }

        List<View> views = new ArrayList<View>();
        for (int i = 0; i < page; i++) {
            GridView gridView = new GridView(getContext());
            gridView.setNumColumns(4);
            gridView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
            gridView.setVerticalSpacing(CommonUtil.dip2px(getContext(), 8));
            gridView.setGravity(Gravity.CENTER);
            ViewPager.LayoutParams gridParamas = new ViewPager.LayoutParams();
            gridParamas.height = CommonUtil.dip2px(getContext(), getInputHeight());
            gridView.setLayoutParams(gridParamas);
            gridView.setSelector(R.color.transparent);
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // 表情按钮
                    EmotionEntity.DataBean dataBean = ((EmotionAdapter) parent.getAdapter()).getEmotionUrl(position);
                    if (dataBean.getUrl().equals("add")) {
//                        context.startActivity(new Intent(context,CustomEmotionActivity.class));
                    } else {
                        if (onCustomItemClickListener != null) {
                            onCustomItemClickListener.onCustomItemClicked(dataBean, position);
                        }
                    }
                }
            });

            EmotionAdapter adapter = new EmotionAdapter(context, EMOTION_NUM_PER_PAGE, (i + 1) * EMOTION_NUM_PER_PAGE);
            gridView.setAdapter(adapter);

            views.add(gridView);
        }


        this.viewPager = new ViewPager(getContext());
        this.viewPager.setOverScrollMode(ViewPager.OVER_SCROLL_NEVER);
        LayoutParams layoutParams = new LayoutParams(-1, -1);
        layoutParams.setMargins(0, CommonUtil.dip2px(getContext(), 8), 0, 0);
        this.viewPager.setLayoutParams(layoutParams);
        this.emotionPagerAdapter = new EmotionPagerAdapter(views);
        this.viewPager.setAdapter(emotionPagerAdapter);

//        this.circlePageIndicator = new CirclePageIndicator(getContext());
//        LayoutParams indicatorParams = new LayoutParams(-2, -2);
//        indicatorParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//        indicatorParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
//        indicatorParams.setMargins(0, 0, 0, CommonUtil.dip2px(getContext(), 8));
//        this.circlePageIndicator.setLayoutParams(indicatorParams);
//        int paddingSize = CommonUtil.dip2px(getContext(), 4);
//        this.circlePageIndicator.setPadding(paddingSize, paddingSize, paddingSize, paddingSize);
//        this.circlePageIndicator.setViewPager(viewPager);
//        this.circlePageIndicator.setFillColor(getResources().getColor(R.color.title_color_normal));
//        this.circlePageIndicator.setStrokeColor(getResources().getColor(R.color.title_color_normal));

        addView(viewPager);
//        addView(circlePageIndicator);
    }

    public class EmotionPagerAdapter extends PagerAdapter {

        private List<View> views = null;

        public EmotionPagerAdapter(List<View> views) {
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
                ((ViewGroup) this.views.get(position).getParent())
                        .removeView(this.views.get(position));
                ((ViewPager) container).addView(this.views.get(position), 0);
            }

            return views.get(position);
        }
    }

    public class EmotionAdapter extends BaseAdapter {

        // 表情总数
        private int totalEmotionCount;
        // 当前表情index
        private int emotionIndex;

        private List<EmotionEntity.DataBean> emotionUrl = new ArrayList<EmotionEntity.DataBean>();

        private Context context;

        public EmotionAdapter(Context context, int total, int index) {
            this.totalEmotionCount = total;
            this.emotionIndex = index;
            this.context = context;
            init();
        }

        // 初始化表情资源
        private void init() {
            try {
                int count = 0;
                count = emotionList.size();
                for (int i = emotionIndex - totalEmotionCount; i < emotionIndex; i++) {
                    if (i + 1 > count) {
                        // 表情个数
                        break;
                    }
                    EmotionEntity.DataBean value = emotionList.get((i));
                    this.emotionUrl.add(value);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public EmotionEntity.DataBean getEmotionUrl(int position) {
            return this.emotionUrl.get(position);
        }

        @Override
        public int getCount() {
            return emotionUrl.size();
        }

        @Override
        public Object getItem(int position) {
            return this.emotionUrl.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (null == convertView) {
                convertView = LayoutInflater.from(context).inflate(R.layout.widget_image_view, null);
                holder = new ViewHolder();
                holder.imageView = convertView.findViewById(R.id.imageView);
                int w = CommonUtil.dip2px(getContext(), 60);
                int top = CommonUtil.dip2px(getContext(), 30);
                holder.imageView.setLayoutParams(new GridView.LayoutParams(w, w));//设置ImageView对象布局
                holder.imageView.setAdjustViewBounds(false);//设置边界对齐
                holder.imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);//设置刻度的类型
                holder.imageView.setPadding(8, 8, 8, 8);//设置间距
                LayoutParams params = new LayoutParams(w, w);
                params.setMargins(0, top, 0, 0);
                holder.imageView.setLayoutParams(params);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            if (emotionUrl.get(position).getUrl().equals("add")) {
                GlideLoader.LoderDrawable(context, R.drawable.ic_group_user_add, holder.imageView);
            } else {
                GlideLoader.LoderCustomEmotionImage(context, emotionUrl.get(position).getUrl(), holder.imageView);
            }
            return convertView;
        }

    }

    static class ViewHolder {
        ImageView imageView;
    }

}
