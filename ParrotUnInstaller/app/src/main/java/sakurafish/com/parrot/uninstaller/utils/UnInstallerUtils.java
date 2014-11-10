package sakurafish.com.parrot.uninstaller.utils;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;

import greendao.Apps;
import sakurafish.com.parrot.uninstaller.UninstallerApplication;
import sakurafish.com.parrot.uninstaller.R;
import sakurafish.com.parrot.uninstaller.TopActivity;
import sakurafish.com.parrot.uninstaller.config.Config;

public class UnInstallerUtils {

    @Deprecated
    private UnInstallerUtils() {
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
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(context.getResources().getString(R.string.app_name))
                .setContentText(context.getString(R.string.launch_app))
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(false)
                .setOngoing(true)
                .setContentIntent(contentIntent);

        notificationManager.cancel(R.string.app_name);
        notificationManager.notify(R.string.app_name, builder.build());
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
     * DBの変更を通知する
     */
    public static void sendDBChangeBroadcast() {
        final Intent i = new Intent(Config.INTENT_DB_UPDTE_ACTION);
        UninstallerApplication.getContext().sendBroadcast(i);
    }
}
