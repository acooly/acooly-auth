<!-- title: 微信授权认证（公众号，小程序，网页授权登陆） -->
<!-- name: acooly-auth-wechat-authenticator -->
<!-- type: app -->
<!-- author: cuifuq -->
<!-- date: 2021-08-13 -->
<!-- title: 微信接入组件  -->

## 1. 组件介绍

acooly-auth-wechat-authenticator 以acooly框架为基础, 集成微信公众号认证体系；方便研发团队依赖此组件快速对接微信公众号，微信小程序；


[微信公众平台开发](https://mp.weixin.qq.com);

[微信公众平台开发指南](https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1421135319)

[微信网页授权](https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1421140842)


## [BOSS]微信授权信息 

##### （非默认配置）/manage/wechat/auth/index.html


### 1.1组件优势

* 快速完成微信公众号，微信小程序的对接
* 简化微信公众号复杂的对接流程
* 支持接口请求access_token 缓存
* 支持校验用户是否关注微信公众号判断
* 新增网站微信网页授权登录



## 2. 使用说明

maven坐标：

     <dependency>
        <groupId>cn.acooly</groupId>
        <artifactId>acooly-auth-wechat-authenticator</artifactId>
        <version>${acooly-latest-version}</version>
      </dependency>

`${acooly-latest-version}`为框架最新版本或者购买的版本。


### 2.1 项目配置说明（核心点）

#####  配置文件开头：acooly.auth.wechat

- 微信公众号：acooly.auth.wechat.webClient
- 微信小程序：acooly.auth.wechat.miniClient
- 网页授权登录：acooly.auth.wechat.webLoginClient


### 2.2 微信公众号对接操作说明

* step 1: 登录微信公众号平台 [网址](https://mp.weixin.qq.com/?token=&lang=zh_CN)
* step 2： 左边菜单，首页-->设置与开发-->基本配置-->公众号开发信息
* step 3: 启用-开发者密码(AppSecret) 并保存对应的AppSecret
* step 4: 设置IP白名单, 配置服务器出口IP 
* step 5: 设置-服务器配置，URL：服务器回调地址（/wechat/webApi/callback.html）；Token；EncodingAESKey（随机生成）；消息加解密方式 [参考](https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1421135319)
* step 6: 左边菜单，首页-->设置与开发-->公众号设置-->功能设置-->[业务域名] 和 [网页授权域名]（1.将“微信授权文件”存在工程static目录；2.重启工程）
* step 7: 左边菜单，首页-->内容与互动-->内容工具-->自定义菜单  组件默认访问地址 （ /wechat/webApi/index.html）
* step 8: 左边菜单，首页-->设置与开发-->公众号设置-->帐号详情-->二维码，关注公众号，即可正常访问授权



### 2.3 微信小程序对接操作说明

* step 1: 登录凭证校验 cn.acooly.auth.wechat.authenticator.oauth.mini.WechatMiniClientService#loginAuthVerify

#### 2.3.1 微信小程序对接操作说明

* step 1: 登录微信公众号平台 [网址](https://mp.weixin.qq.com/?token=&lang=zh_CN)
* step 2： 左边菜单，首页-->开发-->开发管理-->开发设置 ：开启 [AppSecret(小程序密钥)],并保存对应的AppSecret
* step 3: 左边菜单，首页-->开发-->开发管理-->开发设置 ：服务器域名 --->设置【配置服务器信息】
* step 4: 左边菜单，首页-->开发-->开发管理-->开发设置 :  开启:消息推送(可选)；扫普通链接二维码打开小程序（必选）；



### 2.4 网站应用微信登录说明

* step 1: 登录微信开放平台[https://open.weixin.qq.com]；申请创建网站应用；
* step 2: 申请成功后，创建并保存 appid,appSecret；
* step 3: 开发信息-->授权回调域  -修改为系统域名

#### 2.4.1 网站应用微信登录逻辑解析
* step 1: 确保 用户名+密码 登录成功；
* step 2: 让用户绑定 授权登录二维码，绑定成功后，跳转指定页面（用户的信息与微信的openId/unionid绑定）
* step 3: 用户扫码登录时，使用微信的openId/unionid 查询已绑定的用户；确定用户权限并登录




### 2.5 联调错误排除

	错误描述：scope的权限错误代码10005
	方案
		1.使用的是订阅号，订阅号没有权限使用网页授权
		2.使用的是未认证的服务号
		3.网页授权回调域名填写错误
		4.scope参数顺序不对
		
	错误描述:  userinfo 接口无法获取 unionid
	方案：公众号需要绑定到微信开放平台 [https://open.weixin.qq.com]


## 3.版本说明

#### 2022-07-18

###### 文件存储设置：
文件存储设置：
1. 新增配置项 acooly.auth.wechat.obsFileBucketName 文件存储-桶名称（如：微信小程序码 文件存储）
   设置规则：
* 1.1: 此配置开关 依赖：acooly.ofile.storageType=OBS 
* 1.2: obs默认名称；参考：acooly.obs.aliyun.bucketName，（若不设置 wechat.obsFileBucketName） 
* 1.3: 微信文件的BucketName 仅支持"公共读" 【阿里云可设置】


2. 新增批量获取小程序码；单次500个(不同的小程序可以同时获取)【上一版本已经支持多个小程序配置】

3. 支持不同环境下小程序小程序码【正式版为 "release"，体验版为 "trial"，开发版为 "develop"。默认是正式版。】
   生产环境仅支持 正式版为 "release"

###### 性能测试：
MacbookPro 14[2021];  M1 pro（10核 16GB）/控制线程最大数量100个

| 数量  | 线程数 |本地存储 | 云存储 |
|-----| -----|------- | ------- |
| 40  | 40  | 1.756 s      | 2.341 s      |
| 50  | 50  |1.112 s     |  2.474 s      |
| 100 | 20  | 3.761 s      |  5.017 s       |
| 200 | 40  | 3.904 s     |  5.481 s     |
| 300 | 60  |3.93 s    |  7.336 s       |
| 500 | 100 |5.372 s      |  10.126 s       |


#### 2022-05-23

######  应用场景说明：
* 1.微信针对小程序的大小有严格限制【整个小程序所有分包大小不超过 12M 单个分包/主包大小不能超过 2M】
* 2.由于公司产品线的不同，针对不同的产品线研发不同的小程序

###### 更新内容
* 1.微信小程序：支持多个小程序配置，acooly.auth.wechat.miniManyClient[小程序appid]=小程序secret
* 2.公众号:支持由商户(使用者)动态配置用户授权方式


#### 2022-03-08

* 1.微信小程序-配置说明
* 2.公众号,小程序unionid同步方案； 提前公众号（取消关注，未关注），小程序内嵌公众号首页，通过服务端绑定公众号的unionid


#### 2021-12-29

* 1.新增 jsapi_ticket 获取方案；jsapi_ticket是公众号用于调用微信JS接口的临时票据，有效期7200秒
* 2.新增boss管理界面，自行配置：/manage/wechat/auth/index.html; 支持人工刷新缓存
* 3.优化公众号接口access_token，小程序access_token, jsapi_ticket，支持分布式锁机制，有效处理高并发

#### 2021-09-24

* 1.微信网页授权登录

#### 2020-09-16

* 1.微信组件升级，同时支持公众号，小程序对接
* 2.微信组件新增对小程序对接支持功能(微信用户信息（openid），无限获取小程序码等)
* 3.新增依赖acooly-component-ofile组件，满足 无限获取小程序码并本地化存储(小程序可以设置多个入口)

#### 2019-03-31

* 1.微信组件升级，同时支持公众号，小程序对接
* 2.微信组件新增对小程序对接支持功能(微信用户信息（openid），无限获取小程序码等)
* 3.新增依赖acooly-component-ofile组件，满足 无限获取小程序码并本地化存储(小程序可以设置多个入口)



#### 2019-02-27

* 1.微信公众号快速对接授权
* 2.支持接口请求access_token 缓存
* 3.支持校验用户是否关注微信公众号判断
* 4.支持获取授权用户用户基本信息（已经关注了公众号，并且已经授权；未关注公众号，已经授权过）



## 4.基础能力说明

### 4.1 授权说明

* 参考 cn.acooly.auth.wechat.authenticator.web.portal.WechatWebApiController

*  授权访问地址：/wechat/webApi/index.html
*  微信回调地址：/wechat/webApi/backRedirect.html

### 4.2.1微信公众号 配置说明

* //组件开关 
* acooly.auth.wechat.enable=true
* //此组件文件存储-桶名称（如：微信小程序码 文件存储）重要说明：微信文件的BucketName 仅支持"公共读" 
* acooly.auth.wechat.obsFileBucketName=null
* //微信openapi请求地址(已默认)
* acooly.auth.wechat.webClient.apiUrl=https://api.weixin.qq.com
* //微信公众号的appid 详情 step 3
* acooly.auth.wechat.webClient.appid=xxxxxxxxxxx
* //微信公众号的secret 详情 step 3
* acooly.auth.wechat.webClient.secret=xxxxxxxxxxxxxxxxxxxxxx
* //snsapi_base （不弹出授权页面，直接跳转，只能获取用户openid）  
* //snsapi_userinfo （弹出授权页面，可通过openid拿到昵称、性别、所在地。即使在未关注的情况下，只要用户授权，也能获取其信息 (已默认)
* acooly.auth.wechat.webClient.scope=snsapi_userinfo
* // 令牌(Token)  详情 step 5
* acooly.auth.wechat.webClient.serverToken=HelloWorld
* //微信授权后跳转业务系统地址(URL);可以参考 cn.acooly.auth.wechat.authenticator.web.portal.WechatWebApiControl#backRedirect
* acooly.auth.wechat.webClient.redirectUri=http://www.xxx.com/wechat/webApi/backRedirect.html
* // 微信重定向后会带上state参数
* acooly.auth.wechat.webClient.state=hello,world




### 4.2.2微信小程序 配置说明
- //微信小程序api请求地址(已默认)
- acooly.auth.wechat.miniClient.apiUrl=https://api.weixin.qq.com
- //微信小程序appid
- acooly.auth.wechat.miniClient.appid=xxxx
- //微信小程序secret
- acooly.auth.wechat.miniClient.secret=xxxxxxxxxxxxxxxxxx


-  //配置多个小程序
- acooly.auth.wechat.miniManyClient[小程序appid1]=小程序secret1
- acooly.auth.wechat.miniManyClient[小程序appid2]=小程序secret2
 

### 4.2.3 网页授权登录 配置说明
- //微信网页授权登录 appid
- acooly.auth.wechat.webLoginClient.appid=xxxxxxxxxxxxxx
- //微信网页授权登录 secret
- acooly.auth.wechat.webLoginClient.secret=xxxxxxxxxxxxxxxxxxxxxxxxxx
- //令牌
- acooly.auth.wechat.webLoginClient.serverToken=HelloWorld
- //回调地址（扫码登陆成功，跳转地址）
- acooly.auth.wechat.webLoginClient.redirectUri=http://www.xxx.com/wechat/webLogin/api/backRedirect.html
- //重定向后会带上state参数
- acooly.auth.wechat.webLoginClient.state=HelloWorld

 



### 4.3.1 提供公众号基础功能说明

 * 参考 cn.acooly.auth.wechat.authenticator.service.WechatWebService
 
	
	/**
	 * 微信页面确认授权(step 1)
	 * 
	 * <li>redirectUri: 授权后重定向的回调链接地址， 请使用 urlEncode 对链接进行处理
	 * 
	 * @return
	 */
	public String getWechatOauthUrl(String redirectUri);

	/**
	 * 微信页面确认授权(step 1) 扩展方案，与 getWechatOauthUrl(redirectUri)方案相同
	 * 
	 * @param redirectUri  <li>授权后重定向的回调链接地址， 请使用 urlEncode 对链接进行处理 scope
	 * @param scope  <li>snsapi_base （不弹出授权页面，直接跳转，只能获取用户openid），snsapi_userinfo （弹出授权页面，可通过openid拿到昵称、性别、所在地。并且， 即使在未关注的情况下，只要用户授权，也能获取其信息）
	 * @param state <li> 重定向后会带上state参数，开发者可以填写a-zA-Z0-9的参数值，最多128字节
	 * 
	 * @return
	 */
	public String getWechatOauthUrl(String redirectUri, String scope, String state);
	
	/**
	 * 微信页面确认授权(step 2)
	 * https://developers.weixin.qq.com/doc/offiaccount/OA_Web_Apps/Wechat_webpage_authorization.html#3
	 * 
	 * @return
	 */
	public WechatUserInfoDto getWechatUserInfo(HttpServletRequest request, HttpServletResponse response);
	
	
	
	
### 4.3.2 提供微信小程序基础功能说明
- 参考 cn.acooly.auth.wechat.authenticator.service.WechatMiniService

##### 多个小程序客户端支持
- 参考 cn.acooly.auth.wechat.authenticator.services.WechatMiniManyService


	/**
	 * 获取小程序全局唯一后台接口调用凭据（access_token）
	 * 
	 * @return
	 */
	public String getAccessToken();
	
	/**
	 * 清除 access_token
	 * 
	 * @return
	 */
	public void cleanAccessToken();

	/**
	 * 登录凭证校验
	 * 
	 * @param jsCode
	 *               <li>调用接口获取登录凭证（code）
	 *               <li>详情参考 wx.login(Object object)
	 * 
	 * @return
	 */
	public WechatMiniSession loginAuthVerify(String jsCode);

	/**
	 * 获取小程序码，适用于需要的码数量极多的业务场景。
	 * 
	 * 
	 * @return
	 */
	public String getMiniProgramImgCode(String scene, String page);

### 4.3.2 网站应用微信登录功能说明
- 参考cn.acooly.auth.wechat.authenticator.service.WechatWebLoginService
				
	/**
	 * 第一步：请求CODE
	 * 
	 * <li>第三方使用网站应用授权登录前请注意已获取相应网页授权作用域（scope=snsapi_login），则可以通过在PC端打开以下链接：
	 * <li>https://open.weixin.qq.com/connect/qrconnect?appid=APPID&redirect_uri=REDIRECT_URI&response_type=code&scope=SCOPE&state=STATE#wechat_redirect
	 * <li>若提示“该链接无法访问”，请检查参数是否填写错误，如redirect_uri的域名与审核时填写的授权域名不一致或scope不为snsapi_login。
	 * 
	 * @return
	 */
	public String wechatWebLoginOauth(String redirectUri);

	/**
	 * 第二步：通过code获取access_token
	 * <li>https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code
	 * 
	 * @param code 用户授权成功后，微信回调跳转参数 code
	 * @return
	 */
	public WechatWebLoginInfoDto getOpenIdAndUnionid(HttpServletRequest request, HttpServletResponse response);


	
	
