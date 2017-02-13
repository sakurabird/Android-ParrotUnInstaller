package sakurafish.com.parrot.uninstaller.tasks;

import android.content.Context;
import android.os.AsyncTask;

import org.jsoup.Jsoup;

import java.io.IOException;

import sakurafish.com.parrot.uninstaller.UninstallerApplication;
import sakurafish.com.parrot.uninstaller.utils.Utils;

/**
 * Google Playのサイトよりリリースされているアプリのバージョンを取得する
 */
public class RetrieveReleasedVersion extends AsyncTask<Void, Void, String> {
    final private Callback mCallback;
    private String mPackageName;

    public RetrieveReleasedVersion(final Context context, final Callback callback) {
        mCallback = callback;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mPackageName = UninstallerApplication.getContext().getPackageName();
    }

    @Override
    protected String doInBackground(final Void... params) {
        String new_version;
        mPackageName = "sakurafish.com.parrot.uninstaller";
        try {
            new_version = Jsoup.connect("https://play.google.com/store/apps/details?id=" + mPackageName)
                    .timeout(30000)
                    .userAgent("Mozilla") //http://stackoverflow.com/a/36780250/2845202
                    .referrer("http://www.google.com")
                    .get()
                    .select("div[itemprop=softwareVersion]")
                    .first()
                    .ownText();
            Utils.logDebug("new_version:" + new_version);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return new_version;
    }

    @Override
    protected void onPostExecute(final String param) {
        super.onPostExecute(param);
        mCallback.onFinish(param);
    }

    @Override
    protected void onProgressUpdate(final Void... values) {
        super.onProgressUpdate(values);
    }

    public interface Callback {
        void onFinish(String version);
    }
}
