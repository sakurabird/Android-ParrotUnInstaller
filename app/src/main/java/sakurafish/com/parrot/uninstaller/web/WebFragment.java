
package sakurafish.com.parrot.uninstaller.web;

import android.os.Bundle;


public class WebFragment extends BaseWebFragment {

    public static WebFragment getInstance(final String url, final String title) {
        final WebFragment fragment = new WebFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_URL, url);
        fragment.setArguments(bundle);
        return fragment;
    }
}
