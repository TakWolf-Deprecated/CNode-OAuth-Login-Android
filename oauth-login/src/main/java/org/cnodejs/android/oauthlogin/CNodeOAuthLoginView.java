package org.cnodejs.android.oauthlogin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.StyleRes;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class CNodeOAuthLoginView extends FrameLayout {

    private static final Handler handler = new Handler(Looper.getMainLooper());
    private static final ExecutorService executorService = Executors.newCachedThreadPool();

    private WebView webView;
    private ViewGroup layoutLoading;
    private ViewGroup layoutFinish;
    private ViewGroup layoutError;

    private OAuthLoginCallback loginCallback;

    public CNodeOAuthLoginView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public CNodeOAuthLoginView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CNodeOAuthLoginView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CNodeOAuthLoginView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    @SuppressLint("ShowToast")
    private void init(final Context context) {
        LayoutInflater.from(context).inflate(R.layout.widget_cnode_oauth_login_view, this, true);
        webView = (WebView) findViewById(R.id.web_view);
        layoutLoading = (ViewGroup) findViewById(R.id.layout_loading);
        layoutFinish = (ViewGroup) findViewById(R.id.layout_finish);
        layoutError = (ViewGroup) findViewById(R.id.layout_error);

        clearCookie();
        webView.setWebViewClient(new WebViewClient() {

            @SuppressWarnings("StatementWithEmptyBody")
            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, String url) {
                if (TextUtils.isEmpty(url)) {
                    showErrorLayout();
                } else {
                    String currentUrl = webView.getUrl();
                    if (!TextUtils.isEmpty(currentUrl) && currentUrl.startsWith("https://github.com/login/oauth/authorize?")) {
                        if (url.startsWith("https://github.com/login?") || url.startsWith("https://github.com/login/oauth/authorize?") || url.startsWith("https://cnodejs.org/auth/github/callback?")) {
                            showLoadingLayout();
                            webView.loadUrl(url);
                        } else {
                            openInBrowser(url);
                        }
                    } else {
                        switch (url) {
                            case "https://github.com/":
                            case "https://github.com/password_reset":
                                openInBrowser(url);
                                break;
                            default:
                                showLoadingLayout();
                                webView.loadUrl(url);
                                break;
                        }
                    }
                }
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (TextUtils.isEmpty(url)) {
                    showErrorLayout();
                } else if (url.equals("https://cnodejs.org/")) {
                    startGetAccessTokenAsyncTask(getCookie(url));
                } else if (url.startsWith("https://github.com/login?") || url.startsWith("https://github.com/login/oauth/authorize?")) {
                    showWebView();
                }
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                showErrorLayout();
            }

        });

        findViewById(R.id.btn_reopen_login).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                openOAuth();
            }

        });
    }

    public void destroy() {
        webView.destroy();
    }

    public void setOAuthLoginCallback(OAuthLoginCallback loginCallback) {
        this.loginCallback = loginCallback;
    }

    public void openOAuth() {
        showLoadingLayout();
        webView.loadUrl("https://cnodejs.org/auth/github");
    }

    @SuppressWarnings("deprecation")
    private void clearCookie() {
        CookieSyncManager cookieSyncManager = CookieSyncManager.createInstance(getContext());
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.removeSessionCookie();
        cookieManager.removeAllCookie();
        cookieSyncManager.sync();
    }

    private String getCookie(String url) {
        CookieManager cookieManager = CookieManager.getInstance();
        return cookieManager.getCookie(url);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    private void openInBrowser(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            getContext().startActivity(intent);
        } else {
            // do noting
        }
    }

    private void showWebView() {
        webView.setVisibility(VISIBLE);
        layoutLoading.setVisibility(INVISIBLE);
        layoutFinish.setVisibility(INVISIBLE);
        layoutError.setVisibility(INVISIBLE);
    }

    private void showLoadingLayout() {
        webView.setVisibility(INVISIBLE);
        layoutLoading.setVisibility(VISIBLE);
        layoutFinish.setVisibility(INVISIBLE);
        layoutError.setVisibility(INVISIBLE);
    }

    private void showFinishLayout() {
        webView.setVisibility(INVISIBLE);
        layoutLoading.setVisibility(INVISIBLE);
        layoutFinish.setVisibility(VISIBLE);
        layoutError.setVisibility(INVISIBLE);
    }

    private void showErrorLayout() {
        webView.setVisibility(INVISIBLE);
        layoutLoading.setVisibility(INVISIBLE);
        layoutFinish.setVisibility(INVISIBLE);
        layoutError.setVisibility(VISIBLE);
    }

    private void startGetAccessTokenAsyncTask(final String cookie) {
        showLoadingLayout();
        executorService.execute(new Runnable() {

            @Override
            public void run() {
                try {
                    Document document = Jsoup.connect("https://cnodejs.org/setting").header("Cookie", cookie).get();
                    final String accessToken = document.getElementById("content").getElementsByClass("panel").get(2).child(1).child(0).text().replace("字符串：", "").replace(" ", "").trim();
                    handler.post(new Runnable() {

                        @Override
                        public void run() {
                            showFinishLayout();
                            if (loginCallback != null) {
                                loginCallback.onLoginSuccess(accessToken);
                            }
                        }

                    });
                } catch (IOException e) {
                    handler.post(new Runnable() {

                        @Override
                        public void run() {
                            showErrorLayout();
                        }

                    });
                }
            }

        });
    }

}
