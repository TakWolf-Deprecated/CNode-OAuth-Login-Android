package org.cnodejs.android.oauth;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public final class CNodeOAuthWebView extends WebView {

    private static final Handler handler = new Handler(Looper.getMainLooper());

    private OnOAuthSuccessListener listener;

    public CNodeOAuthWebView(Context context) {
        super(context);
        init(context);
    }

    public CNodeOAuthWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CNodeOAuthWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @SuppressWarnings("deprecation")
    private void init(Context context) {
        // 清理 cookie
        CookieSyncManager cookieSyncManager = CookieSyncManager.createInstance(context);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.removeSessionCookie();
        cookieManager.removeAllCookie();
        cookieSyncManager.sync();
        // 设置 client
        setWebViewClient(new WebViewClient() {

            @SuppressWarnings("deprecation")
            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, String url) {
                if (TextUtils.isEmpty(url)) {
                    setErrorPage();
                } else {
                    if (url.equals("https://cnodejs.org/")) {
                        setLoadingPage();
                        CookieManager cookieManager = CookieManager.getInstance();
                        String cookie = cookieManager.getCookie(url);
                        startGetAccessTokenAsyncTask(cookie);
                    } else {
                        webView.loadUrl(url);
                    }
                }
                return true;
            }

        });
    }

    public void setOnOAuthSuccessListener(OnOAuthSuccessListener listener) {
        this.listener = listener;
    }

    public void openOAuth() {
        loadUrl("https://cnodejs.org/auth/github");
    }

    private void setLoadingPage() {

    }

    private void setErrorPage() {

    }

    private void startGetAccessTokenAsyncTask(final String cookie) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Document document = Jsoup.connect("https://cnodejs.org/setting").header("Cookie", cookie).get();
                    final String accessToken = document.getElementById("content").getElementsByClass("panel").get(2).child(1).child(0).text().replace("字符串：", "").replace(" ", "").trim();
                    handler.post(new Runnable() {

                        @Override
                        public void run() {
                            if (listener != null) {
                                listener.onOAuthSuccess(accessToken);
                            }
                        }

                    });
                } catch (IOException e) {
                    handler.post(new Runnable() {

                        @Override
                        public void run() {
                            setErrorPage();
                        }

                    });
                }
            }

        }).start();
    }

    public interface OnOAuthSuccessListener {

        void onOAuthSuccess(String accessToken);

    }

}
