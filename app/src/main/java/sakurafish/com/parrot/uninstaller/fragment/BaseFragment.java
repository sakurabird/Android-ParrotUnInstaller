package sakurafish.com.parrot.uninstaller.fragment;

import android.app.DialogFragment;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import sakurafish.com.parrot.uninstaller.R;
import sakurafish.com.parrot.uninstaller.TopActivity;
import sakurafish.com.parrot.uninstaller.config.Config;
import sakurafish.com.parrot.uninstaller.ui.GeneralDialogFragment;

public class BaseFragment extends Fragment {

    protected static final String DIALOG_CONNECTING = "connecting_fragment";
    private final static int REQUEST_DIALOG_ERROR = 999;
    // 通信失敗時にエラーダイアログを表示する.
    protected boolean mShowErrorDialog = true;
    private GeneralDialogFragment mDialogFragment;

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        dismissConnectingDialog();
        mDialogFragment = null;
    }


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
        GeneralDialogFragment.Builder builder = new GeneralDialogFragment.Builder();
        builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveText("OK");
        builder.setTargetFragment(this, REQUEST_DIALOG_ERROR);
        builder.create().show(getFragmentManager(), tag);
    }

    protected void dismissConnectingDialog() {
        if (mDialogFragment != null) {
            mDialogFragment.dismiss();
            mDialogFragment = null;
        } else {
            if (getFragmentManager() != null) {
                Fragment fragment = getFragmentManager().findFragmentByTag(
                        DIALOG_CONNECTING);
                if (fragment instanceof DialogFragment) {
                    DialogFragment dialog = (DialogFragment) fragment;
                    dialog.onDismiss(null);
                }
            }
        }
    }
}
