package org.cnodejs.android.oauthdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import org.cnodejs.android.oauth.CNodeOAuthActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CNODE_OAUTH = 1;

    @BindView(R.id.tv_token)
    protected TextView tvToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_oauth)
    protected void onBtnOAuthClick() {
        startActivityForResult(new Intent(this, CNodeOAuthActivity.class), REQUEST_CNODE_OAUTH);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CNODE_OAUTH && resultCode == RESULT_OK && data != null) {
            String accessToken = data.getStringExtra(CNodeOAuthActivity.EXTRA_ACCESS_TOKEN);
            tvToken.setText(accessToken);
        }
    }

}
