package sakurafish.com.parrot.uninstaller.utils;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.io.File;
import java.util.Date;

import greendao.Apps;
import sakurafish.com.parrot.uninstaller.R;
import sakurafish.com.parrot.uninstaller.TopActivity;
import sakurafish.com.parrot.uninstaller.UninstallerApplication;
import sakurafish.com.parrot.uninstaller.config.Config;
import sakurafish.com.parrot.uninstaller.config.SectionCodes;
import sakurafish.com.parrot.uninstaller.pref.Pref;

public class UnInstallerUtils {

    @Deprecated
    private UnInstallerUtils() {
    }

    public static SectionCodes.SortOrder getDefaultSortOrder() {
        SectionCodes.SortOrder sortOrder = SectionCodes.SortOrder.DATE_ASC;
        int no = Integer.parseInt(Pref.getSharedPreferences(UninstallerApplication.getContext()).getString(Config.PREF_SORT_ORDER, "1"));
        sortOrder = sortOrder.getSortOrderFromNo(no);
        return sortOrder;
    }

    /**
     * コンテキストメニューからシェアする
     *
     * @param context
     * @param apps
     */
    public static void actionShare(final Context context, final Apps apps) {
        final Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, UnInstallerUtils.createShareText(apps));
        intent.putExtra(Intent.EXTRA_SUBJECT, "App : " + apps.getName());
        intent.setType("text/plain");
        context.startActivity(Intent.createChooser(intent, context.getResources().getText(R.string.send_to)));
    }

    /**
     * シェア文字列作成
     *
     * @param apps
     * @return
     */
    @NonNull
    public static String createShareText(final Apps apps) {
        final StringBuffer message = new StringBuffer();
        message.append(apps.getName());
        message.append("\n");
        message.append("http://play.google.com/store/apps/details?id=");
        message.append(apps.getPackage_name());
        message.append("\n");
        return message.toString();
    }

    /**
     * コンテキストメニューからGoogle Playに遷移する
     *
     * @param context
     * @param apps
     */
    public static void actionGooglePlay(final Context context, final Apps apps) {
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + apps.getPackage_name())));
        } catch (android.content.ActivityNotFoundException e) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + apps.getPackage_name())));
        } catch (Exception e2) {
            Utils.showToast((Activity) context, context.getString(R.string.error_googleplay));
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/")));
        }
    }

    /**
     * ステータスバーにアプリを常駐させる
     */
    public static void setNotification() {
        final Context context = UninstallerApplication.getContext();
        final NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        final Intent intent = new Intent(context, TopActivity.class);
        final PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, 0);

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(getNotificationIcon())
                .setContentTitle(context.getResources().getString(R.string.app_name))
                .setContentText(context.getString(R.string.launch_app))
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(false)
                .setOngoing(true)
                .setContentIntent(contentIntent);

        notificationManager.cancel(R.string.app_name);
        notificationManager.notify(R.string.app_name, builder.build());
    }

    private static int getNotificationIcon() {
        boolean whiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return whiteIcon ? R.drawable.notification_icon : R.drawable.ic_launcher;
    }

    /**
     * ステータスバーからアプリを除去する
     */
    public static void cancelNotification() {
        final NotificationManager notificationManager = (NotificationManager) UninstallerApplication.getContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(R.string.app_name);
    }

    /**
     * ApplicationInfoの情報からAppsの項目をセットする
     *
     * @param packageManager
     * @param info
     * @return
     */
    public static Apps createAppsFromApplicationInfo(PackageManager packageManager, ApplicationInfo info) {
        final Apps apps = new Apps();
        apps.setName(info.loadLabel(packageManager).toString());
        apps.setPackage_name(info.packageName);

        apps.setPackage_size(0);
        final File file = new File(info.publicSourceDir);
        long size = file.length();
        apps.setPackage_size(size);

        Date date;
        try {
            date = new Date(packageManager.getPackageInfo(info.packageName, PackageManager.GET_META_DATA).firstInstallTime);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        apps.setInstall_time(date);

        if ((info.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
            apps.setSystem_app(true);
        } else {
            apps.setSystem_app(false);
        }

        apps.setFavorite(false);
        apps.setFavorite_added_time(null);
        apps.setDelete(false);
        apps.setDeleted_time(null);

        return apps;
    }

    /**
     * Google Analyticsにスクリーンのログを送信する
     *
     * @param screenName
     */
    public static void sendScreenToGA(@Nullable final String screenName) {
        Tracker t = UninstallerApplication.getInstance().getDefaultTracker();
        t.setScreenName(screenName);
        t.send(new HitBuilders.ScreenViewBuilder().build());
    }
}
