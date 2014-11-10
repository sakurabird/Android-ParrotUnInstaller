package sakurafish.com.parrot.uninstaller.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import sakurafish.com.parrot.uninstaller.config.Config;
import sakurafish.com.parrot.uninstaller.utils.UnInstallerUtils;
import sakurafish.com.parrot.uninstaller.pref.Pref;
import sakurafish.com.parrot.uninstaller.utils.Utils;

/**
 * 端末を起動した時に呼び出される <br>
 */
public class BootBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        Utils.logDebug("onReceive");
        if (Pref.getPrefBool(context, Config.PREF_SHOW_STATUS_BAR, false)) {
            UnInstallerUtils.setNotification();
        }
    }
}
