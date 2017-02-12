package sakurafish.com.parrot.uninstaller.web;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Picture;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.GeolocationPermissions.Callback;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebView.PictureListener;
import android.webkit.WebViewClient;

import java.net.URISyntaxException;

import sakurafish.com.parrot.uninstaller.R;
import sakurafish.com.parrot.uninstaller.fragment.BaseFragment;
import sakurafish.com.parrot.uninstaller.ui.IncoProgressDialog;
import sakurafish.com.parrot.uninstaller.utils.Utils;

/**
 * WebViewの基本画面
 */
@SuppressWarnings("deprecation")
@SuppressLint("SetJavaScriptEnabled")
public class BaseWebFragment extends BaseFragment {

    public static final String EXTRA_URL = "url";

    protected WebView mWebView;
    protected IncoProgressDialog mLoading;

    final protected WebViewClient mWebViewClient = new WebViewClient() {
        @Override
        public void onPageStarted(final WebView view, final String url, final Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            if (getActivity() != null) {
                dismissLoading();
                mLoading = new IncoProgressDialog(getActivity());
                mLoading.setCancelable(true);
                mLoading.show();
            }
        }

        @Override
        public void onPageFinished(final WebView view, final String url) {
            dismissLoading();
        }

        @Override
        public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
            Utils.logDebug("url:" + url);

            if (url.startsWith("tel:")) {
                final Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
                startActivity(intent);
                return true;
            }
            if (url.startsWith("mailto:")) {
                try {
                    final Intent intent = Intent.parseUri(url, 0);
                    startActivity(intent);
                    return true;
                } catch (URISyntaxException e) {
                    // Intent schemeが不正.
                } catch (ActivityNotFoundException e) {
                    // 対象アプリがインストールされていない.
                }
                return true;
            }
            if (url.startsWith("intent:")) {
                try {
                    final Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                    startActivity(intent);
                    return true;
                } catch (URISyntaxException e) {
                    // Intent schemeが不正.
                } catch (ActivityNotFoundException e) {
                    // 対象アプリがインストールされていない.
                }
                return true;
            }
            if (url.startsWith("http://line.naver.jp/")) {
                // LINEで送るボタン.
                try {
                    final Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                    startActivity(intent);
                    return true;
                } catch (Exception e) {
                    goBrowser("https://play.google.com/store/apps/details?id=jp.naver.line.android&hl=ja");
                    return true;
                }
            }
            goBrowser(url);
            return false;
        }
    };

    final protected WebChromeClient mWebChromeClient = new WebChromeClient() {
        @Override
        public void onProgressChanged(final WebView view, final int newProgress) {
            super.onProgressChanged(view, newProgress);
        }

        @Override
        public boolean onJsAlert(final WebView view, final String url, final String message,
                                 final JsResult result) {
            try {
                Utils.logError("onJsAlert 取得したメッセージは" + message);
                return true;
            } finally {
                result.confirm();
            }
        }

        @Override
        public boolean onConsoleMessage(final ConsoleMessage cm) {
            return super.onConsoleMessage(cm);
        }

        @Override
        public void onGeolocationPermissionsShowPrompt(final String origin, final Callback callback) {
            callback.invoke(origin, true, false);
        }
    };

    protected String mUrl;
    protected Bundle mBundle;

    final PictureListener mPictureLisener = new PictureListener() {

        @Override
        @Deprecated
        public void onNewPicture(final WebView arg0, final Picture arg1) {
            dismissLoading();
        }
    };

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_web, null);
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (!Utils.isConnected()) {
            showNetworkErrorDialog();
        }
        initLayout(savedInstanceState);
    }

    protected void initLayout(final Bundle savedInstanceState) {

        mUrl = getArguments().getString(EXTRA_URL);

        // Viewのセットアップ
        mWebView = (WebView) getView().findViewById(R.id.webView);
        final WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setBuiltInZoomControls(true);
        settings.setSupportZoom(true);
        settings.setAppCacheEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        settings.setGeolocationEnabled(true);
        mWebView.setWebChromeClient(mWebChromeClient);
        mWebView.setWebViewClient(mWebViewClient);
        mWebView.setPictureListener(mPictureLisener);
        mWebView.setVerticalScrollbarOverlay(true);

        Utils.logDebug("url:" + mUrl);
        if (mBundle != null) {
            mWebView.restoreState(mBundle);
        } else if (savedInstanceState != null) {
            mWebView.restoreState(savedInstanceState);
        } else {
            mWebView.loadUrl(mUrl);
        }
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mWebView != null) {
            mWebView.saveState(outState);
        }
    }

    public void saveWebView() {
        mBundle = new Bundle();
        if (mWebView != null) {
            mWebView.saveState(mBundle);
        }
    }

    public boolean onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        } else {
            return false;
        }
    }

    public String getInitialUrl() {
        return mUrl;
    }

    private void goBrowser(final String url) {
        mWebView.stopLoading();
        try {
            final Uri uri = Uri.parse(url);
            final Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        } catch (Exception e) {
            Log.d("JSLogs", "Webview Error:" + e.getMessage());
        }
    }

    private void dismissLoading() {
        if (mLoading != null && mLoading.isShowing()) {
            mLoading.dismiss();
            mLoading = null;
        }
    }

    private void finalizeLayout() {
        dismissLoading();
        if (mWebView != null) {
            mWebView.stopLoading();
            mWebView = null;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        finalizeLayout();
        mUrl = null;
    }
}
