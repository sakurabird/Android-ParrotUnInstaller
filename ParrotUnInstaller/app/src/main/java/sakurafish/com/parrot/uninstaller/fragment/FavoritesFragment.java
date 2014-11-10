package sakurafish.com.parrot.uninstaller.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupMenu;
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
import sakurafish.com.parrot.uninstaller.ui.AppItemClickListener;
import sakurafish.com.parrot.uninstaller.ui.AppListAdapter;
import sakurafish.com.parrot.uninstaller.utils.AppsTableAccessHelper;
import sakurafish.com.parrot.uninstaller.pref.Pref;
import sakurafish.com.parrot.uninstaller.utils.ThemeUtils;
import sakurafish.com.parrot.uninstaller.utils.UnInstallerUtils;
import sakurafish.com.parrot.uninstaller.utils.Utils;

/**
 * Created by sakura on 2014/10/09.
 */
public class FavoritesFragment extends BaseAppListFragment {
    private ListView mListView;
    private AppListAdapter mListAdaptor = null;
    private TextView mAppCount;

    public static FavoritesFragment getInstance() {
        FavoritesFragment fragment = new FavoritesFragment();
        return fragment;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_top, container, false);
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((TopActivity) mContext).setFragmentViews(Config.NavMenu.FAVOURITES);
    }

    /**
     * パラメータのソート順序に従いDBを読み込む
     *
     * @param sortOrder
     */
    public void getAppList(@NonNull final SortOrder sortOrder) {
        QueryBuilder<Apps> builder = mDaoSession.getAppsDao().queryBuilder();
        builder.where(AppsDao.Properties.Delete.eq(false));
        builder.where(AppsDao.Properties.Favorite.eq(true));
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
                .where(AppsDao.Properties.Delete.eq(false))
                .where(AppsDao.Properties.Favorite.eq(true))
                .where(AppsDao.Properties.Name.like("%" + query + "%"))
                .orderAsc(AppsDao.Properties.Name)
                .build()
                .list();

        mListAdaptor.swapItems(mAppList);
    }

    @Override
    public void initLayout() {

        mAppCount = (TextView) getView().findViewById(R.id.app_count2);

        RelativeLayout relativeLayout = (RelativeLayout) getView().findViewById(R.id.relativeLayout_top);
        ThemeUtils.setTopBackground(mContext, relativeLayout);
        View view = getView().findViewById(R.id.divider);
        ThemeUtils.setDividerBackground(mContext, view);

        mListView = (ListView) getView().findViewById(android.R.id.list);
        registerForContextMenu(mListView);

        View emptyList = ((LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.view_empty, (ViewGroup) mListView.getParent(), false);
        TextView textView = (TextView) emptyList.findViewById(R.id.empty);
        textView.setText(R.string.favorite_empty_text);
        ((ViewGroup) mListView.getParent()).addView(emptyList);
        mListView.setEmptyView(emptyList);

        mListAdaptor = new AppListAdapter(mContext, mLruCache, mAppCount);
        mListAdaptor.setOnAppItemClickListener(new AppItemClickListener() {
            @Override
            public void onClickItem(View view, Apps apps) {
                showContextPopup(view, apps);
            }
        });
        mListView.setAdapter(mListAdaptor);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (Pref.getPrefBool(mContext, Config.PREF_SOUND_ON, false)) {
                    UninstallerApplication.getSoundManager().play(UninstallerApplication.getSoundIds()[0]);
                }
                mListAdaptor.swapClickedPosition(position);
            }
        });

        getView().findViewById(R.id.button_uninstall).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Pref.getPrefBool(mContext, Config.PREF_SOUND_ON, false)) {
                    UninstallerApplication.getSoundManager().play(UninstallerApplication.getSoundIds()[1]);
                }
                int selected = mListAdaptor.getSelected();
                if (selected <= 0) {
                    Utils.showToast((Activity) mContext, getString(R.string.error_unselected));
                    return;
                }
                boolean[] checkBoxes = mListAdaptor.getSelectedList();
                for (int i = 0; i < checkBoxes.length; i++) {
                    if (checkBoxes[i]) {
                        doUnInstall(mAppList.get(i));
                    }
                }
            }
        });

        // 広告枠の設定
        RelativeLayout adarea = (RelativeLayout) getView().findViewById(R.id.ad_area);
        adarea.addView(UninstallerApplication.getAdContext()
                .createBanner(mContext, getResources().getInteger(R.integer.surupass_bannerId)));
    }

    @Override
    public void listRefresh() {
        mListAdaptor.countSelected();
    }

    @Override
    public void onCreateContextMenu(final ContextMenu menu, final View v,
                                    final ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        AdapterView.AdapterContextMenuInfo adapterInfo = (AdapterView.AdapterContextMenuInfo) menuInfo;
        Apps apps = mAppList.get(adapterInfo.position);
        Drawable icon = null;
        try {
            icon = mPackageManager.getApplicationIcon(apps.getPackage_name());
            menu.setHeaderIcon(icon);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        menu.setHeaderTitle(apps.getName());

        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.context_top, menu);
        //お気に入りのところだけ文字を変更する
        menu.findItem(R.id.action_favorite).setTitle(R.string.action_unfavorite);
    }

    private void showContextPopup(@NonNull final View view, @NonNull final Apps apps) {
        final PopupMenu popupMenu = new PopupMenu(mContext, view);
        popupMenu.getMenuInflater().inflate(R.menu.context_top, popupMenu.getMenu());
        //お気に入りのところだけ文字を変更する
        popupMenu.getMenu().findItem(R.id.action_favorite).setTitle(R.string.action_unfavorite);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final MenuItem item) {
                doContextMenuAction(item, apps);
                return true;
            }
        });
        popupMenu.show();
    }

    @Override
    public boolean onContextItemSelected(@NonNull final MenuItem item) {
        if (Pref.getPrefBool(mContext, Config.PREF_SOUND_ON, false)) {
            UninstallerApplication.getSoundManager().play(UninstallerApplication.getSoundIds()[0]);
        }
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Apps apps = mAppList.get(info.position);
        doContextMenuAction(item, apps);
        return super.onContextItemSelected(item);
    }

    private void doContextMenuAction(@NonNull final MenuItem item, @NonNull final Apps apps) {
        if (Pref.getPrefBool(mContext, Config.PREF_SOUND_ON, false)) {
            UninstallerApplication.getSoundManager().play(UninstallerApplication.getSoundIds()[0]);
        }
        switch (item.getItemId()) {
            case R.id.action_uninstall:
                doUnInstall(apps);
                break;
            case R.id.action_launch:
                Intent intent = mPackageManager.getLaunchIntentForPackage(apps.getPackage_name());
                startActivity(intent);
                break;
            case R.id.action_details:
                intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS", Uri.parse("package:" + apps.getPackage_name()));
                startActivity(intent);
                break;
            case R.id.action_favorite:
                deleteFavorites(apps);
                break;
            case R.id.action_share:
                UnInstallerUtils.actionShare(mContext, apps);
                break;
            case R.id.action_googleplay:
                UnInstallerUtils.actionGooglePlay(mContext, apps);
                break;
        }
    }

    private void doUnInstall(@NonNull final Apps apps) {
        Uri packageURI = Uri.parse("package:" + apps.getPackage_name());
        Intent intent = new Intent(Intent.ACTION_DELETE, packageURI);
        startActivity(intent);
    }

    private void deleteFavorites(@NonNull final Apps apps) {
        AppsTableAccessHelper.updateFavoriteAppsRecord(apps, false);
        Utils.showToast((Activity) mContext, getString(R.string.unfavorite_completed));
        getAppList(mSortOrder);
    }

    private void finalizeLayout() {
        if (mListView != null) {
            mListView.setOnItemClickListener(null);
            mListView.setAdapter(null);
            mListView = null;
        }
        mListAdaptor = null;
        mAppCount = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        finalizeLayout();
    }
}
