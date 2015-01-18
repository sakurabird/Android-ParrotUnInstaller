package sakurafish.com.parrot.uninstaller.utils;

import android.support.annotation.NonNull;

import java.util.Date;
import java.util.List;

import greendao.Apps;
import sakurafish.com.parrot.uninstaller.UninstallerApplication;

public class AppsTableAccessHelper {

    @Deprecated
    private AppsTableAccessHelper() {
    }

    /**
     * DBにアプリ情報を追加（複数レコード）
     *
     * @param appsList
     * @return
     */
    @NonNull
    public synchronized static int addAppsRecord(@NonNull final List<Apps> appsList) {
        if (appsList.size() == 0) return 0;

        //レコード追加
        for (final Apps apps : appsList) {
            UninstallerApplication.getDaoSession().getAppsDao().insertOrReplace(apps);
        }
        return appsList.size();
    }

    /**
     * DBにアプリ情報を追加（１レコード）
     *
     * @param apps
     */
    @NonNull
    public synchronized static void addAppsRecord(@NonNull final Apps apps) {
        //レコード追加
        UninstallerApplication.getDaoSession().getAppsDao().insertOrReplace(apps);
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
        Date date = isFavorite ? new Date() : null;
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
