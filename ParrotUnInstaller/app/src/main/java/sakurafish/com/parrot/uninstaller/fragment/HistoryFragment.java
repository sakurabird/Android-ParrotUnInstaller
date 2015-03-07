package sakurafish.com.parrot.uninstaller.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import de.greenrobot.dao.query.QueryBuilder;
import greendao.Apps;
import greendao.AppsDao;
import sakurafish.com.parrot.uninstaller.R;
import sakurafish.com.parrot.uninstaller.TopActivity;
import sakurafish.com.parrot.uninstaller.UninstallerApplication;
import sakurafish.com.parrot.uninstaller.config.Config;
import sakurafish.com.parrot.uninstaller.config.SectionCodes.SortOrder;
import sakurafish.com.parrot.uninstaller.pref.Pref;
import sakurafish.com.parrot.uninstaller.ui.AppItemClickListener;
import sakurafish.com.parrot.uninstaller.ui.GeneralDialogFragment;
import sakurafish.com.parrot.uninstaller.ui.HistoryListAdapter;
import sakurafish.com.parrot.uninstaller.utils.AppsTableAccessHelper;
import sakurafish.com.parrot.uninstaller.utils.ThemeUtils;
import sakurafish.com.parrot.uninstaller.utils.UnInstallerUtils;
import sakurafish.com.parrot.uninstaller.utils.Utils;

/**
 * Created by sakura on 2014/10/09.
 */
public class HistoryFragment extends BaseAppListFragment implements GeneralDialogFragment.Callbacks {
    private static final int REQUEST_CODE_DELETE = 222;
    private ListView mListView;
    private HistoryListAdapter mListAdaptor = null;

    public static HistoryFragment getInstance() {
        return new HistoryFragment();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mSortOrder = UnInstallerUtils.getDefaultSortOrder();
        ((TopActivity) mContext).setFragmentViews(Config.NavMenu.HISTORY);
    }

    /**
     * パラメータのソート順序に従いDBを読み込む
     *
     * @param sortOrder
     */
    public void getAppList(@NonNull final SortOrder sortOrder) {
        QueryBuilder<Apps> builder = mDaoSession.getAppsDao().queryBuilder();
        builder.where(AppsDao.Properties.Delete.eq(true));
        switch (sortOrder) {
            case NAME_ASC:
                mSortOrder = SortOrder.NAME_ASC;
                builder.orderAsc(AppsDao.Properties.Name).build();
                break;
            case NAME_DSC:
                mSortOrder = SortOrder.NAME_DSC;
                builder.orderDesc(AppsDao.Properties.Name).build();
                break;
            case DATE_ASC:
                mSortOrder = SortOrder.DATE_ASC;
                builder.orderAsc(AppsDao.Properties.Install_time).build();
                break;
            case DATE_DSC:
                mSortOrder = SortOrder.DATE_DSC;
                builder.orderDesc(AppsDao.Properties.Install_time).build();
                break;
            case SIZE_ASC:
                mSortOrder = SortOrder.SIZE_ASC;
                builder.orderAsc(AppsDao.Properties.Package_size).build();
                break;
            case SIZE_DSC:
                mSortOrder = SortOrder.SIZE_DSC;
                builder.orderDesc(AppsDao.Properties.Package_size).build();
                break;
            default:
                throw new AssertionError();
        }
        mAppList = builder.list();

        mListAdaptor.swapItems(mAppList);
    }

    /**
     * パラメータの文字列を含むレコードを読み込む
     *
     * @param query
     */
    public void getAppList(@NonNull final String query) {
        mAppList = mDaoSession.getAppsDao().queryBuilder()
                .where(AppsDao.Properties.Delete.eq(true))
                .where(AppsDao.Properties.Name.like("%" + query + "%"))
                .orderAsc(AppsDao.Properties.Name)
                .build()
                .list();

        mListAdaptor.swapItems(mAppList);
    }

