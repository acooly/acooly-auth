<!-- title: Google身份验证器 -->
<!-- name: acooly-auth-google-authenticator -->
<!-- type: auth -->
<!-- author: zhangpu -->
<!-- date: 2021-08-13 -->

# 1. 简介

Google Authenticator 的服务端集成，配合Google身份验证器App实现Google通用二次验证码认证。基础逻辑为：TOTP(Time-based One-time Password)

具体逻辑请参考：

* [Java集成Google Authenticator](https://ghthou.github.io/2018/01/13/Java-%E9%9B%86%E6%88%90-Google-Authenticator/)
* [Google账户两步验证的工作原理](https://blog.seetee.me/post/2011/google-two-step-verification/)

# 2. 特性

* 生成秘钥（20字符的bytes的Base32编码：32长度的字符串）
* 生成Google身份验证器App可识别的二维码（包含信息：应用，用户标志，秘钥）
* 提供验证工具方法，支持偏移量（默认为1，表示当前时间段前后各一个30秒时间段兼容验证）
* 提供非online环境的controller的demo

# 3. 集成

```xml

<dependency>
    <groupId>cn.acooly</groupId>
    <artifactId>acooly-auth-google-authenticator</artifactId>
    <version>5.0.0</version>
</dependency>
```

> 如果是Acooly标准项目，`version`可以不配给制，由父POM中统一管理

# 4. 使用

## 4.1 核心工具类

组件提供静态核心工具类：`cn.acooly.auth.google.authenticator.GoogleAuthenticators` ,提供：

* 生成秘钥：createSecretKey
* 生成二维码数据：createKeyUri
* 验证动态密码：verification

集成项目可根据实际需要调用工具类实现二次验证功能的集成。

## 4.2 集成开发逻辑

### 4.2.1 设置服务名称

确定一个你服务和应用的名称，用于客户在Google身份验证器中识别和区分这个验证器是做什么的。例如：GitHub，Acooly，Wecoinbank等。

### 4.2.2 管理用户秘钥

* 为服务下的用户创建对应的认证秘钥（`GoogleAuthenticators.createSecretKey()`）
* 建立用户唯一标志与秘钥的绑定关系。
* 如有需要，为用户建立开通二次认证的标志，只有启用了标志的，才生成秘钥，建立绑定关系。

### 4.2.3 生成用户秘钥二维码

使用服务名称`ISSUER`，用户客户识别唯一标志`account`(例如：手机号码，邮箱等)和用户秘钥`secretKey`生成用户专用的Google身份验证器秘钥二维码。

```
// 生成 Google Authenticator Key Uri
GoogleAuthenticators.createKeyUri(secretKey, account, ISSUER);
// 把 keyUri 生成二维码图片
try (ServletOutputStream outputStream = response.getOutputStream()) {
    Barcodes.encodeQRcode(keyUri, "UTF-8", 300, outputStream, false);
}
```

> 生成二维码图片后，用户通过Google身份验证器扫码添加到验证器中，每30秒会更新一次密码。

### 4.2.4 验证密码

* 用户在服务的应用界面输入Google身份验证器生成的动态密码：totpCode
* 服务使用当前用户标志，并转换获得唯一标志：account
* 从数据库（持久化）中通过`account`查询出对应的秘钥：secretKey
* 通过`GoogleAuthenticators.verification`传入`secretKey`和`totpCode`验证是否通过

# 5 Demo示例

组件提供了示例的代码`cn.acooly.auth.google.authenticator.GoogleAuthenticatorDemoController`.

注意：该示例只能在非online环境才会启用。

# 6 changelog

## 5.0.0

20210813

* 生成秘钥（20字符的bytes的Base32编码：32长度的字符串）
* 生成Google身份验证器App可识别的二维码（包含信息：应用，用户标志，秘钥）
* 提供验证工具方法，支持偏移量（默认为1，表示当前时间段前后各一个30秒时间段兼容验证）
* 提供非online环境的controller的demo
