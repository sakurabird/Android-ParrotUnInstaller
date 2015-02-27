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
        NAME_ASC(1),
        /**
         * 名前降順
         */
        NAME_DSC(2),
        /**
         * 日付昇順
         */
        DATE_ASC(3),
        /**
         * 日付降順
         */
        DATE_DSC(4),
        /**
         * サイズ昇順
         */
        SIZE_ASC(5),
        /**
         * サイズ降順
         */
        SIZE_DSC(6);

        private int value;
        private SortOrder(int orderNo){
            this.value = orderNo;
        }

        public SortOrder getSortOrderFromNo(int orderNo){
            switch (orderNo){
                case 1:
                    return SortOrder.NAME_ASC;
                case 2:
                    return SortOrder.NAME_DSC;
                case 3:
                    return SortOrder.DATE_ASC;
                case 4:
                    return SortOrder.DATE_DSC;
                case 5:
                    return SortOrder.SIZE_ASC;
                case 6:
                    return SortOrder.SIZE_DSC;
                default:
                    return SortOrder.NAME_ASC;
            }
        }
    }
}
