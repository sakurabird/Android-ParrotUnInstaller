
package sakurafish.com.parrot.uninstaller.web;

import android.os.Bundle;

import sakurafish.com.parrot.uninstaller.utils.Utils;


public class WebFragment extends BaseWebFragment {

    public static WebFragment getInstance(final String url, final String title) {
        final WebFragment fragment = new WebFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_URL, url);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (!Utils.isConnected()) {
            showNetworkErrorDialog();
        }
    }
}
