package sakurafish.com.parrot.uninstaller.utils;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;

import java.io.File;
import java.util.Date;

import greendao.Apps;
import sakurafish.com.parrot.uninstaller.UninstallerApplication;

public class AppsTableAccessHelper {

    @Deprecated
    private AppsTableAccessHelper() {
    }

    /**
     * DBにアプリ情報を追加
     *
     * @param packageManager
     * @param info
     * @return
     */
    @NonNull
    public synchronized static Apps addAppsRecord(@NonNull final PackageManager packageManager, @NonNull final ApplicationInfo info) {

        final Apps apps = new Apps();
        apps.setName(info.loadLabel(packageManager).toString());
        apps.setPackage_name(info.packageName);

        apps.setPackage_size(0);
        final File file = new File(info.publicSourceDir);
        long size = file.length();
        apps.setPackage_size(size);

        Date date = null;
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

        //レコード追加
        UninstallerApplication.getDaoSession().getAppsDao().insertOrReplace(apps);

        return apps;
    }

    /**
     * DB更新
     *
     * @param apps
     * @return
     */
    public synchronized static void updateAppsRecord(@NonNull final Apps apps) {
        UninstallerApplication.getDaoSession().getAppsDao().update(apps);
    }

    /**
     * DBお気に入りフラグを更新
     *
     * @param apps
     * @return
     */
    public synchronized static void updateFavoriteAppsRecord(@NonNull final Apps apps, final boolean isFavorite) {
        apps.setFavorite(isFavorite);
        Date date = isFavorite == true ? new Date() : null;
        apps.setFavorite_added_time(date);

        UninstallerApplication.getDaoSession().getAppsDao().update(apps);
    }

    /**
     * DBに削除フラグを立てて更新
     *
     * @param apps
     * @return
     */
    public synchronized static void deleteAppsRecord(@NonNull final Apps apps) {
        apps.setDelete(true);
        apps.setDeleted_time(new Date());

        UninstallerApplication.getDaoSession().getAppsDao().update(apps);
    }

    /**
     * DBからレコードを完全に削除
     *
     * @param apps
     * @return
     */
    public synchronized static void deleteCompleteAppsRecord(@NonNull final Apps apps) {
        UninstallerApplication.getDaoSession().getAppsDao().delete(apps);
    }

    /**
     * DBから全レコードを完全に削除
     */
    public synchronized static void deleteAllRecord() {
        UninstallerApplication.getDaoSession().getAppsDao().deleteAll();
    }
}
