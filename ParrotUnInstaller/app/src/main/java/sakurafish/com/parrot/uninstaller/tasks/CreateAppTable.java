package sakurafish.com.parrot.uninstaller.tasks;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import greendao.Apps;
import greendao.DaoSession;
import sakurafish.com.parrot.uninstaller.UninstallerApplication;
import sakurafish.com.parrot.uninstaller.ui.IncoProgressDialog;
import sakurafish.com.parrot.uninstaller.utils.AppsTableAccessHelper;
import sakurafish.com.parrot.uninstaller.utils.UnInstallerUtils;
import sakurafish.com.parrot.uninstaller.utils.Utils;

/**
 * アプリ情報の取得（初回のみ）
 * Created by sakura on 2014/10/12.
 */
public class CreateAppTable extends AsyncTask<Void, Void, Void> {
    private Callback mCallback;
    private Context mContext;
    private PackageManager mPackageManager = null;
    private IncoProgressDialog mProgressDialog;
    private DaoSession mDaoSession;

    public CreateAppTable(final Context context, final PackageManager packageManager, final Callback callback) {
        mCallback = callback;
        mContext = context;
        mPackageManager = packageManager;
    }

    @Override
    protected void onPreExecute() {
        mProgressDialog = new IncoProgressDialog(mContext);
        mProgressDialog.show();
        mDaoSession = UninstallerApplication.getDaoSession();
        mDaoSession.getAppsDao().deleteAll();

        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(final Void... params) {
        addRecord(mPackageManager.getInstalledApplications(PackageManager.GET_META_DATA));
        return null;
    }

    @Override
    protected void onPostExecute(final Void param) {
        super.onPostExecute(param);
        mProgressDialog.dismiss();
        mCallback.onFinish(true);
    }

    @Override
    protected void onProgressUpdate(final Void... values) {
        super.onProgressUpdate(values);
    }

    private void addRecord(@Nullable final List<ApplicationInfo> list) {
        if (list == null) return;

        Utils.logDebug("計測スタート");
        long start = System.currentTimeMillis();

        List<Apps> appsList=new ArrayList<Apps>();



        for (final ApplicationInfo info : list) {

            try {
                if (null != mPackageManager.getLaunchIntentForPackage(info.packageName)) {

                    final Apps apps = UnInstallerUtils.createAppsFromApplicationInfo(mPackageManager, info);
                    if (apps == null) {
                        Utils.logError("Error app record didn't add. package:" + info.packageName);
                        break;
                    }
                    appsList.add(apps);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Utils.logError("Error app record didn't add. package:" + info.packageName);
                break;
            }
        }
        AppsTableAccessHelper.addAppsRecord(appsList);

        long stop = System.currentTimeMillis();
        Utils.logDebug("実行にかかった時間は " + (stop - start) + " ミリ秒です。");
    }

    public interface Callback {
        void onFinish(boolean success);
    }
}
