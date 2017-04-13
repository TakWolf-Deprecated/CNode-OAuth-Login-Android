# CNode OAuth - Android #

实现直接用 GitHub 账户登录 [CNode社区](https://cnodejs.org)，Android 端用组件。

iOS端对应的组件：暂时还没有，如果你开发了一个，请[告诉我](mailto:takwolf@foxmail.com)。

## 原理 ##

通过 WebView 加载 GitHub 的授权页面，用户完成授权后，等待页面重定向到 CNode 首页。

这时，取出当前 Cookie，用该 Cookie 去抓取用户设置页面，解析出 AccessToken。

这个 AccessToken 可以被API用于用户鉴权使用。

## 用法 ##

### 快速集成 ###

添加依赖：

```
compile 'org.cnodejs.android:oauth:0.0.1'
```

该依赖会自动注册 `CNodeOAuthActivity` 和网络权限。

在你的 `Activity` 中，启动授权页面：

```
private static final int REQUEST_CNODE_OAUTH = 1;

......

startActivityForResult(new Intent(this, CNodeOAuthActivity.class), REQUEST_CNODE_OAUTH);
```

重写 `onActivityResult` 来监听返回值：

```
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == REQUEST_CNODE_OAUTH && resultCode == RESULT_OK && data != null) {
        String accessToken = data.getStringExtra(CNodeOAuthActivity.EXTRA_ACCESS_TOKEN); // 这里就是 API 鉴权用的 AccessToken
    }
}
```

### 自定义样式 ###

如果你希望自定义样式，包括配色，ActionBar样式等，你可以直接使用 `CNodeOAuthWebView`，示例如下：

```
CNodeOAuthWebView webView = findViewById(R.id.cnode_oauth_web_view);
webView.setOnOAuthSuccessListener(new CNodeOAuthWebView.OnOAuthSuccessListener() {

    @Override
    public void onOAuthSuccess(String accessToken) {
        // Do something about accessToken.
    }

});
webView.openOAuth();
```

## 哪些 CNode 客户端在使用这个库？ ##

请[告诉我](mailto:takwolf@foxmail.com)你的应用在使用，如果你希望它出现在下面的列表中。

- [CNode-Material-Design](https://github.com/TakWolf/CNode-Material-Design)

## Author ##

TakWolf

[takwolf@foxmail.com](mailto:takwolf@foxmail.com)

[http://takwolf.com](http://takwolf.com)

## License ##

```
Copyright 2015 TakWolf

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
