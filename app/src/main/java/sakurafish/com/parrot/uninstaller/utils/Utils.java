package sakurafish.com.parrot.uninstaller.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.Calendar;

import sakurafish.com.parrot.uninstaller.BuildConfig;
import sakurafish.com.parrot.uninstaller.R;
import sakurafish.com.parrot.uninstaller.UninstallerApplication;

public class Utils {

    @Deprecated
    private Utils() {
    }

    /**
     * デバッグ用ログ出力：出所が分かるようにメソッドの出力位置を表示する
     *
     * @param message 出力するメッセージ文字列
     */
    public static void logDebug(String message) {
        if (BuildConfig.DEBUG) {

            String prefLabel = new Throwable().getStackTrace()[1].toString();
            Log.d("logDebug", prefLabel + ": " + message);
        }
    }

    /**
     * ログ出力：出所が分かるようにメソッドの出力位置を表示する
     *
     * @param message 出力するメッセージ文字列
     */
    public static void logError(String message) {
        String prefLabel = new Throwable().getStackTrace()[1].toString();
        Log.e("logError", prefLabel + ": " + message);
    }

    /**
     * Toastを表示する
     *
     * @param text
     */
    public static void showToast(String text) {
        Toast.makeText(UninstallerApplication.getContext(), text,
                Toast.LENGTH_SHORT).show();
    }

