package org.cnodejs.android.oauthlogin;

import android.content.Context;
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
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

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
    private Button btnReopenLogin;

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

    private void init(final Context context) {
        LayoutInflater.from(context).inflate(R.layout.widget_cnode_oauth_login_view, this, true);
        webView = (WebView) findViewById(R.id.web_view);
        layoutLoading = (ViewGroup) findViewById(R.id.layout_loading);
        layoutFinish = (ViewGroup) findViewById(R.id.layout_finish);
        layoutError = (ViewGroup) findViewById(R.id.layout_error);
        btnReopenLogin = (Button) findViewById(R.id.btn_reopen_login);

        clearCookie();
        webView.setWebViewClient(new WebViewClient() {

            @SuppressWarnings("StatementWithEmptyBody")
            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, String url) {
                if (TextUtils.isEmpty(url)) {
                    showErrorLayout();
                } else if (url.equals("https://github.com/")) { // Desktop version
                    // nothing to do
                } else if (url.equals("https://github.com/password_reset")) {
                    Toast.makeText(context, "请在PC浏览器端重置密码", Toast.LENGTH_SHORT).show();
                } else {
                    showLoadingLayout();
                    webView.loadUrl(url);
                }
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (TextUtils.isEmpty(url)) {
                    showErrorLayout();
                } else if (url.equals("https://github.com/login?client_id=0625d398dd9166a196e9&return_to=%2Flogin%2Foauth%2Fauthorize%3Fclient_id%3D0625d398dd9166a196e9%26redirect_uri%3Dhttps%253A%252F%252Fcnodejs.org%252Fauth%252Fgithub%252Fcallback%26response_type%3Dcode")) {
                    showWebView();
                } else if (url.equals("https://cnodejs.org/")) {
                    startGetAccessTokenAsyncTask(getCookie(url));
                }
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                showErrorLayout();
            }

        });

        btnReopenLogin.setOnClickListener(new OnClickListener() {

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

    private void showWebView() {
        layoutLoading.setVisibility(INVISIBLE);
        layoutFinish.setVisibility(INVISIBLE);
        layoutError.setVisibility(INVISIBLE);
    }

    private void showLoadingLayout() {
        layoutLoading.setVisibility(VISIBLE);
        layoutFinish.setVisibility(INVISIBLE);
        layoutError.setVisibility(INVISIBLE);
    }

    private void showFinishLayout() {
        layoutLoading.setVisibility(INVISIBLE);
        layoutFinish.setVisibility(VISIBLE);
        layoutError.setVisibility(INVISIBLE);
    }

    private void showErrorLayout() {
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
