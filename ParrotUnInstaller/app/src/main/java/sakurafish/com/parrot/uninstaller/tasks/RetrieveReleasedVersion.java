package sakurafish.com.parrot.uninstaller.tasks;

import android.content.Context;
import android.os.AsyncTask;

import org.jsoup.Jsoup;

import java.io.IOException;

import sakurafish.com.parrot.uninstaller.UninstallerApplication;

/**
 * Google Playのサイトよりリリースされているアプリのバージョンを取得する
 */
public class RetrieveReleasedVersion extends AsyncTask<Void, Void, String> {
    private Callback mCallback;
    private Context mContext;
    private String mPackageName;

    public RetrieveReleasedVersion(final Context context, final Callback callback) {
        mCallback = callback;
        mContext = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mPackageName = UninstallerApplication.getContext().getPackageName();
    }

    @Override
    protected String doInBackground(final Void... params) {
        String new_version = null;
        try {
            new_version = Jsoup.connect("https://play.google.com/store/apps/details?id=" + mPackageName)
                    .timeout(30000)
                    .userAgent("Mozilla/5.0 (Linux; U; Android 4.0.1; ja-jp; Galaxy Nexus Build/ITL41D) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30")
                    .referrer("http://www.google.com")
                    .get()
                    .select("div[itemprop=softwareVersion]")
                    .first()
                    .ownText();
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
