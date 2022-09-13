package com.base;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.widget.Toast;

import com.base.utils.BitmapUtils;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.NotFoundException;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Hashtable;

/**
 * Created by Administrator on 2018/3/23 0023.
 */

public class ZxingManager {

    private static final String TAG = "ZxingManager";

    private static ZxingManager mInstance;

    private Context context;

    public static ZxingManager getInstance(Context context) {
        if (null == mInstance) {
            synchronized (ZxingManager.class) {
                if (null == mInstance) {
                    mInstance = new ZxingManager(context);
                }
            }
        }
        return mInstance;
    }

    private ZxingManager(Context context) {
        this.context = context;
    }

    public interface scanCodeCallBack {
        void Success(String message);

        void Error(String message);
    }

    /**
     * 识别图中二维码
     *
     * @param photo_path
     * @return
     */
    public String ScanCode(final String photo_path) {
//        String result = recogQRcode(photo_path);
        return recogQRcode(photo_path);
    }

    /**
     * 创建二维码
     *
     * @param key
     * @return
     */
    public Bitmap createQRCode(String key) {
        Bitmap bitmap = create2Code(key);
        return bitmap;
    }

    /**
     * 生成条形码
     *
     * @param key
     * @return
     */
    public Bitmap createBarCode(String key) {
        Bitmap qrCode = null;
        try {
            qrCode = EncodingHandler.createBarCode(key, 600, 300);
        } catch (Exception e) {
            Toast.makeText(context, "输入的内容条形码不支持！", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        return qrCode;
    }

    /**
     * 生成二维码
     *
     * @param key
     */
    public Bitmap create2Code(String key) {
        Bitmap qrCode = null;
        try {
            qrCode = EncodingHandler.create2Code(key, 600);
        } catch (WriterException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return qrCode;
    }

    /**
     * 初始化头像图片
     */
    public Bitmap getHeadBitmap(String url, int size) {
        try {
            // 这里采用从asset中加载图片abc.jpg
//            Bitmap portrait = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher);
            Bitmap portrait = getBitMBitmap(url);
            // 对原有图片压缩显示大小
            Matrix mMatrix = new Matrix();
            float width = portrait.getWidth();
            float height = portrait.getHeight();
            mMatrix.setScale(size / width, size / height);
            return Bitmap.createBitmap(portrait, 0, 0, (int) width,
                    (int) height, mMatrix, true);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Bitmap getHeadBitmap(Bitmap portrait, int size) {
        try {
            portrait = GetRoundedCornerBitmap(portrait);
            portrait = GetRoundeBitmapWithWhite(portrait);
            Matrix mMatrix = new Matrix();
            float width = portrait.getWidth();
            float height = portrait.getHeight();
            mMatrix.setScale(size / width, size / height);
            return Bitmap.createBitmap(portrait, 0, 0, (int) width,
                    (int) height, mMatrix, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 在二维码上绘制头像
     */
    public void createQRCodeBitmapWithPortrait(Bitmap qrcode, Bitmap portrait) {
        // 头像图片的大小
        int portrait_W = portrait.getWidth();
        int portrait_H = portrait.getHeight();

        // 设置头像要显示的位置，即居中显示
        int left = (qrcode.getWidth() - portrait_W) / 2;
        int top = (qrcode.getHeight() - portrait_H) / 2;
        int right = left + portrait_W;
        int bottom = top + portrait_H;
        Rect rect1 = new Rect(left, top, right, bottom);

        // 取得qr二维码图片上的画笔，即要在二维码图片上绘制我们的头像
        Canvas canvas = new Canvas(qrcode);

        // 设置我们要绘制的范围大小，也就是头像的大小范围
        Rect rect2 = new Rect(0, 0, portrait_W, portrait_H);
        // 开始绘制
        canvas.drawBitmap(portrait, rect2, rect1, null);
    }

    /**
     * 通过图片url生成Bitmap对象
     *
     * @param urlpath
     * @return Bitmap
     * 根据图片url获取图片对象
     */
    public static Bitmap getBitMBitmap(String urlpath) {
        Bitmap map = null;
        try {
            URL url = new URL(urlpath);
            URLConnection conn = url.openConnection();
            conn.connect();
            InputStream in;
            in = conn.getInputStream();
            map = BitmapFactory.decodeStream(in);
//            map = GetRoundedCornerBitmap(map);
//            map = GetRoundeBitmapWithWhite(map);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 生成圆角图片
     *
     * @param bitmap
     * @return
     */
    public static Bitmap GetRoundedCornerBitmap(Bitmap bitmap) {
        try {
            Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                    bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(output);
            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, bitmap.getWidth(),
                    bitmap.getHeight());
            final RectF rectF = new RectF(new Rect(0, 0, bitmap.getWidth(),
                    bitmap.getHeight()));
            final float roundPx = 15;
            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(Color.BLACK);
            canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

            final Rect src = new Rect(0, 0, bitmap.getWidth(),
                    bitmap.getHeight());

            canvas.drawBitmap(bitmap, src, rect, paint);
            return output;
        } catch (Exception e) {
            return bitmap;
        }
    }

    /**
     * 获取白色边框
     *
     * @param bitmap
     * @return
     */
    public static Bitmap GetRoundeBitmapWithWhite(Bitmap bitmap) {
        int size = bitmap.getWidth() < bitmap.getHeight() ? bitmap.getWidth()
                : bitmap.getHeight();
        int num = 14;
        int sizebig = size + num;
        Bitmap output = Bitmap.createBitmap(sizebig, sizebig, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = Color.parseColor("#FFFFFF");
        final Paint paint = new Paint();
        final float roundPx = sizebig / 2;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawBitmap(bitmap, num / 2, num / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_ATOP));

        RadialGradient gradient = new RadialGradient(roundPx, roundPx, roundPx,
                new int[]{Color.WHITE, Color.WHITE,
                        Color.parseColor("#AAAAAAAA")}, new float[]{0.f,
                0.97f, 1.0f}, Shader.TileMode.CLAMP);
        paint.setShader(gradient);
        canvas.drawCircle(roundPx, roundPx, roundPx, paint);
        return output;
    }

    public String getStringFromQRCode(String photo_path) {
        String httpString = null;
        Bitmap bitmap = BitmapUtils.decodeBitmapFromPath(photo_path, 200, 200);
        byte[] data = ImageUtils.getYUV420sp(bitmap.getWidth(), bitmap.getHeight(), bitmap);
        // 处理
        try {
            Hashtable<DecodeHintType, Object> hints = new Hashtable<DecodeHintType, Object>();
            hints.put(DecodeHintType.CHARACTER_SET, "utf-8");
            hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
            hints.put(DecodeHintType.POSSIBLE_FORMATS, BarcodeFormat.QR_CODE);
            PlanarYUVLuminanceSource source = new PlanarYUVLuminanceSource(data,
                    bitmap.getWidth(),
                    bitmap.getHeight(),
                    0, 0,
                    bitmap.getWidth(),
                    bitmap.getHeight(),
                    false);
            BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
            QRCodeReader reader2 = new QRCodeReader();
            Result result = reader2.decode(bitmap1, hints);

            httpString = result.getText();
        } catch (Exception e) {
            e.printStackTrace();
        }

        bitmap.recycle();
        bitmap = null;
        return httpString;
    }

    public static class ImageUtils {
        /**
         * YUV420sp
         *
         * @param inputWidth
         * @param inputHeight
         * @param scaled
         * @return
         */
        public static byte[] getYUV420sp(int inputWidth, int inputHeight, Bitmap scaled) {
            int[] argb = new int[inputWidth * inputHeight];

            scaled.getPixels(argb, 0, inputWidth, 0, 0, inputWidth, inputHeight);

            byte[] yuv = new byte[inputWidth * inputHeight * 3 / 2];

            encodeYUV420SP(yuv, argb, inputWidth, inputHeight);

            scaled.recycle();

            return yuv;
        }

        /**
         * RGB转YUV420sp
         *
         * @param yuv420sp inputWidth * inputHeight * 3 / 2
         * @param argb     inputWidth * inputHeight
         * @param width
         * @param height
         */
        private static void encodeYUV420SP(byte[] yuv420sp, int[] argb, int width, int height) {
            // 帧图片的像素大小
            final int frameSize = width * height;
            // ---YUV数据---
            int Y, U, V;
            // Y的index从0开始
            int yIndex = 0;
            // UV的index从frameSize开始
            int uvIndex = frameSize;

            // ---颜色数据---
            // int a, R, G, B;
            int R, G, B;
            //
            int argbIndex = 0;
            //

            // ---循环所有像素点，RGB转YUV---
            for (int j = 0; j < height; j++) {
                for (int i = 0; i < width; i++) {

                    // a is not used obviously
                    // a = (argb[argbIndex] & 0xff000000) >> 24;
                    R = (argb[argbIndex] & 0xff0000) >> 16;
                    G = (argb[argbIndex] & 0xff00) >> 8;
                    B = (argb[argbIndex] & 0xff);
                    //
                    argbIndex++;

                    // well known RGB to YUV algorithm
                    Y = ((66 * R + 129 * G + 25 * B + 128) >> 8) + 16;
                    U = ((-38 * R - 74 * G + 112 * B + 128) >> 8) + 128;
                    V = ((112 * R - 94 * G - 18 * B + 128) >> 8) + 128;

                    //
                    Y = Math.max(0, Math.min(Y, 255));
                    U = Math.max(0, Math.min(U, 255));
                    V = Math.max(0, Math.min(V, 255));

                    // NV21 has a plane of Y and interleaved planes of VU each
                    // sampled by a factor of 2
                    // meaning for every 4 Y pixels there are 1 V and 1 U. Note the
                    // sampling is every other
                    // pixel AND every other scanline.
                    // ---Y---
                    yuv420sp[yIndex++] = (byte) Y;
                    // ---UV---
                    if ((j % 2 == 0) && (i % 2 == 0)) {
                        //
                        yuv420sp[uvIndex++] = (byte) V;
                        //
                        yuv420sp[uvIndex++] = (byte) U;
                    }
                }
            }
        }
    }

    //识别二维码的函数
    public String recogQRcode(String path) {
//        Bitmap QRbmp = BitmapUtil.decodeBitmapFromPath(path, 600, 600);   //将图片bitmap化
        Bitmap QRbmp = BitmapFactory.decodeFile(path);   //将图片bitmap化
        int width = QRbmp.getWidth();
        int height = QRbmp.getHeight();
        int[] data = new int[width * height];
        QRbmp.getPixels(data, 0, width, 0, 0, width, height);    //得到像素
        RGBLuminanceSource source = new RGBLuminanceSource(QRbmp);   //RGBLuminanceSource对象
        BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
        QRCodeReader reader = new QRCodeReader();
        Result re = null;
        try {
            //得到结果
            re = reader.decode(bitmap1);
        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (ChecksumException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }
//        //利用正则表达式判断内容是否是URL，是的话则打开网页
//        String regex = "(((https|http)?://)?([a-z0-9]+[.])|(www.))"
//                + "\\w+[.|\\/]([a-z0-9]{0,})?[[.]([a-z0-9]{0,})]+((/[\\S&&[^,;\u4E00-\u9FA5]]+)+)?([.][a-z0-9]{0,}+|/?)";//设置正则表达式
//
//        Pattern pat = Pattern.compile(regex.trim());//比对
//        Matcher mat = pat.matcher(re.getText().trim());
//        if (mat.matches()) {
////            Uri uri = Uri.parse(re.getText());
////            Intent intent = new Intent(Intent.ACTION_VIEW, uri);//打开浏览器
////            startActivity(intent);
//        }
        if (re != null) {
            return re.getText();
        } else {
            return null;
        }
    }

    //识别图片所需要的RGBLuminanceSource类
    public class RGBLuminanceSource extends LuminanceSource {

        private byte bitmapPixels[];

        protected RGBLuminanceSource(Bitmap bitmap) {
            super(bitmap.getWidth(), bitmap.getHeight());

            // 首先，要取得该图片的像素数组内容
            int[] data = new int[bitmap.getWidth() * bitmap.getHeight()];
            this.bitmapPixels = new byte[bitmap.getWidth() * bitmap.getHeight()];
            bitmap.getPixels(data, 0, getWidth(), 0, 0, getWidth(), getHeight());

            // 将int数组转换为byte数组，也就是取像素值中蓝色值部分作为辨析内容
            for (int i = 0; i < data.length; i++) {
                this.bitmapPixels[i] = (byte) data[i];
            }
        }

        @Override
        public byte[] getMatrix() {
            // 返回我们生成好的像素数据
            return bitmapPixels;
        }

        @Override
        public byte[] getRow(int y, byte[] row) {
            // 这里要得到指定行的像素数据
            System.arraycopy(bitmapPixels, y * getWidth(), row, 0, getWidth());
            return row;
        }
    }


}
