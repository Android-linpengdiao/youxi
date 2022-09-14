package com.yuoxi.android.app.loader;

import android.content.Context;
import android.widget.ImageView;

import com.base.view.GlideRoundTransform;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.youth.banner.loader.ImageLoader;
import com.yuoxi.android.app.R;


public class GlideImageLoader extends ImageLoader {


    public GlideImageLoader() {
    }

    @Override
    public void displayImage(Context context, Object path, ImageView imageView) {
        Glide.with(context.getApplicationContext())
//                .load(path)
                .load(R.drawable.ic_test_banner)
                .transform(new GlideRoundTransform(context, 5))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
    }

//    @Override
//    public ImageView createImageView(Context context) {
//        //圆角
//        return new RoundAngleImageView(context);
//    }
}
