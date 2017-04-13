# CNode OAuth - Android #

[CNode社区](https://cnodejs.org) Open Auth 登录的 Android 用 SDK。

## 原理 ##

CNode仅能通过GitHub账号登录。

通过WebView加载GitHub的授权页面，用户完成授权后，等待页面重定向到CNode首页。

这时，取出当前Cookie，用该Cookie去抓取用户设置页面，解析出AccessToken。

这个AccessToken可以被API用于用户鉴权使用。

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
