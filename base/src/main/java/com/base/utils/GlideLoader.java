package com.base.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.ImageView;

import com.base.BaseApplication;
import com.base.R;
import com.base.manager.DialogManager;
import com.base.view.GlideBlurTransformation;
import com.base.view.GlideRoundTransform;
import com.base.view.RoundedCornersTransform;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.cache.ExternalCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.io.File;
import java.io.Serializable;
import java.math.BigDecimal;

public class GlideLoader {
    private static final String TAG = "GlideLoader";

    private static GlideLoader mInstance;

    private GlideLoader() {
    }

    public static GlideLoader getInstance() {
        if (mInstance == null) {
            synchronized (DialogManager.class) {
                if (mInstance == null) {
                    mInstance = new GlideLoader();
                }
            }
        }
        return mInstance;
    }

    public static void LoderAvatar(Context context, String url, ImageView view, int round) {
        RequestOptions options = new RequestOptions().transform(new GlideRoundTransform(context, round));
        Glide.with(context)
                .load(url)
                .placeholder(R.drawable.ic_default_icon)
                .error(R.drawable.ic_default_icon)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .apply(options)
                .into(view);
    }

    public void LoaderUserIcon(Context context, String url, ImageView view) {
        try {
            Glide.with(context)
                    .load(url + format(url))
                    .centerCrop()
                    .transform(new GlideRoundTransform(context, 100))
                    .placeholder(R.drawable.ic_default_icon)
                    .error(R.drawable.ic_default_icon)
                    .skipMemoryCache(skipMemoryCache())
                    .diskCacheStrategy(getDiskCacheStrategy())
                    .into(view);
        } catch (Exception e) {
            e.getMessage();
        }
    }

    public static void LoderChatImage(Context context, String url, ImageView view,int round) {
        if (!CommonUtil.isBlank(url)) {
            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .transform(new GlideRoundTransform(context, round))
                    .placeholder(R.drawable.image)
                    .error(R.drawable.image)
                    .diskCacheStrategy(DiskCacheStrategy.ALL);
            if (url.endsWith("gif")) {
                Glide.with(context)
                        .asGif()
                        .load(url)
                        .apply(options)
                        .into(view);
            } else {
                Glide.with(context)
                        .asBitmap()
                        .load(url)
                        .apply(options)
                        .into(view);
            }
        }
    }

    public void LoaderCover(Context context, String url, ImageView view) {
        try {
            Glide.with(context)
                    .load(url + format(url))
                    .centerCrop()
                    .placeholder(R.color.coverColor)
                    .error(R.color.coverColor)
                    .skipMemoryCache(skipMemoryCache())
                    .diskCacheStrategy(getDiskCacheStrategy())
                    .into(view);
        } catch (Exception e) {
            e.getMessage();
        }

    }


    public void LoaderVideoCover(Context context, String url, ImageView view) {
        try {
            Glide.with(context)
                    .load(url + format(url))
                    .centerCrop()
                    .placeholder(R.drawable.ic_default_cover_b)
                    .error(R.drawable.ic_default_cover_b)
                    .skipMemoryCache(skipMemoryCache())
                    .diskCacheStrategy(getDiskCacheStrategy())
                    .into(view);
        } catch (Exception e) {
            e.getMessage();
        }

    }

    public void LoaderVideoCoverSmall(Context context, String url, ImageView view) {
        try {
            Glide.with(context)
                    .load(url + format(url))
                    .centerCrop()
                    .placeholder(R.drawable.ic_default_cover_z)
                    .error(R.drawable.ic_default_cover_z)
                    .skipMemoryCache(skipMemoryCache())
                    .diskCacheStrategy(getDiskCacheStrategy())
                    .into(view);
        } catch (Exception e) {
            e.getMessage();
        }

    }

    public void LoaderAudioCover(Context context, String url, ImageView view) {
        try {
            Glide.with(context)
                    .load(url + format(url))
                    .centerCrop()
                    .placeholder(R.drawable.ic_default_cover_z)
                    .error(R.drawable.ic_default_cover_z)
                    .skipMemoryCache(skipMemoryCache())
                    .diskCacheStrategy(getDiskCacheStrategy())
                    .into(view);
        } catch (Exception e) {
            e.getMessage();
        }

    }

