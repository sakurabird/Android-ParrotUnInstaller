package sakurafish.com.parrot.uninstaller.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import greendao.Apps;
import greendao.DaoSession;
import sakurafish.com.parrot.uninstaller.TutorialActivity;
import sakurafish.com.parrot.uninstaller.UninstallerApplication;
import sakurafish.com.parrot.uninstaller.config.Config;
import sakurafish.com.parrot.uninstaller.config.SectionCodes.SortOrder;
import sakurafish.com.parrot.uninstaller.tasks.CreateAppTable;
import sakurafish.com.parrot.uninstaller.ui.BitmapLruCache;
import sakurafish.com.parrot.uninstaller.pref.Pref;
import sakurafish.com.parrot.uninstaller.utils.Utils;

/**
 * Created by sakura on 2014/10/09.
 */
public abstract class BaseAppListFragment extends BaseFragment {

    protected Context mContext;
    protected PackageManager mPackageManager = null;
    protected DaoSession mDaoSession;
    protected List<Apps> mAppList = new ArrayList<Apps>();
    protected BitmapLruCache mLruCache;
    protected SortOrder mSortOrder = SortOrder.NAME_ASC;
    private DBUpdateReceiver mUpdateReceiver;

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mContext = getActivity();
        mPackageManager = mContext.getPackageManager();
        mDaoSession = UninstallerApplication.getDaoSession();
        mSortOrder = SortOrder.NAME_ASC;

        mLruCache = new BitmapLruCache();

        initLayout();

        if (!Pref.getPrefBool(mContext, Config.PREF_HAS_APP_TABLE, false)) {
            createAppList();
        } else {
            getAppList(mSortOrder);
        }

        // チュートリアルの表示
        if (!Pref.getPrefBool(mContext, Config.PREF_SHOW_FIRST_TUTORIAL, false)) {
            startActivity(new Intent(mContext, TutorialActivity.class));
            Pref.setPref(mContext, Config.PREF_SHOW_FIRST_TUTORIAL, true);
        }
    }

    public abstract void getAppList(SortOrder sortOrder);

    public abstract void getAppList(String query);

    public abstract void initLayout();

    public abstract void listRefresh();

    /**
     * 初回のみアプリ情報のTable作成
     */
    private void createAppList() {
        Utils.logDebug("createAppList1");
        new CreateAppTable(mContext, mPackageManager, new CreateAppTable.Callback() {
            @Override
            public void onFinish(boolean success) {
                Utils.logDebug("createAppList2");
                Pref.setPref(mContext, Config.PREF_HAS_APP_TABLE, true);
                getAppList(SortOrder.NAME_ASC);
            }
        }).execute();
    }

    @Override
    public void onResume() {
        super.onResume();

        IntentFilter filter = new IntentFilter();
        filter.addAction(Config.INTENT_DB_UPDTE_ACTION);
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        mUpdateReceiver = new DBUpdateReceiver();
        mContext.registerReceiver(mUpdateReceiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();

        mContext.unregisterReceiver(mUpdateReceiver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mContext = null;
        mPackageManager = null;
        mDaoSession = null;
        mAppList = null;
        mSortOrder = null;
        mLruCache = null;
        mUpdateReceiver = null;
    }

    /**
     * DB変更の通知を受け取り画面を更新する
     */
    public class DBUpdateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            String action = intent.getAction();
            if (action != null && action.equals(Config.INTENT_DB_UPDTE_ACTION)) {
                getAppList(mSortOrder);
                listRefresh();
            }
        }
    }
}
