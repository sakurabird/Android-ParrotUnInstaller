package sakurafish.com.parrot.uninstaller.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.support.annotation.NonNull;

public class SoundManager {

    private static SoundManager sSoundManager = null;
    private static Context sContext;
    public static SoundPool sSoundPool;
    private static float rate = 1.0f;
    private static float masterVolume = 1.0f;
    private static float leftVolume = 1.0f;
    private static float rightVolume = 1.0f;
    private static float balance = 0.5f;

    private SoundManager(@NonNull final Context context) {
        sSoundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        sContext = context;
    }

    @NonNull
    public static SoundManager getInstance(@NonNull final Context context) {
        if (sSoundManager == null) {
            sSoundManager = new SoundManager(context);
        }
        return sSoundManager;
    }

    public int load(final int sound_id) {
        return sSoundPool.load(sContext, sound_id, 1);
    }

    public void play(final int sound_id) {
        sSoundPool.play(sound_id, leftVolume, rightVolume, 1, 0, rate);
    }

    public void setVolume(final float vol) {
        masterVolume = vol;

        if (balance < 1.0f) {
            leftVolume = masterVolume;
            rightVolume = masterVolume * balance;
        } else {
            rightVolume = masterVolume;
            leftVolume = masterVolume * (2.0f - balance);
        }
    }

    public void setSpeed(final float speed) {
        rate = speed;

        if (rate < 0.01f)
            rate = 0.01f;

        if (rate > 2.0f)
            rate = 2.0f;
    }

    public void setBalance(final float balVal) {
        balance = balVal;

        setVolume(masterVolume);
    }


    public void unloadAll() {
        sSoundPool.release();
    }
}