    public void LoaderVideoCoverGrid(Context context, String url, ImageView view, int rowNum) {
        try {
            Glide.with(context)
                    .load(url + format(url))
                    .centerCrop()
                    .placeholder(rowNum == 1 ? R.drawable.ic_default_cover_b : rowNum == 2 ? R.drawable.ic_default_cover_z : R.drawable.ic_default_cover_z)
                    .error(rowNum == 1 ? R.drawable.ic_default_cover_b : rowNum == 2 ? R.drawable.ic_default_cover_z : R.drawable.ic_default_cover_z)
                    .skipMemoryCache(skipMemoryCache())
                    .diskCacheStrategy(getDiskCacheStrategy())
                    .into(view);
        } catch (Exception e) {
            e.getMessage();
        }

    }

    public void LoaderDrawableVideoCover(Context context, int drawable, ImageView view) {
        try {
            Glide.with(context)
                    .load(drawable)
                    .centerCrop()
                    .placeholder(R.color.black)
                    .error(R.color.black)
                    .into(view);
        } catch (Exception e) {
            e.getMessage();
        }
    }


    public void LoaderRadioCover(Context context, String url, ImageView view) {
        try {
            Glide.with(context)
                    .load(url + format(url))
                    .fitCenter()
                    .placeholder(R.drawable.ic_default_cover_z)
                    .error(R.drawable.ic_default_cover_z)
                    .skipMemoryCache(skipMemoryCache())
                    .diskCacheStrategy(getDiskCacheStrategy())
                    .into(view);
        } catch (Exception e) {
            e.getMessage();
        }

    }

    public void LoaderRadioCover(Context context, String url, ImageView view, int round) {
        try {
            Glide.with(context)
                    .load(url + format(url))
                    .fitCenter()
                    .transform(new GlideRoundTransform(context, round))
                    .placeholder(R.drawable.ic_default_cover_z)
                    .error(R.drawable.ic_default_cover_z)
                    .skipMemoryCache(skipMemoryCache())
                    .diskCacheStrategy(getDiskCacheStrategy())
                    .into(view);
        } catch (Exception e) {
            e.getMessage();
        }

    }

    public void LoaderDrawableRadioCover(Context context, int drawable, ImageView view) {
        try {
            Glide.with(context)
                    .load(drawable)
                    .centerCrop()
                    .placeholder(R.drawable.ic_default_cover_z)
                    .error(R.drawable.ic_default_cover_z)
                    .into(view);
        } catch (Exception e) {
            e.getMessage();
        }
    }

    public void LoaderDrawableRadioCover(Context context, int drawable, ImageView view, int round) {
        try {
            Glide.with(context)
                    .load(drawable)
                    .centerCrop()
                    .transform(new GlideRoundTransform(context, round))
                    .placeholder(R.drawable.ic_default_cover_z)
                    .error(R.drawable.ic_default_cover_z)
                    .into(view);
        } catch (Exception e) {
            e.getMessage();
        }
    }

    public void LoaderImage(Context context, String url, ImageView view) {
        try {
            Glide.with(context)
                    .load(url)
                    .centerCrop()
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .skipMemoryCache(skipMemoryCache())
                    .diskCacheStrategy(getDiskCacheStrategy())
                    .into(view);
        } catch (Exception e) {
            e.getMessage();
        }
    }

    public void LoaderImage(Context context, String url, ImageView view, int round) {
        try {
            Glide.with(context)
                    .load(url)
                    .centerCrop()
                    .transform(new GlideRoundTransform(context, round))
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .skipMemoryCache(skipMemoryCache())
                    .diskCacheStrategy(getDiskCacheStrategy())
                    .into(view);
        } catch (Exception e) {
            e.getMessage();
        }

    }

    public static void LoaderBlurImage(Context context, String url, ImageView view) {
        try {
            RequestOptions requestOptions = new RequestOptions();
            requestOptions
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .transform(new GlideBlurTransformation(context));
            Glide.with(context)
                    .load(url)
                    .apply(requestOptions)
                    .into(view);
        } catch (Exception e) {
            e.getMessage();
        }
    }

    public void LoaderMediaImage(Context context, String url, ImageView view) {
        try {
            Glide.with(context)
                    .load(url)
                    .centerCrop()
                    .skipMemoryCache(skipMemoryCache())
                    .diskCacheStrategy(getDiskCacheStrategy())
                    .into(view);
        } catch (Exception e) {
            e.getMessage();
        }
    }

