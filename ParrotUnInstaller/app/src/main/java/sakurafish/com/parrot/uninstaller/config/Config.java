package sakurafish.com.parrot.uninstaller.config;

public class Config {

    // preference
    public static final String PREF_HAS_APP_TABLE = "PREF_HAS_APP_TABLE";
    public static final String PREF_SHOW_FIRST_TUTORIAL = "PREF_SHOW_FIRST_TUTORIAL";
    public static final String PREF_LAUNCH_COUNT = "PREF_LAUNCH_COUNT";
    public static final String PREF_ASK_REVIEW = "PREF_ASK_REVIEW";
    public static final String PREF_SOUND_ON = "PREF_SOUND_ON";
    public static final String PREF_SHOW_STATUS_BAR = "PREF_SHOW_STATUS_BAR";
    public static final String PREF_FONT_SIZE = "PREF_FONT_SIZE";
    public static final String PREF_THEME = "PREF_THEME";
    public static final String PREF_SHOW_SYSTEM_APP = "PREF_SHOW_SYSTEM_APP";
    public static final String PREF_REFRESH = "PREF_REFRESH";
    public static final String PREF_CREDIT = "PREF_CREDIT";
    public static final String PREF_MAIL_TO_DEV = "PREF_MAIL_TO_DEV";

    // Intent
    public static final String INTENT_DB_UPDTE_ACTION = "INTENT_DB_UPDTE_ACTION";

    //    Tag string
    public static final String TAG_ERROR = "TAG_ERROR";

    // 文字の大きさ(px)
    public static final float FONT_SIZE_TITLE_L = 25.0f;
    public static final float FONT_SIZE_SUMMARY_L = 18.0f;
    public static final float FONT_SIZE_TITLE_M = 19.0f;
    public static final float FONT_SIZE_SUMMARY_M = 14.0f;
    public static final float FONT_SIZE_TITLE_S = 15.0f;
    public static final float[] FONT_SIZE_TITLES = {
            FONT_SIZE_TITLE_L,
            FONT_SIZE_TITLE_M,
            FONT_SIZE_TITLE_S
    };
    public static final float FONT_SIZE_SUMMARY_S = 10.0f;
    public static final float[] FONT_SIZE_FONT_SUMMARIES = {
            FONT_SIZE_SUMMARY_L,
            FONT_SIZE_SUMMARY_M,
            FONT_SIZE_SUMMARY_S
    };

    private Config() {
    }

    // Navigation Drawerのリスト位置
    public class NavMenu {
        public static final int HOME = 0;
        public static final int FAVOURITES = 1;
        public static final int HISTORY = 2;
        public static final int SETTINGS = 3;
        public static final int HELP = 4;
        public static final int SORT = 5;
        public static final int GOOGLEPLAY = 6;
        public static final int SHARE = 7;
    }

    // themeの選択された位置
    public class ThemeColor {
        public static final int GREEN = 1;
        public static final int PINK = 2;
        public static final int BLUE = 3;
        public static final int BROWN = 4;
        public static final int PURPLE = 5;
    }
}