    @Override
    public void initLayout() {
        RelativeLayout relativeLayout = (RelativeLayout) getView().findViewById(R.id.relativeLayout_top);
        ThemeUtils.setTopBackground(mContext, relativeLayout);

        mListView = (ListView) getView().findViewById(android.R.id.list);
        registerForContextMenu(mListView);

        View emptyList = ((LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.view_empty, (ViewGroup) mListView.getParent(), false);
        TextView textView = (TextView) emptyList.findViewById(R.id.empty);
        textView.setText(R.string.history_empty_text);
        ((ViewGroup) mListView.getParent()).addView(emptyList);
        mListView.setEmptyView(emptyList);

        mListAdaptor = new HistoryListAdapter(mContext);
        mListAdaptor.setOnAppItemClickListener(new AppItemClickListener() {
            @Override
            public void onClickItem(View view, Apps apps) {
                askDelete(view, apps);
            }
        });
        mListView.setAdapter(mListAdaptor);

        // 広告枠の設定
        RelativeLayout adarea = (RelativeLayout) getView().findViewById(R.id.ad_area);
        adarea.addView(UninstallerApplication.getAdContext()
                .createBanner(mContext, getResources().getInteger(R.integer.surupass_bannerId)));
    }

    private void askDelete(@NonNull final View view, @NonNull final Apps apps) {
        // 削除確認ダイアログ表示
        if (Pref.getPrefBool(mContext, Config.PREF_SOUND_ON, false)) {
            UninstallerApplication.getSoundManager().play(UninstallerApplication.getSoundIds()[2]);
        }
        GeneralDialogFragment.Builder builder = new GeneralDialogFragment.Builder();
        builder.setTitle(apps.getName());
        builder.setMessage(getString(R.string.history_confirm));
        builder.setCancelable(true);
        builder.setPositiveText(getString(R.string.delete));
        builder.setNegativeText(getString(android.R.string.cancel));
        Bundle bundle = new Bundle();
        bundle.putInt("REQUEST_CODE_DELETE", REQUEST_CODE_DELETE);
        bundle.putSerializable("Apps", apps);
        builder.setParams(bundle);
        builder.setTargetFragment(getFragmentManager().findFragmentById(R.id.content_frame),
                REQUEST_CODE_DELETE);
        GeneralDialogFragment fragment = builder.create();
        fragment.show(getFragmentManager(), "GeneralDialogFragment");
    }

    @Override
    public void listRefresh() {
        mListAdaptor.notifyDataSetChanged();
    }

    @Override
    public void onCreateContextMenu(final ContextMenu menu, final View v,
                                    final ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(final MenuItem item) {
        return super.onContextItemSelected(item);
    }

    private void finalizeLayout() {
        if (mListView != null) {
            mListView.setOnItemClickListener(null);
            mListView.setAdapter(null);
            mListView = null;
        }
        mListAdaptor = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        finalizeLayout();
    }

    @Override
    public void onDialogClicked(final String tag, final Bundle args, final int which, final boolean isChecked) {
        int reqCode = 999;
        if (args != null) {
            reqCode = args.getInt("REQUEST_CODE_DELETE", 999);
        }

        if (which == DialogInterface.BUTTON_POSITIVE) {
            if (reqCode == REQUEST_CODE_DELETE) {
                // 削除処理
                if (Pref.getPrefBool(mContext, Config.PREF_SOUND_ON, false)) {
                    UninstallerApplication.getSoundManager().play(UninstallerApplication.getSoundIds()[0]);
                }
                Apps apps = (Apps) args.getSerializable("Apps");
                AppsTableAccessHelper.deleteCompleteAppsRecord(apps);
                Utils.showToast((Activity) mContext, mContext.getString(R.string.delete_history_completed));
                getAppList(mSortOrder);
            }
        }
    }

    @Override
    public void onDialogCancelled(final String tag, final Bundle args) {
    }
}
