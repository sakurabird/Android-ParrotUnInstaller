package sakurafish.com.parrot.uninstaller.config;

/**
 * 区分用クラス
 */
public class SectionCodes {
    @Deprecated
    private SectionCodes() {
    }

    /**
     * ソート用
     */
    public enum SortOrder {
        /**
         * 名前昇順
         */
        NAME_ASC,
        /**
         * 名前降順
         */
        NAME_DSC,
        /**
         * 日付昇順
         */
        DATE_ASC,
        /**
         * 日付降順
         */
        DATE_DSC,
        /**
         * サイズ昇順
         */
        SIZE_ASC,
        /**
         * サイズ降順
         */
        SIZE_DSC;
    }

}
