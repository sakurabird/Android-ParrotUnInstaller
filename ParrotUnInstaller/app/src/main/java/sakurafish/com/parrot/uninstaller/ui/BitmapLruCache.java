package sakurafish.com.parrot.uninstaller.ui;

import android.graphics.Bitmap;
import android.util.LruCache;

import java.io.Serializable;

public class BitmapLruCache extends LruCache<String, Bitmap> implements ImageCache, Serializable {

    public BitmapLruCache() {
        this(getDefaultLruCacheSize());
    }

    public BitmapLruCache(int sizeInKiloBytes) {
        super(sizeInKiloBytes);
    }

    /**
     * LruCacheのデフォルトサイズ
     *
     * @return
     */
    public static int getDefaultLruCacheSize() {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;
        return cacheSize;
    }

    @Override
    protected int sizeOf(final String key, final Bitmap value) {
        return value.getRowBytes() * value.getHeight() / 1024;
    }

    @Override
    public Bitmap getBitmap(final String url) {
        return get(url);
    }

    @Override
    public void putBitmap(final String url, final Bitmap bitmap) {
        put(url, bitmap);
    }
}
