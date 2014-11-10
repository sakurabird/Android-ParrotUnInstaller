package sakurafish.com.parrot.uninstaller.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.List;

import greendao.Apps;
import greendao.AppsDao;
import sakurafish.com.parrot.uninstaller.UninstallerApplication;
import sakurafish.com.parrot.uninstaller.config.Config;
import sakurafish.com.parrot.uninstaller.utils.AppsTableAccessHelper;
import sakurafish.com.parrot.uninstaller.utils.UnInstallerUtils;
import sakurafish.com.parrot.uninstaller.utils.Utils;

/**
 * Created by sakura on 2014/10/13.
 */
public class PackageIntentReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        final String action = intent.getAction();
        final Uri uri = intent.getData();
        final String pkg = uri != null ? uri.getSchemeSpecificPart() : null;
        if (TextUtils.isEmpty(pkg)) {
            return;
        }
        Utils.logDebug("onReceive pkg:" + pkg);

        if (action.equals(Intent.ACTION_PACKAGE_FULLY_REMOVED)) {
            removeFromDB(pkg);
            return;
        }

        if (action.equals(Intent.ACTION_PACKAGE_ADDED)) {
            addToDB(pkg);
            return;
        }
    }

    private void removeFromDB(@NonNull final String packageName) {
        final List<Apps> appsList = UninstallerApplication.getDaoSession().getAppsDao().queryBuilder()
                .where(AppsDao.Properties.Package_name.eq(packageName))
                .list();
        for (final Apps apps : appsList) {
            if (apps.getPackage_name().equals(packageName)) {
                AppsTableAccessHelper.deleteAppsRecord(apps);
                UnInstallerUtils.sendDBChangeBroadcast();
                return;
            }
        }
    }

    private void addToDB(@NonNull final String packageName) {
        final PackageManager packageManager = UninstallerApplication.getContext().getPackageManager();
        ApplicationInfo info = null;
        try {
            info = packageManager.getApplicationInfo(packageName, 0);
            final Apps apps = AppsTableAccessHelper.addAppsRecord(packageManager, info);
            if (apps == null) {
                Utils.logError("Error app record didn't add. package:" + info.packageName);
                return;
            }

            final Intent i = new Intent(Config.INTENT_DB_UPDTE_ACTION);
            UninstallerApplication.getContext().sendBroadcast(i);
        } catch (PackageManager.NameNotFoundException e) {
            Utils.logError("Error app record didn't add. package:" + info.packageName);
            e.printStackTrace();
            return;
        }
    }
}