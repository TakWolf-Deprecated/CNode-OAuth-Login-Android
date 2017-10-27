# CNode OAuth Login - Android #

[![Build Status](https://travis-ci.org/TakWolf/CNode-OAuth-Login-Android.svg?branch=master)](https://travis-ci.org/TakWolf/CNode-OAuth-Login-Android)
[![Bintray](https://api.bintray.com/packages/takwolf/maven/CNode-OAuth-Login-Android/images/download.svg)](https://bintray.com/takwolf/maven/CNode-OAuth-Login-Android/_latestVersion)
[![Platform](https://img.shields.io/badge/platform-Android-green.svg)](https://www.android.com)
[![API](https://img.shields.io/badge/API-14%2B-brightgreen.svg)](https://android-arsenal.com/api?level=14)
[![License](https://img.shields.io/github/license/TakWolf/CNode-OAuth-Login-Android.svg)](http://www.apache.org/licenses/LICENSE-2.0)

直接用 GitHub 账户登录 [CNode社区](https://cnodejs.org)，Android 端用组件。

iOS端对应的组件：暂时还没有，如果你开发了一个，请[告诉我](mailto:takwolf@foxmail.com)。

## 问题 ##

该项目来源于下面这个讨论：

[https://github.com/TakWolf/CNode-Material-Design/issues/37](https://github.com/TakWolf/CNode-Material-Design/issues/37)

## 原理 ##

CNode 网站端仅能通过 GitHub 账户登录，使用 OAuth 2.0 授权。

有如下思路：

初始化一个 WebView，载入 CNode 登录地址：https://cnodejs.org/auth/github

这句会重定向到：https://github.com/login/oauth/authorize?response_type=code&redirect_uri=http://cnodejs.org/auth/github/callback&client_id=0625d398dd9166a196e9

GitHub 登录成功后，携带 code 回调： https://cnodejs.org/auth/github/callback?code=xxxxxxx

CNode 验证成功后，会重定向到首页：https://cnodejs.org/

这时截断这个重定向，取出 CNode 的 session_cookie，用这个 session_cookie 去抓取设置页面：https://cnodejs.org/setting

解析 HTML 文档，取出 AccessToken。

关于 OAuth 2.0 的更详细资料，请参考 [https://oauth.net/2/](https://oauth.net/2/)

## 用法 ##

添加依赖：

``` gradle
implementation 'org.cnodejs.android:oauth-login:0.1.0'
```

该依赖会自动注册 `CNodeOAuthLoginActivity` 和网络权限。

在你的 `Activity` 中，启动授权页面：

``` java
private static final int REQUEST_CNODE_OAUTH_LOGIN = 1;

......

startActivityForResult(new Intent(this, CNodeOAuthLoginActivity.class), REQUEST_CNODE_OAUTH_LOGIN);
```

重写 `onActivityResult` 来监听返回值：

``` java
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == REQUEST_CNODE_OAUTH_LOGIN && resultCode == RESULT_OK && data != null) {
        String accessToken = data.getStringExtra(CNodeOAuthLoginActivity.EXTRA_ACCESS_TOKEN); // 这里就是 API 鉴权用的 AccessToken
    }
}
```

## 哪些 CNode 客户端在使用这个库？ ##

请[告诉我](mailto:takwolf@foxmail.com)，如果你希望它出现在下面的列表中。

- [CNode-Material-Design](https://github.com/TakWolf/CNode-Material-Design)

## Dependencies ##

- [jsoup](https://jsoup.org)

- [materialish-progress](https://github.com/pnikosis/materialish-progress)

## Author ##

TakWolf

[takwolf@foxmail.com](mailto:takwolf@foxmail.com)

[http://takwolf.com](http://takwolf.com)

## License ##

```
Copyright 2017 TakWolf

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
