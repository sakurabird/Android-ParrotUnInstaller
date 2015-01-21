package sakurafish.com.parrot.uninstaller.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import greendao.Apps;
import greendao.DaoSession;
import sakurafish.com.parrot.uninstaller.TutorialActivity;
import sakurafish.com.parrot.uninstaller.UninstallerApplication;
import sakurafish.com.parrot.uninstaller.config.Config;
import sakurafish.com.parrot.uninstaller.config.SectionCodes.SortOrder;
import sakurafish.com.parrot.uninstaller.events.DataChangedEvent;
import sakurafish.com.parrot.uninstaller.pref.Pref;
import sakurafish.com.parrot.uninstaller.tasks.CreateAppTable;
import sakurafish.com.parrot.uninstaller.ui.BitmapLruCache;

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

    protected abstract void getAppList(SortOrder sortOrder);

    public abstract void getAppList(String query);

    protected abstract void initLayout();

    protected abstract void listRefresh();

    /**
     * 初回のみアプリ情報のTable作成
     */
    private void createAppList() {
        new CreateAppTable(mContext, mPackageManager, new CreateAppTable.Callback() {
            @Override
            public void onFinish(boolean success) {
                Pref.setPref(mContext, Config.PREF_HAS_APP_TABLE, true);
                getAppList(SortOrder.NAME_ASC);
            }
        }).execute();
    }

    @Override
    public void onStart() {
        super.onStart();
        // EventBusを登録する
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        // EventBusを登録解除する
        EventBus.getDefault().unregister(this);
        super.onStop();
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
    }

    /**
     * DB変更の通知を受け取り画面を更新する<br>
     */
    public void onEvent(@NonNull final DataChangedEvent event){
        getAppList(mSortOrder);
        listRefresh();
    }
}
