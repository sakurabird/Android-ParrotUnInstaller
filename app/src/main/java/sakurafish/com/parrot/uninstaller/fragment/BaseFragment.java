package sakurafish.com.parrot.uninstaller.fragment;

import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;

import sakurafish.com.parrot.uninstaller.TopActivity;
import sakurafish.com.parrot.uninstaller.config.Config;

public class BaseFragment extends Fragment {

    // 通信失敗時にエラーダイアログを表示する.
    protected boolean mShowErrorDialog = true;

    protected void switchFragment(@NonNull final Fragment fragment, @NonNull final String backStack) {
        if (getActivity() == null) {
            return;
        }

        if (getActivity() instanceof TopActivity) {
            TopActivity fca = (TopActivity) getActivity();
            fca.switchContent(fragment, backStack);
        }
    }

    protected void switchTopContent() {
        if (getActivity() == null) {
            return;
        }

        if (getActivity() instanceof TopActivity) {
            TopActivity fca = (TopActivity) getActivity();
            fca.switchTopContent();
        }
    }

    protected void showNetworkErrorDialog() {
        showDialog("ネットワークに接続できません",
                "恐れ入りますが、通信状況が良い環境で再度ご利用ください。");
    }

    protected void showErrorDialog() {
        showDialog("エラーが発生しました",
                "恐れ入りますが、しばらく時間をおいてから再度の操作をお願いいたします。");
    }

    protected void showDialog(@Nullable final String title, @NonNull final String message) {
        showDialog(title, message, Config.TAG_ERROR);
    }

    protected void showDialog(String title, final String message, final String tag) {
        if (TextUtils.isEmpty(title)) {
            title = "エラーが発生しました";
        }
        new MaterialDialog.Builder(getActivity())
                .theme(Theme.LIGHT)
                .title(title)
                .content(message)
                .positiveText(getString(android.R.string.ok))
                .show();
    }
}
