package sakurafish.com.parrot.uninstaller;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;

import sakurafish.com.parrot.uninstaller.config.Config;
import sakurafish.com.parrot.uninstaller.config.Config.NavMenu;
import sakurafish.com.parrot.uninstaller.config.SectionCodes.SortOrder;
import sakurafish.com.parrot.uninstaller.fragment.FavoritesFragment;
import sakurafish.com.parrot.uninstaller.fragment.HelpFragment;
import sakurafish.com.parrot.uninstaller.fragment.HistoryFragment;
import sakurafish.com.parrot.uninstaller.fragment.TopFragment;
import sakurafish.com.parrot.uninstaller.pref.Pref;
import sakurafish.com.parrot.uninstaller.pref.PrefsFragment;
import sakurafish.com.parrot.uninstaller.tasks.RetrieveReleasedVersion;
import sakurafish.com.parrot.uninstaller.ui.GeneralDialogFragment;
import sakurafish.com.parrot.uninstaller.ui.NavExpandableAdapter;
import sakurafish.com.parrot.uninstaller.utils.ThemeUtils;
import sakurafish.com.parrot.uninstaller.utils.UnInstallerUtils;
import sakurafish.com.parrot.uninstaller.utils.Utils;
import sakurafish.com.parrot.uninstaller.web.WebFragment;


public class TopActivity extends ActionBarActivity implements GeneralDialogFragment.Callbacks {

    private static final int REQUEST_CODE_REVIEW = 112;
    private static final int REQUEST_CODE_UPDATE = 113;
    protected SortOrder mSortOrder = SortOrder.NAME_ASC;
    private Context mContext;
    private Fragment mContent;
    private DrawerLayout mDrawerLayout;
    private ExpandableListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private SearchView mSearchView;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;

        ThemeUtils.onActivityCreateSetTheme(this);
        setContentView(R.layout.activity_top);

        init();

        initLayout();

        if (savedInstanceState == null) {
            mContent = TopFragment.getInstance();
        } else {
            mContent = getFragmentManager().getFragment(savedInstanceState, "mContent");
            if (mContent == null) mContent = TopFragment.getInstance();
        }

        getFragmentManager().beginTransaction()
                .replace(R.id.content_frame, mContent).commit();

        getFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                mContent = getFragmentManager().findFragmentById(R.id.content_frame);
                if (mContent instanceof TopFragment) {
                    setFragmentViews(NavMenu.HOME);
                    return;
                }
                if (mContent instanceof FavoritesFragment) {
                    setFragmentViews(NavMenu.FAVOURITES);
                    return;
                }
                if (mContent instanceof HistoryFragment) {
                    setFragmentViews(NavMenu.HISTORY);
                    return;
                }
                if (mContent instanceof PrefsFragment) {
                    setFragmentViews(NavMenu.SETTINGS);
                    return;
                }
                if (mContent instanceof HelpFragment) {
                    setFragmentViews(NavMenu.HELP);
                    return;
                }
            }
        });
    }

    private void init() {
        mSortOrder = UnInstallerUtils.getDefaultSortOrder();

        if (Pref.getPrefBool(mContext, Config.PREF_SHOW_STATUS_BAR, false)) {
            UnInstallerUtils.setNotification();
        } else {
            UnInstallerUtils.cancelNotification();
        }

        if (!Pref.getPrefBool(mContext, Config.PREF_ASK_REVIEW, false)) {
            if (Pref.getPrefInt(mContext, Config.PREF_LAUNCH_COUNT) == 10) {
                pleaseReview();
            }
        }

        new RetrieveReleasedVersion(mContext, new RetrieveReleasedVersion.Callback() {
            @Override
            public void onFinish(final String version) {
                if (version == null) return;
                final String thisVersion = Utils.getVersionName();
                if (thisVersion == null) return;
                if (!version.equals(thisVersion)) {
                    pleaseUpdate();
                }
            }
        }).execute();

        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    private void pleaseReview() {
        //10回めの起動でレビュー誘導
        final GeneralDialogFragment.Builder builder = new GeneralDialogFragment.Builder();
        builder.setTitle(getString(R.string.ask_review_title));
        builder.setMessage(getString(R.string.ask_review_message));
        builder.setCancelable(true);
        builder.setPositiveText(getString(android.R.string.ok));
        builder.setNegativeText(getString(android.R.string.cancel));
        final Bundle bundle = new Bundle();
        bundle.putInt("REQUEST_CODE_REVIEW", REQUEST_CODE_REVIEW);
        builder.setParams(bundle);
        builder.setTargetFragment(getFragmentManager().findFragmentById(R.id.content_frame),
                REQUEST_CODE_REVIEW);
        GeneralDialogFragment fragment = builder.create();
        fragment.show(getFragmentManager(), "GeneralDialogFragment");

        Pref.setPref(mContext, Config.PREF_ASK_REVIEW, true);
    }

    private void pleaseUpdate() {
        //アップデートへ誘導する
        final GeneralDialogFragment.Builder builder = new GeneralDialogFragment.Builder();
        builder.setTitle(getString(R.string.ask_update_title));
        builder.setMessage(getString(R.string.ask_update_message));
        builder.setCancelable(true);
        builder.setPositiveText(getString(android.R.string.ok));
        builder.setNegativeText(getString(android.R.string.cancel));
        final Bundle bundle = new Bundle();
        bundle.putInt("REQUEST_CODE_UPDATE", REQUEST_CODE_UPDATE);
        builder.setParams(bundle);
        builder.setTargetFragment(getFragmentManager().findFragmentById(R.id.content_frame),
                REQUEST_CODE_UPDATE);
        GeneralDialogFragment fragment = builder.create();
        fragment.show(getFragmentManager(), "GeneralDialogFragment");
    }

    @Override
    protected void onPostCreate(final Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(final Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    private void initLayout() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.top_title));

        mSearchView = (SearchView) findViewById(R.id.action_search);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {
                doSearchAction(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {
                doSearchAction(newText);
                return false;
            }
        });

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar,
                0, 0) {
            @Override
            public void onDrawerClosed(final View drawerView) {
            }

            @Override
            public void onDrawerOpened(final View drawerView) {
            }

            @Override
            public void onDrawerSlide(final View drawerView, final float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                mSearchView.setIconified(true);
            }

            @Override
            public void onDrawerStateChanged(final int newState) {
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerList = (ExpandableListView) findViewById(R.id.left_drawer);
        ThemeUtils.setNavBackground(mContext, mDrawerList, false);
        mDrawerList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(final ExpandableListView parent, final View v, final int groupPosition, final long id) {
                actionGroupNavi(groupPosition);
                return false;
            }
        });
        mDrawerList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(final ExpandableListView parent, final View v, final int groupPosition, final int childPosition, final long id) {
                actionChildNavi(childPosition);
                return false;
            }
        });
        mDrawerList.setAdapter(new NavExpandableAdapter(mContext));
    }

    private void actionGroupNavi(final int position) {
        if (Pref.getPrefBool(mContext, Config.PREF_SOUND_ON, false)) {
            UninstallerApplication.getSoundManager().play(UninstallerApplication.getSoundIds()[3]);
        }
        switch (position) {
            case NavMenu.HOME:
                switchTopContent();
                break;
            case NavMenu.SORT:
                break;
            case NavMenu.FAVOURITES:
                switchContent(FavoritesFragment.getInstance(), "FavoritesFragment");
                break;
            case NavMenu.HISTORY:
                switchContent(HistoryFragment.getInstance(), "HistoryFragment");
                break;
            case NavMenu.SETTINGS:
                switchContent(PrefsFragment.getInstance(), "PrefsFragment");
                break;
            case NavMenu.GOOGLEPLAY:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/")));
                break;
            case NavMenu.SHARE:
                final Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text));
                intent.setType("text/plain");
                startActivity(intent);
                break;
            case NavMenu.HELP:
                switchContent(HelpFragment.getInstance(), "HelpFragment");
                break;
        }

        if (position != NavMenu.SORT) {
            mDrawerLayout.closeDrawers();
        }
    }

    public void setFragmentViews(final int position) {
        switch (position) {
            case NavMenu.HOME:
                getSupportActionBar().setTitle(getString(R.string.top_title));
                mSearchView.setVisibility(View.VISIBLE);
                break;
            case NavMenu.FAVOURITES:
                getSupportActionBar().setTitle(getString(R.string.favorite_title));
                mSearchView.setVisibility(View.VISIBLE);
                break;
            case NavMenu.HISTORY:
                getSupportActionBar().setTitle(getString(R.string.history_title));
                mSearchView.setVisibility(View.VISIBLE);
                break;
            case NavMenu.SETTINGS:
                getSupportActionBar().setTitle(getString(R.string.action_settings));
                mSearchView.setVisibility(View.INVISIBLE);
                break;
            case NavMenu.HELP:
                getSupportActionBar().setTitle(getString(R.string.help));
                mSearchView.setVisibility(View.INVISIBLE);
                break;
        }
    }

    private void actionChildNavi(final int position) {
        if (mContent instanceof TopFragment
                || mContent instanceof FavoritesFragment
                || mContent instanceof HistoryFragment) {
        } else {
            if (Pref.getPrefBool(mContext, Config.PREF_SOUND_ON, false)) {
                UninstallerApplication.getSoundManager().play(UninstallerApplication.getSoundIds()[2]);
            }
            Utils.showToast((Activity) mContext, getString(R.string.error_nav_illegal));
            mDrawerList.collapseGroup(NavMenu.SORT);
            return;
        }
        if (Pref.getPrefBool(mContext, Config.PREF_SOUND_ON, false)) {
            UninstallerApplication.getSoundManager().play(UninstallerApplication.getSoundIds()[0]);
        }

        //ソートする
        switch (position) {
            case 0:
                mSortOrder = SortOrder.NAME_ASC;
                break;
            case 1:
                mSortOrder = SortOrder.NAME_DSC;
                break;
            case 2:
                mSortOrder = SortOrder.DATE_ASC;
                break;
            case 3:
                mSortOrder = SortOrder.DATE_DSC;
                break;
            case 4:
                mSortOrder = SortOrder.SIZE_ASC;
                break;
            case 5:
                mSortOrder = SortOrder.SIZE_DSC;
                break;
        }

        if (mContent instanceof TopFragment) {
            ((TopFragment) mContent).getAppList(mSortOrder);
        }
        if (mContent instanceof FavoritesFragment) {
            ((FavoritesFragment) mContent).getAppList(mSortOrder);
        }
        if (mContent instanceof HistoryFragment) {
            ((HistoryFragment) mContent).getAppList(mSortOrder);
        }

        mDrawerList.collapseGroup(NavMenu.SORT);
        mDrawerLayout.closeDrawers();
    }

    private void doSearchAction(final String query) {

        if (Pref.getPrefBool(mContext, Config.PREF_SOUND_ON, false)) {
            UninstallerApplication.getSoundManager().play(UninstallerApplication.getSoundIds()[3]);
        }
        if (TextUtils.isEmpty(query)) {
            ((TopFragment) mContent).getAppList(mSortOrder);
            return;
        }

        if (mContent instanceof TopFragment) {
            ((TopFragment) mContent).getAppList(query);
            return;
        }
        if (mContent instanceof HistoryFragment) {
            ((HistoryFragment) mContent).getAppList(query);
            return;
        }
        if (mContent instanceof FavoritesFragment) {
            ((FavoritesFragment) mContent).getAppList(query);
        }
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        getFragmentManager().putFragment(outState, "mContent", mContent);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mContent = getFragmentManager().getFragment(savedInstanceState, "mContent");
    }

    public void switchTopContent() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack(
                    getFragmentManager().getBackStackEntryAt(0).getId(),
                    FragmentManager.POP_BACK_STACK_INCLUSIVE);
            mContent = getFragmentManager().findFragmentById(R.id.content_frame);
        }
    }

    public void switchContent(final Fragment fragment, final String backStack) {
        mContent = fragment;
        getFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_right)
                .replace(R.id.content_frame, fragment)
                .addToBackStack(backStack).commit();
    }

    @Override
    public void onBackPressed() {
        mSearchView.setIconified(true);
        mDrawerLayout.closeDrawers();

        if (mContent instanceof WebFragment) {
            final WebFragment fragment = (WebFragment) mContent;
            if (!fragment.onBackPressed()) {
            } else {
                return;
            }
        }

        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
            mContent = getFragmentManager().findFragmentById(R.id.content_frame);
        } else {
            super.onBackPressed();
        }
    }

    private void finalizeLayout() {
        mSearchView = null;
        mDrawerLayout = null;
        mDrawerList = null;
        mDrawerToggle = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        finalizeLayout();
        mContent = null;
        mContext = null;
    }

    @Override
    public void onDialogClicked(final String tag, final Bundle args, final int which, final boolean isChecked) {
        int reqCode = 999;
        if (args != null) {
            reqCode = args.getInt("REQUEST_CODE_REVIEW", 999);
            if (reqCode == 999) {
                reqCode = args.getInt("REQUEST_CODE_UPDATE", 999);
            }
        }

        if (which == DialogInterface.BUTTON_POSITIVE) {
            switch (reqCode) {
                case REQUEST_CODE_REVIEW:
                case REQUEST_CODE_UPDATE:
                    // Google Playに移動
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.app_url))));
                    break;
            }
        }
    }

    @Override
    public void onDialogCancelled(final String tag, final Bundle args) {

    }
}
