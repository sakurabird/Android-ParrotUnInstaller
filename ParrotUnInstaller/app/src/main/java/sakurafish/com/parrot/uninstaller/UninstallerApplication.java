package sakurafish.com.parrot.uninstaller;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import greendao.DaoMaster;
import greendao.DaoSession;
import sakurafish.com.parrot.uninstaller.config.Config;
import sakurafish.com.parrot.uninstaller.pref.Pref;
import sakurafish.com.parrot.uninstaller.utils.SoundManager;
import tokyo.suru_pass.AdContext;

public class UninstallerApplication extends Application {

    public static DaoSession daoSession;
    private static UninstallerApplication application;
    private static SoundManager sSoundManager;
    private static int[] sSoundIds = new int[4];
    private static AdContext sAdContext;//suru pass
    private Tracker mTracker;

    public UninstallerApplication() {
        super();
        application = this;
    }

    public static UninstallerApplication getInstance() {
        return application;
    }

    public static Context getContext() {
        return application.getApplicationContext();
    }

    public static DaoSession getDaoSession() {
        return daoSession;
    }

    public static SoundManager getSoundManager() {
        return sSoundManager;
    }

    public static int[] getSoundIds() {
        return sSoundIds;
    }

    public static AdContext getAdContext() {
        return sAdContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mTracker = GoogleAnalytics.getInstance(this).newTracker(R.xml.global_tracker);

        setupDatabase();

        int launchCount = Pref.getPrefInt(getApplicationContext(), Config.PREF_LAUNCH_COUNT);
        Pref.setPref(getApplicationContext(), Config.PREF_LAUNCH_COUNT, ++launchCount);

        setupSounds();

        setupADs();
    }

    public synchronized Tracker getTracker() {
        return mTracker;
    }

    private void setupDatabase() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "parrotuninstaller.db", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
    }

    private void setupSounds() {
        sSoundManager = SoundManager.getInstance(getContext());
        sSoundIds[0] = sSoundManager.load(R.raw.inco1);
        sSoundIds[1] = sSoundManager.load(R.raw.inco2);
        sSoundIds[2] = sSoundManager.load(R.raw.inco3);
        sSoundIds[3] = sSoundManager.load(R.raw.inco4);
    }

    /**
     * 広告SDKのインスタンスを取得
     */
    private void setupADs() {
        //Suru Pass
        sAdContext = new AdContext(getContext(), getString(R.string.surupass_mediaId), BuildConfig.DEBUG);
    }
}