    /**
     * Toastを表示する
     *
     * @param text
     */
    public static void showToast(Activity activity, String text) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.views_toast, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.icon);
        int rand = (int) (Math.random() * 10);
        switch (rand) {
            case 0:
                imageView.setImageResource(R.drawable.inco1);
                break;
            case 1:
                imageView.setImageResource(R.drawable.inco2);
                break;
            case 2:
                imageView.setImageResource(R.drawable.inco3);
                break;
            case 3:
                imageView.setImageResource(R.drawable.inco4);
                break;
            case 4:
                imageView.setImageResource(R.drawable.inco5);
                break;
            case 5:
                imageView.setImageResource(R.drawable.inco6);
                break;
            case 6:
                imageView.setImageResource(R.drawable.inco7);
                break;
            case 7:
                imageView.setImageResource(R.drawable.inco8);
                break;
            case 8:
                imageView.setImageResource(R.drawable.inco9);
                break;
            case 9:
                imageView.setImageResource(R.drawable.inco10);
                break;
            default:
                imageView.setImageResource(R.drawable.inco9);
                break;
        }

        TextView textView = (TextView) view.findViewById(R.id.message);
        textView.setText(text);
        Toast toast = new Toast(activity);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setView(view);
        toast.show();
    }

    /**
     * ネットワーク接続チェック
     *
     * @return
     */
    public static boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) UninstallerApplication.getContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni != null) {
            return cm.getActiveNetworkInfo().isConnected();
        }
        return false;
    }

    /**
     * パッケージ存在チェック
     *
     * @return
     */
    public static boolean isPackageExist(String packageName) {
        PackageManager packageManager = UninstallerApplication.getContext().getPackageManager();
        try {
            packageManager.getApplicationInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
        return true;
    }

    /**
     * version nameを取得する
     *
     * @return
     */
    public static String getVersionName() {
        Context context = UninstallerApplication.getContext();
        PackageManager manager = context.getPackageManager();
        PackageInfo info;
        String version;
        try {
            info = manager.getPackageInfo(context.getPackageName(), 0);
            version = info.versionName;
            // for debug
            if (version.contains("-DEBUG")) {
                version = version.replace("-DEBUG", "");
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return version;
    }

    public static int getAge(int year, int month, int day) {
        Calendar birthdayCal = Calendar.getInstance();
        birthdayCal.set(year, month, day);
        Calendar todayCal = Calendar.getInstance();
        int yearDiff = todayCal.get(Calendar.YEAR)
                - birthdayCal.get(Calendar.YEAR);
        if (todayCal.get(Calendar.MONTH) < birthdayCal.get(Calendar.MONTH)) {
            yearDiff--;
        } else if (todayCal.get(Calendar.MONTH) == birthdayCal
                .get(Calendar.MONTH)) {
            if (todayCal.get(Calendar.DAY_OF_MONTH) < birthdayCal
                    .get(Calendar.DAY_OF_MONTH)) {
                yearDiff--;
            }
        }
        return (int) Math.floor(yearDiff / 10);
    }

    public static boolean checkStorage() {
        boolean mExternalStorageAvailable = false;
        boolean mExternalStorageWriteable = false;
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // We can read and write the media
            mExternalStorageAvailable = mExternalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            // We can only read the media
            mExternalStorageAvailable = true;
            mExternalStorageWriteable = false;
        } else {
            // Something else is wrong. It may be one of many other states, but
            // all we need
            // to know is we can neither read nor write
            mExternalStorageAvailable = mExternalStorageWriteable = false;
        }
        logDebug("mExternalStorageAvailable:" + mExternalStorageAvailable);
        return mExternalStorageWriteable;
    }

    public static Bitmap createStreamBitmap(InputStream stream, Options opts, int newWidth,
                                            int newHeight) {
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(stream, null, opts);

        int imageHeight = opts.outHeight;
        int imageWidth = opts.outWidth;
        if (imageHeight > newHeight || imageWidth > newWidth) {
            if (imageWidth > imageHeight) {
                opts.inSampleSize = (int) Math.ceil((float) imageHeight
                        / (float) newHeight);
            } else {
                opts.inSampleSize = (int) Math.ceil((float) imageWidth
                        / (float) newWidth);
            }
        }
        opts.inJustDecodeBounds = false;
        Bitmap ret = BitmapFactory.decodeStream(stream, null, opts);
        if (ret == null) {
            return null;
        }
        return ret;
    }

    public static Bitmap resizeBitmap(Bitmap bitmap, int newWidth, int newHeight) {
        if (bitmap == null) {
            return null;
        }

        int oldWidth = bitmap.getWidth();
        int oldHeight = bitmap.getHeight();

        if (oldWidth < newWidth && oldHeight < newHeight) {
            // 縦も横も指定サイズより小さい場合は何もしない
            return bitmap;
        }

        float scaleWidth = ((float) newWidth) / oldWidth;
        float scaleHeight = ((float) newHeight) / oldHeight;
        float scaleFactor = Math.min(scaleWidth, scaleHeight);

        Matrix scale = new Matrix();
        scale.postScale(scaleFactor, scaleFactor);

        Bitmap resizeBitmap = Bitmap.createBitmap(bitmap, 0, 0, oldWidth, oldHeight, scale, false);
        bitmap.recycle();

        return resizeBitmap;
    }

    @SuppressWarnings("deprecation")
    public static void overrideGetSize(Display display, Point outSize) {
        try {
            @SuppressWarnings("rawtypes")
            Class pointClass = Class.forName("android.graphics.Point");
            Method newGetSize = Display.class.getMethod("getSize", new Class[]{
                    pointClass
            });

            newGetSize.invoke(display, outSize);
        } catch (Exception ex) {
            outSize.x = display.getWidth();
            outSize.y = display.getHeight();
        }
    }

    public static long copyLarge(InputStream input, OutputStream output)
            throws IOException {
        byte[] buffer = new byte[4096];
        long count = 0L;
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

    public static void saveImageFileInApp(Context con, InputStream is) {
        FileOutputStream fos = null;
        String outputFilePath = null;
        try {
            File outputDir = con.getExternalFilesDir("images");

            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }

            File outputFile = new File(outputDir, String.valueOf(System
                    .currentTimeMillis()));
            fos = new FileOutputStream(outputFile);
            Utils.copyLarge(is, fos);
            outputFilePath = outputFile.toString();
            logDebug("outputFilePath:" + outputFilePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public static void enableView(View v, boolean enable) {
        if (v.isEnabled() == enable) {
            return;
        }

        float from = enable ? .5f : 1.0f;
        float to = enable ? 1.0f : .5f;

        AlphaAnimation a = new AlphaAnimation(from, to);

        a.setDuration(0);
        a.setFillAfter(true);

        v.setEnabled(enable);
        v.startAnimation(a);
    }

    /**
     * ディスプレイ幅を返す
     *
     * @return
     */
    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    public static int getWidth(final Activity activity) {
        Display display;
        int size = 0;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
            display = wm.getDefaultDisplay();
            size = display.getWidth();
        } else {
            display = activity.getWindowManager().getDefaultDisplay();
            Point p = new Point();
            display.getSize(p);
            size = p.x;
        }
        return size;
    }

    /**
     * ディスプレイ高を返す
     *
     * @return
     */
    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    public static int getHeight(final Activity activity) {
        Display display;
        int size = 0;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
            display = wm.getDefaultDisplay();
            size = display.getHeight();
        } else {
            display = activity.getWindowManager().getDefaultDisplay();
            Point p = new Point();
            display.getSize(p);
            size = p.y;
        }
        return size;
    }

    /**
     * ディスプレイ密度を返す
     *
     * @return
     */
    public static float getDensity(final Activity activity) {
        return activity.getResources().getDisplayMetrics().density;
    }
}
