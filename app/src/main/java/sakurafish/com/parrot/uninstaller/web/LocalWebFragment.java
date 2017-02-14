
package sakurafish.com.parrot.uninstaller.web;

import android.os.Bundle;


public class LocalWebFragment extends BaseWebFragment {

    public static LocalWebFragment getInstance(final String url, final String title) {
        final LocalWebFragment fragment = new LocalWebFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_URL, url);
        fragment.setArguments(bundle);
        return fragment;
    }
}