    public void LoaderCircleImage(Context context, String url, ImageView view) {
        try {
            Glide.with(context)
                    .load(url)
                    .centerCrop()
                    .transform(new GlideRoundTransform(context, 100))
                    .placeholder(R.drawable.head)
                    .error(R.drawable.head)
                    .skipMemoryCache(skipMemoryCache())
                    .diskCacheStrategy(getDiskCacheStrategy())
                    .into(view);
        } catch (Exception e) {
            e.getMessage();
        }
    }

    public void LoaderDrawable(Context context, int drawable, ImageView view) {
        try {
            Glide.with(context)
                    .load(drawable)
                    .centerCrop()
                    .into(view);
        } catch (Exception e) {
            e.getMessage();
        }
    }

    public void LoaderDrawable(Context context, int drawable, ImageView view, int round) {
        try {
            Glide.with(context)
                    .load(drawable)
                    .centerCrop()
                    .transform(new GlideRoundTransform(context, round))
                    .into(view);
        } catch (Exception e) {
            e.getMessage();
        }
    }
    public static void LoderDrawable(Context context, int drawable, ImageView view) {
        Glide.with(context)
                .load(drawable)
                .into(view);
    }

    public static void LoderCustomEmotionImage(Context context, String url, ImageView view) {
        Glide.with(context)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(view);
    }



    private Serializable format(String url) {
        return "?quality=60&format=2";
    }

    private boolean skipMemoryCache() {
        return false;
    }

    private DiskCacheStrategy getDiskCacheStrategy() {
        return DiskCacheStrategy.ALL;
    }


    /**
     * ????????????????????????
     */
    public void clearImageDiskCache() {
        try {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.get(BaseApplication.getInstance()).clearDiskCache();
                    }
                }).start();
            } else {
                Glide.get(BaseApplication.getInstance()).clearDiskCache();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * ????????????????????????
     */
    public void clearImageMemoryCache() {
        try {
            if (Looper.myLooper() == Looper.getMainLooper()) { //????????????????????????
                Glide.get(BaseApplication.getInstance()).clearMemory();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * ????????????????????????
     */
    public void clearImageAllCache() {
        clearImageDiskCache();
        clearImageMemoryCache();
        String ImageExternalCatchDir = BaseApplication.getInstance().getExternalCacheDir() + ExternalCacheDiskCacheFactory.DEFAULT_DISK_CACHE_DIR;
        deleteFolderFile(ImageExternalCatchDir, true);
    }

    /**
     * ??????Glide?????????????????????
     *
     * @return CacheSize
     */
    public String getCacheSize() {
        try {
            return getFormatSize(getFolderSize(new File(BaseApplication.getInstance().getCacheDir() + "/" + InternalCacheDiskCacheFactory.DEFAULT_DISK_CACHE_DIR)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * ????????????????????????????????????????????????
     *
     * @param file file
     * @return size
     * @throws Exception
     */
    private long getFolderSize(File file) throws Exception {
        long size = 0;
        try {
            File[] fileList = file.listFiles();
            for (File aFileList : fileList) {
                if (aFileList.isDirectory()) {
                    size = size + getFolderSize(aFileList);
                } else {
                    size = size + aFileList.length();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    /**
     * ????????????????????????????????????????????????????????????
     *
     * @param filePath       filePath
     * @param deleteThisPath deleteThisPath
     */
    private void deleteFolderFile(String filePath, boolean deleteThisPath) {
        if (!TextUtils.isEmpty(filePath)) {
            try {
                File file = new File(filePath);
                if (file.isDirectory()) {
                    File files[] = file.listFiles();
                    for (File file1 : files) {
                        deleteFolderFile(file1.getAbsolutePath(), true);
                    }
                }
                if (deleteThisPath) {
                    if (!file.isDirectory()) {
                        file.delete();
                    } else {
                        if (file.listFiles().length == 0) {
                            file.delete();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * ???????????????
     *
     * @param size size
     * @return size
     */
    private static String getFormatSize(double size) {

        double kiloByte = size / 1024;
        if (kiloByte < 1) {
            return size + "MB";
        }

        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "KB";
        }

        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "MB";
        }

        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);

        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "TB";
    }


}
