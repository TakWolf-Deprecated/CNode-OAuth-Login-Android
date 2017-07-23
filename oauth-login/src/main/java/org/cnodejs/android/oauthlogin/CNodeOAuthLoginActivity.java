package org.cnodejs.android.oauthlogin;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

public final class CNodeOAuthLoginActivity extends AppCompatActivity implements OAuthLoginCallback {

    public static final String EXTRA_ACCESS_TOKEN = "accessToken";

    private CNodeOAuthLoginView loginView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginView = new CNodeOAuthLoginView(this);
        loginView.setOAuthLoginCallback(this);
        loginView.openOAuth();
        setContentView(loginView);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        loginView.destroy();
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

    @Override
    public void onLoginSuccess(String accessToken) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_ACCESS_TOKEN, accessToken);
        setResult(RESULT_OK, intent);
        finish();
    }

}
