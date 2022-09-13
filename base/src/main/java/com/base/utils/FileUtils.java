package com.base.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.core.content.FileProvider;

import android.util.Log;

import com.base.BaseApplication;
import com.base.R;


import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class FileUtils {

    public static String getPath() {
        return BaseApplication.getInstance().getExternalFilesDir(null) + File.separator;
    }

    public static String getTempPath() {
        new File(getPath() + "temp").mkdirs();
        return getPath() + "temp" + File.separator;
    }

    public static String getMediaPath() {
        new File(getPath() + "media").mkdirs();
        return getPath() + "media" + File.separator;
    }

    public static String getAppPath() {
        return Environment.getExternalStorageDirectory() + File.separator +
                BaseApplication.getInstance().getResources().getString(R.string.app_name) + File.separator;
    }

    public static File createFile(String fileName) {
        if (!CommonUtil.isBlank(fileName)) {
            try {
                File file = new File(getPath() + fileName);
                file.getParentFile().mkdirs();
                if (file.exists()) {
                    file.delete();
                }
                file.createNewFile();
                return file;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    // 创建一个附件文件
    public static File createFile(String path, String fileName) {
        File file = new File(path + fileName);
        File directory = file.getParentFile();
        if (!directory.exists() && !directory.mkdirs()) {
        }
        try {
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            return file;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static File createTempFile(String fileName) {
        if (!CommonUtil.isBlank(fileName)) {
            try {
                File file = new File(getTempPath() + fileName);
                file.getParentFile().mkdirs();
                if (file.exists()) {
                    file.delete();
                }
                file.createNewFile();
                return file;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static File getTempFileName(String picPath) {
        String fileName = picPath.substring(picPath.lastIndexOf("/") + 1);
        File file = FileUtils.createTempFile(fileName);
        return file;
    }

    //判断文件是否存在
    public static boolean fileIsExists(String strFile) {
        try {
            File f = new File(strFile);
            if (!f.exists()) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static void clearFile() {
        if (clearIsFileExists(getPath())) {
            File file = new File(getPath());
            delete(file);
        } else {
        }
    }

    public static boolean clearIsFileExists(String path) {
        if (CommonUtil.isBlank(path)) {
            return false;
        }
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        try {
            if (file.exists()) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void delete(File file) {
        if (file.isFile()) {
            file.delete();
            return;
        }

        if (file.isDirectory()) {
            File[] childFiles = file.listFiles();
            if (childFiles == null || childFiles.length == 0) {
                file.delete();
                return;
            }

            for (int i = 0; i < childFiles.length; i++) {
                delete(childFiles[i]);
            }
            file.delete();
        }
    }

    //分享多张图片
    public static void shareMultiImg(Activity activity, String dlgTitle, String filePath) {
        if (filePath == null) {
            return;
        }
        //分享单张图片
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        Uri fileUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            fileUri = FileProvider.getUriForFile(activity, activity.getPackageName() + ".provider", new File(filePath));
        } else {
            fileUri = Uri.fromFile(new File(filePath));
        }
        shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
        shareIntent.setType("image/*");
        activity.startActivity(Intent.createChooser(shareIntent, dlgTitle));

//        //分享多张图片
//        ArrayList<Uri> imageUris = new ArrayList<>();
//        imageUris.add(uri);
//        imageUris.add(uri);
//        Intent shareIntent = new Intent();
//        shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
//        shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
//        shareIntent.setType("image/*");
//        startActivity(Intent.createChooser(shareIntent, dlgTitle));
    }

    public static void openFile(Context context, File file) {
        try {
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction("android.intent.action.VIEW");
            String type = getMIMEType(file);
            if (Build.VERSION.SDK_INT >= 24) {
                Uri fileUri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);
                intent.setDataAndType(fileUri, type);
                grantUriPermission(context, fileUri, intent);
            } else {
                intent.setDataAndType(Uri.fromFile(file), type);
            }

            context.startActivity(intent);
        } catch (Exception var6) {
            var6.printStackTrace();
        }

    }

    private static void grantUriPermission(Context context, Uri fileUri, Intent intent) {
        List<ResolveInfo> resInfoList = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        Iterator var4 = resInfoList.iterator();

        while (var4.hasNext()) {
            ResolveInfo resolveInfo = (ResolveInfo) var4.next();
            String packageName = resolveInfo.activityInfo.packageName;
            context.grantUriPermission(packageName, fileUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }

    }

    private static final String[][] MIME_MapTable = new String[][]{{".3gp", "video/3gpp"}, {".apk", "application/vnd.android.package-archive"}, {".asf", "video/x-ms-asf"}, {".avi", "video/x-msvideo"}, {".bin", "application/octet-stream"}, {".bmp", "image/bmp"}, {".c", "text/plain"}, {".class", "application/octet-stream"}, {".conf", "text/plain"}, {".cpp", "text/plain"}, {".doc", "application/msword"}, {".docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"}, {".xls", "application/vnd.ms-excel"}, {".xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"}, {".exe", "application/octet-stream"}, {".gif", "image/gif"}, {".gtar", "application/x-gtar"}, {".gz", "application/x-gzip"}, {".h", "text/plain"}, {".htm", "text/html"}, {".html", "text/html"}, {".jar", "application/java-archive"}, {".java", "text/plain"}, {".jpeg", "image/jpeg"}, {".jpg", "image/jpeg"}, {".js", "application/x-javascript"}, {".log", "text/plain"}, {".m3u", "audio/x-mpegurl"}, {".m4a", "audio/mp4a-latm"}, {".m4b", "audio/mp4a-latm"}, {".m4p", "audio/mp4a-latm"}, {".m4u", "video/vnd.mpegurl"}, {".m4v", "video/x-m4v"}, {".mov", "video/quicktime"}, {".mp2", "audio/x-mpeg"}, {".mp3", "audio/x-mpeg"}, {".mp4", "video/mp4"}, {".mpc", "application/vnd.mpohun.certificate"}, {".mpe", "video/mpeg"}, {".mpeg", "video/mpeg"}, {".mpg", "video/mpeg"}, {".mpg4", "video/mp4"}, {".mpga", "audio/mpeg"}, {".msg", "application/vnd.ms-outlook"}, {".ogg", "audio/ogg"}, {".pdf", "application/pdf"}, {".png", "image/png"}, {".pps", "application/vnd.ms-powerpoint"}, {".ppt", "application/vnd.ms-powerpoint"}, {".pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation"}, {".prop", "text/plain"}, {".rc", "text/plain"}, {".rmvb", "audio/x-pn-realaudio"}, {".rtf", "application/rtf"}, {".sh", "text/plain"}, {".tar", "application/x-tar"}, {".tgz", "application/x-compressed"}, {".txt", "text/plain"}, {".wav", "audio/x-wav"}, {".wma", "audio/x-ms-wma"}, {".wmv", "audio/x-ms-wmv"}, {".wps", "application/vnd.ms-works"}, {".xml", "text/plain"}, {".z", "application/x-compress"}, {".zip", "application/x-zip-compressed"}, {"", "*/*"}};

    private static String getMIMEType(File file) {
        String type = "*/*";
        String fName = file.getName();
        int dotIndex = fName.lastIndexOf(".");
        if (dotIndex < 0) {
            return type;
        } else {
            String end = fName.substring(dotIndex, fName.length()).toLowerCase();
            if (end == "") {
                return type;
            } else {
                for (int i = 0; i < MIME_MapTable.length; ++i) {
                    if (end.equals(MIME_MapTable[i][0])) {
                        type = MIME_MapTable[i][1];
                    }
                }

                return type;
            }
        }
    }

}
