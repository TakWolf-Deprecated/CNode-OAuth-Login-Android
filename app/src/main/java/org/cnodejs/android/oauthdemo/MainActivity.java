package org.cnodejs.android.oauthdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import org.cnodejs.android.oauthlogin.CNodeOAuthLoginActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CNODE_OAUTH_LOGIN = 1;

    @BindView(R.id.tv_token)
    TextView tvToken;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_oauth)
    void onBtnOAuthClick() {
        startActivityForResult(new Intent(this, CNodeOAuthLoginActivity.class), REQUEST_CNODE_OAUTH_LOGIN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CNODE_OAUTH_LOGIN && resultCode == RESULT_OK && data != null) {
            String accessToken = data.getStringExtra(CNodeOAuthLoginActivity.EXTRA_ACCESS_TOKEN);
            tvToken.setText(accessToken);
        }
    }

}
