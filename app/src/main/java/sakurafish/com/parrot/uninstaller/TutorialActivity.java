package sakurafish.com.parrot.uninstaller;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.Window;

import sakurafish.com.parrot.uninstaller.fragment.TutorialFragment;
import sakurafish.com.parrot.uninstaller.utils.Utils;

public class TutorialActivity extends Activity {
    private Fragment mContent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.view_root);

        if (savedInstanceState == null) {
            mContent = TutorialFragment.getInstance();
        } else {
            mContent = getFragmentManager().getFragment(savedInstanceState, "mContent");
        }

        getFragmentManager().beginTransaction()
                .replace(R.id.content, mContent)
                .addToBackStack("TutorialFragment").commit();
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        getFragmentManager().putFragment(outState, "mContent", mContent);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        Utils.showToast(this, getString(R.string.tutorial_toast));
        finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mContent = null;
    }
}
