package sakurafish.com.parrot.uninstaller.ui;

import android.graphics.Bitmap;

/**
 * Created by sakura on 2014/10/14.
 */

public interface ImageCache {
    public Bitmap getBitmap(String url);

    public void putBitmap(String url, Bitmap bitmap);
}
