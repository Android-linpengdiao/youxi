package com.base.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.media.MediaMetadataRetriever;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.view.Display;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.base.BaseApplication;
import com.base.Constants;
import com.base.MessageBus;
import com.base.R;
import com.base.manager.DialogManager;
import com.base.view.OnClickListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class CommonUtil {
    private static final String TAG = "CommonUtil";

    public static final String Index = "MaxStreamIndex";
    private static final String CountDown = "CountDown";

    public static boolean wordIsBlank(String s) {
        return (s == null || s.equals(""));
    }

    public static boolean isPicture(String url) {
        return (url.startsWith("http") || url.startsWith("/storage"));
    }

    public static String getDateToString(String time) {
        long lcc = Long.valueOf(time);
        Date d = new Date(lcc);
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdr.format(d);
    }

    public static String getStringToDate(String time) {
        String timeStamp = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d;
        try {
            d = sdf.parse(time);
            long l = d.getTime();
            timeStamp = String.valueOf(l);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeStamp;
    }

    //??????service????????????
    public static boolean isWorked(Context context, String className) {
        if (context == null || TextUtils.isEmpty(className)) {
            return false;
        }
        ActivityManager myManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ArrayList<ActivityManager.RunningServiceInfo> runningService = (ArrayList<ActivityManager.RunningServiceInfo>) myManager
                .getRunningServices(300);
        for (int i = 0; i < runningService.size(); i++) {
//            LogUtil.i(TAG, "isWorked: className " + runningService.get(i).service.getClassName().toString() + "  " + className);
            if (runningService.get(i).service.getClassName().toString()
                    .equals(className)) {
                return true;
            }
        }
        return false;
    }

    //????????????Activity ?????????????????????
    public static boolean isForeground(Context context, String className) {
        if (context == null || TextUtils.isEmpty(className)) {
            return false;
        }
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(300);
        if (list != null && list.size() > 0) {
            ComponentName cpn = list.get(0).topActivity;
            if (className.equals(cpn.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static void toSetting(Context context) {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            localIntent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            localIntent.setData(Uri.fromParts("package", context.getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            localIntent.setAction(Intent.ACTION_VIEW);
            localIntent.setClassName("com.android.settings", "com.android.setting.InstalledAppDetails");
            localIntent.putExtra("com.android.settings.ApplicationPkgName", context.getPackageName());
        }
        context.startActivity(localIntent);
    }

    public static boolean sdCardIsAvailable() {
        String status = Environment.getExternalStorageState();
        if (!status.equals(Environment.MEDIA_MOUNTED))
            return false;
        return true;
    }


    //??????????????????
    public static void observeSoftKeyboard(final Activity activity, final OnSoftKeyboardChangeListener listener) {
        final View decorView = activity.getWindow().getDecorView();
        decorView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            int previousKeyboardHeight = -1;

            @Override
            public void onGlobalLayout() {
                Rect rect = new Rect();
                decorView.getWindowVisibleDisplayFrame(rect);
                int displayHeight = rect.bottom - rect.top;
                int height = decorView.getHeight();
                int keyboardHeight = height - rect.bottom;
                if (Build.VERSION.SDK_INT >= 20) {
                    // When SDK Level >= 20 (Android L), the softInputHeight will contain the height of softButtonsBar (if has)
                    keyboardHeight = keyboardHeight - getSoftButtonsBarHeight(activity);

                }
                if (previousKeyboardHeight != keyboardHeight) {
                    boolean hide = (double) displayHeight / height > 0.8;
                    listener.onSoftKeyBoardChange(keyboardHeight, !hide, this);
                }
                previousKeyboardHeight = height;

            }
        });
    }

    private static int getSoftButtonsBarHeight(Activity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int usableHeight = metrics.heightPixels;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            activity.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        }
        int realHeight = metrics.heightPixels;
        if (realHeight > usableHeight) {
            return realHeight - usableHeight;
        } else {
            return 0;
        }
    }

    public interface OnSoftKeyboardChangeListener {
        void onSoftKeyBoardChange(int softKeybardHeight, boolean visible, ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener);
    }



    public static synchronized String chatMaxStreamIndex(Context context, String master, String type, String maxStreamIndex) {
        if (type.equals(Constants.SET_MSG_INDEX)) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(Index, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(master, maxStreamIndex);
            editor.commit();
        } else if (type.equals(Constants.GET_MSG_INDEX)) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(Index, Context.MODE_PRIVATE);
            return sharedPreferences.getString(master, "0");
        }
        return null;
    }

    /**
     * @param source
     * @param n      ?????????????????????
     * @return java.util.List<java.util.List < T>>
     * @Title: ???list????????????????????????(n)??????
     * @methodName: partList
     * @Description: ????????????????????????(n)>list.size(),?????????list;????????????:0?????????:list.size()
     * @date: 2018-07-18 21:13
     */
    public static <T> List<List<T>> partList(List<T> source, int n) {

        if (source == null) {
            return null;
        }

        if (n == 0) {
            return null;
        }
        List<List<T>> result = new ArrayList<List<T>>();
        // ????????????
        int size = source.size();
        // ??????
        int remaider = size % n;
        System.out.println("??????:" + remaider);
        // ???
        int number = size / n;
        System.out.println("???:" + number);

        for (int i = 0; i < number; i++) {
            List<T> value = source.subList(i * n, (i + 1) * n);
            result.add(value);
        }

        if (remaider > 0) {
            List<T> subList = source.subList(size - remaider, size);
            result.add(subList);
        }
        return result;
    }

    public static List<String> getTitles() {
        List<String> list = new ArrayList<>();
        list.add("???????????????|????????????????????????????????????????????????????????????");
        list.add("???????????????|??????????????????MV");
        list.add("????????????????????????????????????????????????????????????");
        list.add("6??????PMI????????????????????????????????? ??????????????????????????????");
        list.add("???????????????????????????????????????????????????");
        list.add("????????????????????????????????????");
        list.add("????????? ????????? ?????????????????????????????????????????????????????????????????????");
        list.add("???????????????|????????????????????????????????????????????????????????????");
        list.add("???????????????|??????????????????MV");
        list.add("????????????????????????????????????????????????????????????");
        list.add("6??????PMI????????????????????????????????? ??????????????????????????????");
        list.add("???????????????????????????????????????????????????");
        list.add("????????????????????????????????????");
        list.add("????????? ????????? ?????????????????????????????????????????????????????????????????????");
        return list;
    }


    public static String FormatMiss(long miss) {
        String hh = miss / 3600 > 9 ? miss / 3600 + "" : "0" + miss / 3600;
        String mm = (miss % 3600) / 60 > 9 ? (miss % 3600) / 60 + "" : "0" + (miss % 3600) / 60;
        String ss = (miss % 3600) % 60 > 9 ? (miss % 3600) % 60 + "" : "0" + (miss % 3600) % 60;
        if (hh.equals("00")) {
            return mm + ":" + ss;
        } else {
            return hh + ":" + mm + ":" + ss;
        }
    }

    /**
     * ???????????????
     */
    public static String stringForTime(double timeMs) {
        int totalSeconds = (int) timeMs;

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        if (hours > 0) {
            return String.format(Locale.getDefault(), "%d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        }
    }

    public static void toggleEllipsize(final Context context,
                                       final TextView textView,
                                       final int minLines,
                                       final String originText,
                                       final String endText,
                                       final int endColorID,
                                       final boolean isExpand,
                                       final OnClickListener onClickListener) {
        textView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (isExpand) {
//                    textView.setText(originText);
                    int paddingLeft = textView.getPaddingLeft();
                    int paddingRight = textView.getPaddingRight();
                    TextPaint paint = textView.getPaint();
                    float moreText = textView.getTextSize() * endText.length();
                    float availableTextWidth = (textView.getWidth() - paddingLeft - paddingRight) * minLines;
                    CharSequence ellipsizeStr = TextUtils.ellipsize(originText, paint, availableTextWidth, TextUtils.TruncateAt.END);
                    CharSequence temp = ellipsizeStr + endText;
                    SpannableStringBuilder ssb = new SpannableStringBuilder(temp);
                    ssb.setSpan(new ForegroundColorSpan(
                                    context.getResources().getColor(endColorID)),
                            temp.length() - endText.length(),
                            temp.length(),
                            Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

                    ssb.setSpan(new MyClickableSpan(textView, isExpand, onClickListener),
                            temp.length() - endText.length(),
                            temp.length(),
                            Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    textView.setMovementMethod(LinkMovementMethod.getInstance());//????????? ??????????????????

                    textView.setText(ssb);

                } else {
                    Log.i(TAG, "onGlobalLayout: ");
                    int paddingLeft = textView.getPaddingLeft();
                    int paddingRight = textView.getPaddingRight();
                    TextPaint paint = textView.getPaint();
                    float moreText = textView.getTextSize() * endText.length();
                    float availableTextWidth = (textView.getWidth() - paddingLeft - paddingRight) * minLines - moreText;
                    CharSequence ellipsizeStr = TextUtils.ellipsize(originText, paint, availableTextWidth, TextUtils.TruncateAt.END);
                    Log.i(TAG, "onGlobalLayout: " + (ellipsizeStr.length() < originText.length()));
                    if (ellipsizeStr.length() < originText.length()) {
                        CharSequence temp = ellipsizeStr + endText;
                        SpannableStringBuilder ssb = new SpannableStringBuilder(temp);
                        ssb.setSpan(new ForegroundColorSpan(
                                        context.getResources().getColor(endColorID)),
                                temp.length() - endText.length(),
                                temp.length(),
                                Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

                        ssb.setSpan(new MyClickableSpan(textView, isExpand, onClickListener),
                                temp.length() - endText.length(),
                                temp.length(),
                                Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                        textView.setMovementMethod(LinkMovementMethod.getInstance());//????????? ??????????????????

                        textView.setText(ssb);
                        textView.setEnabled(true);

                    } else {
                        textView.setText(originText);
                        textView.setEnabled(false);

                    }
                }
                if (Build.VERSION.SDK_INT >= 16) {
                    textView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    textView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            }
        });
    }

    public static class MyClickableSpan extends ClickableSpan {

        private TextView textView;
        private boolean isExpand;
        private OnClickListener onClickListener;

        public MyClickableSpan(TextView textView, boolean isExpand, OnClickListener onClickListener) {
            this.textView = textView;
            this.isExpand = isExpand;
            this.onClickListener = onClickListener;
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setUnderlineText(false);
        }

        @Override
        public void onClick(View view) {
            MessageBus messageBus = new MessageBus.Builder()
                    .codeType(MessageBus.msgId_toggleEllipsize)
                    .param1(isExpand)
                    .param2(textView)
                    .build();
            RxBus.getDefault().post(messageBus);
            if (onClickListener != null) {
                onClickListener.onClick(view, textView);
            }
        }
    }


    public static double musicTime(double time) {
        return new BigDecimal(time).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }


    public static byte[] splitBytes(byte[] data, int bufferSize) {
        byte[] data1 = new byte[bufferSize];
        System.arraycopy(data, data.length - bufferSize, data1, 0, bufferSize);
        return data1;
    }

    public static byte[] addBytes(byte[] data1, byte[] data2) {
        byte[] data3 = new byte[data1.length + data2.length];
        System.arraycopy(data1, 0, data3, 0, data1.length);
        System.arraycopy(data2, 0, data3, data1.length, data2.length);
        return data3;
    }

    public static int getLocalAudioDuration(String videoPath) {
        //??????
        int duration;
        try {
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(videoPath);
            duration = Integer.parseInt(mmr.extractMetadata
                    (MediaMetadataRetriever.METADATA_KEY_DURATION));
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
        return duration;
    }

    public static int getLocalVideoDuration(String videoPath) {
        //??????
        int duration;
        try {
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(videoPath);
            duration = Integer.parseInt(mmr.extractMetadata
                    (MediaMetadataRetriever.METADATA_KEY_DURATION)) / 1000;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
        return duration;
    }

    public static String encodeBase64File(String path) throws Exception {
        File file = new File(path);
        FileInputStream inputFile = new FileInputStream(file);
        byte[] buffer = new byte[(int) file.length()];
        inputFile.read(buffer);
        inputFile.close();
        return Base64.encodeToString(buffer, Base64.DEFAULT);
    }

    public static void decoderBase64File(String base64Code, String savePath) throws Exception {
        byte[] buffer = Base64.decode(base64Code, Base64.DEFAULT);
        FileOutputStream out = new FileOutputStream(savePath);
        out.write(buffer);
        out.close();
    }

    public static boolean isActivityEnable(Activity activity) {
        if (activity == null || activity.isDestroyed() || activity.isFinishing()) {
            return false;
        }
        return true;
    }

    public static boolean isChinaPhoneLegal(String phone)
            throws PatternSyntaxException {
        String regExp = "^((13[0-9])|(15[^4])|(166)|(17[0-8])|(18[0-9])|(19[8-9])|(147,145))\\d{8}$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(phone);
        return m.matches();
    }

    public static boolean isHigherVersion(String newv, String oldv) {

        String[] new_v = newv.split("\\.");
        String[] old_v = oldv.split("\\.");
        for (int i = 0; i < new_v.length; i++) {
            if (Integer.valueOf(old_v[i]) < Integer.valueOf(new_v[i])) {
                return true;
            } else if (Integer.valueOf(old_v[i]) == Integer.valueOf(new_v[i])) {
                continue;
            } else {
                break;
            }
        }

        return false;
    }

    /**
     * ??????????????????????????????URL ??????
     *
     * @param url
     * @return
     */
    public static boolean isValidUrl(String url) {
        return TextUtils.isEmpty(url) == false && url.matches(Patterns.WEB_URL.pattern());
    }

    /**
     * //?????????????????????
     *
     * @param text ??????????????????????????????text??????
     */
    public static String getCompleteUrl(String text) {
        Pattern p = Pattern.compile("((http|ftp|https)://)(([a-zA-Z0-9\\._-]+\\.[a-zA-Z]{2,6})|([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}))(:[0-9]{1,4})*(/[a-zA-Z0-9\\&%_\\./-~-]*)?", Pattern.CASE_INSENSITIVE);
//        Pattern p = Pattern.compile("(((https?|ftp|file)://|)[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|])", Pattern.CASE_INSENSITIVE);
        Matcher matcher = p.matcher(text);
        matcher.find();
        return matcher.group();
    }


    public static void clipboard(Context context, String content) {
        //???????????????????????????
        ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        // ?????????????????????ClipData
        ClipData mClipData = ClipData.newPlainText("Label", content);
        // ???ClipData?????????????????????????????????
        cm.setPrimaryClip(mClipData);
//        ToastUtils.showShort(context, context.getString(R.string.Copy));
    }

    /**
     * ???????????????-??????
     */
    public static String getClipboardContent(Context context) {
        // ?????????????????????
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        // ????????????
        ClipData clipData = clipboard.getPrimaryClip();
        if (clipData == null || clipData.getItemCount() <= 0) {
            return "";
        }
        ClipData.Item item = clipData.getItemAt(0);
        if (item == null || item.getText() == null) {
            return "";
        }
        return item.getText().toString();
    }

    public static boolean isPassword(Context context, String phone) {
        if (CommonUtil.isBlank(phone)) {
            ToastUtils.showShort(context, context.getString(R.string.Password));
            return false;
        } else if (phone.length() < 6) {
            ToastUtils.showShort(context, "?????????????????????");
            return false;
        }
        return true;
    }

    public static boolean isPhone(Context context, String phone) {
        if (CommonUtil.isBlank(phone)) {
            ToastUtils.showShort(context, context.getString(R.string.EnterPhoneNumber));
            return false;
        } else if (phone.length() < 11) {
            ToastUtils.showShort(context, "????????????????????????");
            return false;
        }
        return true;
    }

    public static boolean openLocation(Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
        boolean ok = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!ok) {//??????????????????
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            context.startActivity(intent);
        }
        return ok;
    }

    /**
     * ??????????????????????????????
     *
     * @param
     * @return true ????????????
     */
    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }
            return locationMode != Settings.Secure.LOCATION_MODE_OFF;
        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }

    /**
     * ?????????????????????????????????
     **/
    public static void toOpenGPS(final Activity activity, final int requestCode) {
        new AlertDialog.Builder(activity)
                .setTitle("??????")
                .setMessage("?????????????????????????????????????????????????????????????????????????????????????????????")
                .setNegativeButton("??????", null)
                .setPositiveButton("?????????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        activity.startActivityForResult(intent, requestCode);
                        dialogInterface.dismiss();
                    }
                })
                .show();
    }


    public static void hideSoftInput(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        IBinder b = null;
        if (view != null) b = view.getWindowToken();
        imm.hideSoftInputFromWindow(b, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public static void hideSoftInput(Activity activity, EditText mEditText) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        mEditText.clearFocus();
        inputMethodManager.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
    }

    public static void showSoftInput(Activity activity, EditText mEditText) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        mEditText.requestFocus();
        inputMethodManager.showSoftInput(mEditText, 0);
    }


    public static boolean isBlank(String s) {
        return (s == null || s.equals("") || s.equals("null"));
    }

    public static boolean isBlank(Object o) {
        return (o == null);
    }


    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public static int getScreenWidth(Context context) {
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        return display.getWidth();
    }

    public static int getScreenHeight(Context context) {
        WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        return display.getHeight();
    }

    public static int getStatusBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourcesId = resources.getIdentifier("status_bar_height", "dimen", "android");
        int height = resources.getDimensionPixelSize(resourcesId);
        return height;
    }

}
