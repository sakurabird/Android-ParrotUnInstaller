package sakurafish.com.parrot.uninstaller.pref;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import de.greenrobot.event.EventBus;
import sakurafish.com.parrot.uninstaller.R;
import sakurafish.com.parrot.uninstaller.TopActivity;
import sakurafish.com.parrot.uninstaller.UninstallerApplication;
import sakurafish.com.parrot.uninstaller.config.Config;
import sakurafish.com.parrot.uninstaller.events.DataChangedEvent;
import sakurafish.com.parrot.uninstaller.tasks.CreateAppTable;
import sakurafish.com.parrot.uninstaller.ui.GeneralDialogFragment;
import sakurafish.com.parrot.uninstaller.utils.AppsTableAccessHelper;
import sakurafish.com.parrot.uninstaller.utils.ThemeUtils;
import sakurafish.com.parrot.uninstaller.utils.UnInstallerUtils;
import sakurafish.com.parrot.uninstaller.utils.Utils;
import sakurafish.com.parrot.uninstaller.web.WebConsts;
import sakurafish.com.parrot.uninstaller.web.WebFragment;

/**
 * Created by sakura on 2014/10/24.
 */
public class PrefsFragment extends PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener, GeneralDialogFragment.Callbacks {
    private static final int REQUEST_CODE_REFRESH = 111;

    private Context mContext;

    public static PrefsFragment getInstance() {
        return new PrefsFragment();
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getActivity();
        addPreferencesFromResource(R.xml.preferences);

        initLayout();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((TopActivity) mContext).setFragmentViews(Config.NavMenu.SETTINGS);
    }

    private void initLayout() {
        Preference refresh = findPreference(Config.PREF_REFRESH);
        refresh.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                // アプリ一覧リセット確認ダイアログ表示
                if (Pref.getPrefBool(mContext, Config.PREF_SOUND_ON, false)) {
                    UninstallerApplication.getSoundManager().play(UninstallerApplication.getSoundIds()[2]);
                }
                GeneralDialogFragment.Builder builder = new GeneralDialogFragment.Builder();
                builder.setTitle(getString(R.string.setting_refresh));
                builder.setMessage(getString(R.string.setting_refresh_confirm));
                builder.setCancelable(true);
                builder.setPositiveText(getString(R.string.execute));
                builder.setNegativeText(getString(android.R.string.cancel));
                Bundle bundle = new Bundle();
                bundle.putInt("REQUEST_CODE_REFRESH", REQUEST_CODE_REFRESH);
                builder.setParams(bundle);
                builder.setTargetFragment(getFragmentManager().findFragmentById(R.id.content_frame),
                        REQUEST_CODE_REFRESH);
                GeneralDialogFragment fragment = builder.create();
                fragment.show(getFragmentManager(), "GeneralDialogFragment");

                return false;
            }
        });

        Preference credit = findPreference(Config.PREF_CREDIT);
        credit.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (mContext instanceof TopActivity) {
                    TopActivity a = (TopActivity) mContext;
                    a.switchContent(WebFragment.getInstance(WebConsts.LOCAL_CREDIT,
                            getString(R.string.setting_credit)), "WebFragment");
                }
                return false;
            }
        });

        Preference mailDev = findPreference(Config.PREF_MAIL_TO_DEV);
        mailDev.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("plain/text");
                intent.putExtra(Intent.EXTRA_EMAIL,  new String[] { "sakurafish1@gmail.com" });
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.setting_mail_to_dev2));
                intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.setting_mail_to_dev3));
                startActivity(intent);
                return false;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public void onSharedPreferenceChanged(final SharedPreferences sharedPreferences, final String key) {
        if (key.contains(Config.PREF_SHOW_STATUS_BAR)) {
            if (Pref.getPrefBool(mContext, Config.PREF_SHOW_STATUS_BAR, false)) {
                UnInstallerUtils.setNotification();
            } else {
                UnInstallerUtils.cancelNotification();
            }
        }

        if (key.contains(Config.PREF_SHOW_SYSTEM_APP)) {
            EventBus.getDefault().post(new DataChangedEvent());
        }

        if (key.contains(Config.PREF_THEME)) {
            ThemeUtils.changeToTheme(getActivity());
        }
    }

    /**
     * アプリ一覧リセット処理
     */
    private void refreshAppList() {
        AppsTableAccessHelper.deleteAllRecord();
        new CreateAppTable(mContext, mContext.getPackageManager(), new CreateAppTable.Callback() {
            @Override
            public void onFinish(boolean success) {
                if (Pref.getPrefBool(mContext, Config.PREF_SOUND_ON, false)) {
                    UninstallerApplication.getSoundManager().play(UninstallerApplication.getSoundIds()[0]);
                }
                EventBus.getDefault().post(new DataChangedEvent());
                Utils.showToast((Activity) mContext, getString(R.string.setting_refresh_completed));
            }
        }).execute();
    }

    @Override
    public void onDialogClicked(final String tag, final Bundle args, final int which, final boolean isChecked) {
        int reqCode = 999;
        if (args != null) {
            reqCode = args.getInt("REQUEST_CODE_REFRESH", 999);
        }

        if (which == DialogInterface.BUTTON_POSITIVE) {
            if (reqCode == REQUEST_CODE_REFRESH) {
                // アプリ一覧リセット処理
                refreshAppList();
            }
        }
    }

    @Override
    public void onDialogCancelled(final String tag, final Bundle args) {

    }
}
