package com.base.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;

/**
 * Created by Administrator on 2017/9/1.
 */

public class PermissionUtils {

    public static String ALL = "ALL";
    public static String AUDIO = "AUDIO";
    public static String CAMERA = "CAMERA";
    public static String CAMERA_VIDEO = "CAMERA_VIDEO";
    public static String STORAGE = "STORAGE";
    public static String LOCATION = "LOCATION";
    public static String PHONE = "PHONE";
    public static String INIT = "INIT";
    public static String CONTACTS = "CONTACTS";
    public static String CALL = "CALL";

    public static String[] audio = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO
    };

    public static String[] camera = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    public static String[] camera_video = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA
    };

    public static String[] storage = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    public static String[] contacts = new String[]{
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS
    };

    public static String[] location = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    public static String[] phone = new String[]{
            Manifest.permission.READ_PHONE_STATE
    };

    public static String[] call = new String[]{
            Manifest.permission.CALL_PHONE
    };

    public static String[] init = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    public static String[] all = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA
    };

    /**
     * ??????????????????????????????
     *
     * @param context
     * @return
     */
    public static boolean checkPermissionAllGranted(Context context, String type) {
//        for (String permission : permissions) {
//            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
//                // ????????????????????????????????????, ??????????????? false
//                return false;
//            }
//        }

        if (type.equals(ALL)) {
            for (String permission : all) {
                if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        if (type.equals(INIT)) {
            for (String permission : init) {
                if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        if (type.equals(AUDIO)) {
            for (String permission : audio) {
                if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        if (type.equals(CAMERA)) {
            for (String permission : camera) {
                if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        if (type.equals(CAMERA_VIDEO)) {
            for (String permission : camera_video) {
                if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        if (type.equals(STORAGE)) {
            for (String permission : storage) {
                if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        if (type.equals(CONTACTS)) {
            for (String permission : contacts) {
                if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        if (type.equals(LOCATION)) {
            for (String permission : location) {
                if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }

        if (type.equals(PHONE)) {
            for (String permission : phone) {
                if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }

        if (type.equals(CALL)) {
            for (String permission : call) {
                if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * ????????????
     *
     * @param activity
     * @param code
     */
    public static void requestPermissions(Activity activity, String type, int code) {
//        ActivityCompat.requestPermissions(activity, all, code);

        if (type.equals(ALL)) {
            ActivityCompat.requestPermissions(activity, all, code);
        }
        if (type.equals(INIT)) {
            ActivityCompat.requestPermissions(activity, init, code);
        }
        if (type.equals(AUDIO)) {
            ActivityCompat.requestPermissions(activity, audio, code);
        }
        if (type.equals(CAMERA)) {
            ActivityCompat.requestPermissions(activity, camera, code);
        }
        if (type.equals(CAMERA_VIDEO)) {
            ActivityCompat.requestPermissions(activity, camera_video, code);
        }
        if (type.equals(STORAGE)) {
            ActivityCompat.requestPermissions(activity, storage, code);
        }
        if (type.equals(CONTACTS)) {
            ActivityCompat.requestPermissions(activity, contacts, code);
        }
        if (type.equals(LOCATION)) {
            ActivityCompat.requestPermissions(activity, location, code);
        }

        if (type.equals(PHONE)) {
            ActivityCompat.requestPermissions(activity, phone, code);
        }

        if (type.equals(CALL)) {
            ActivityCompat.requestPermissions(activity, call, code);
        }
    }

    public static boolean checkPermissionGranted(Activity activity, String type) {
        if (type.equals(AUDIO)) {
            for (String permission : audio) {
                if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                    // ????????????????????????????????????, ??????????????? false
                    requestPermission(activity, permission);
                    return false;
                }
            }
        }
        if (type.equals(CAMERA)) {
            for (String permission : camera) {
                if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                    // ????????????????????????????????????, ??????????????? false
                    requestPermission(activity, permission);
                    return false;
                }
            }
        }
        if (type.equals(CAMERA_VIDEO)) {
            for (String permission : camera_video) {
                if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                    // ????????????????????????????????????, ??????????????? false
                    requestPermission(activity, permission);
                    return false;
                }
            }
        }
        if (type.equals(STORAGE)) {
            for (String permission : storage) {
                if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                    // ????????????????????????????????????, ??????????????? false
                    requestPermission(activity, permission);
                    return false;
                }
            }
        }
        if (type.equals(CONTACTS)) {
            for (String permission : contacts) {
                if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                    // ????????????????????????????????????, ??????????????? false
                    requestPermission(activity, permission);
                    return false;
                }
            }
        }

        if (type.equals(LOCATION)) {
            for (String permission : location) {
                if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                    // ????????????????????????????????????, ??????????????? false
                    requestPermission(activity, permission);
                    return false;
                }
            }
        }

        if (type.equals(PHONE)) {
            for (String permission : phone) {
                if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                    // ????????????????????????????????????, ??????????????? false
                    requestPermission(activity, permission);
                    return false;
                }
            }
        }

        if (type.equals(CALL)) {
            for (String permission : call) {
                if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                    // ????????????????????????????????????, ??????????????? false
                    requestPermission(activity, permission);
                    return false;
                }
            }
        }

        return true;
    }

    public static void requestPermission(Activity activity, String permission) {
        ActivityCompat.requestPermissions(activity, new String[]{permission}, 0);
    }

    public interface OnClickListener {

        void onConfirmClicked();

        void onCancelClicked();

    }

    private static OnClickListener onClickListener = null;

    public void setOnClickListener(OnClickListener l) {
        this.onClickListener = l;
    }

    /**
     * ?????? APP ???????????????
     */
    public static void openAppDetails(final Context context, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("\nApp????????????" + message + "????????????????????? ??????????????????>????????????????????????????????????");
        builder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setData(Uri.parse("package:" + context.getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                context.startActivity(intent);
            }
        });
        builder.setNegativeButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }

    /**
     * ?????? APP ???????????????
     */
    public static void openAppDetailsInit(final Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("????????????");
        builder.setMessage("?????????????????????????????????????????????????????????????????????????????????APP \n\n???????????? ??????????????????>????????????????????????????????????");
        builder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setData(Uri.parse("package:" + activity.getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                activity.startActivity(intent);
                onClickListener.onConfirmClicked();
            }
        });
        builder.setNegativeButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onClickListener.onCancelClicked();
            }
        });
        builder.show();
    }

}
