package org.cnodejs.android.oauth;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

public class CNodeOAuthActivity extends AppCompatActivity implements CNodeOAuthWebView.OnOAuthSuccessListener {

    public static final String EXTRA_ACCESS_TOKEN = "accessToken";

    private CNodeOAuthWebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        webView = new CNodeOAuthWebView(this);
        webView.setOnOAuthSuccessListener(this);
        webView.openOAuth();
        setContentView(webView);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        webView.destroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            webView.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            webView.onPause();
        }
    }

    @Override
    public void onOAuthSuccess(String accessToken) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_ACCESS_TOKEN, accessToken);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navigationFinish();
    }

    @Override
    public void onBackPressed() {
        navigationFinish();
    }

    private boolean navigationFinish() {
        setResult(RESULT_CANCELED);
        finish();
        return true;
    }

}
