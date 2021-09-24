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

### 1.1优势

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
* step 2： 左边菜单，首页-->开发-->基本配置-->公众号开发信息
* step 3: 启用-开发者密码(AppSecret) 并保存对应的AppSecret
* step 4: 设置IP白名单, 配置服务器出口IP 
* step 5: 设置-服务器配置，URL：服务器回调地址（/wechat/webApi/callback.html）；Token；EncodingAESKey（随机生成）；消息加解密方式 [参考](https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1421135319)
* step 6: 左边菜单，首页-->设置与开发-->公众号设置-->功能设置-->网页授权域名（1.将“微信授权文件”存在工程static目录；2.重启工程）
* step 7: 左边菜单，首页-->内容与互动-->内容工具-->自定义菜单  组件默认访问地址 （ /wechat/webApi/index.html）
* step 8: 左边菜单，首页-->设置与开发-->公众号设置-->帐号详情-->二维码，关注公众号，即可正常访问授权



### 2.3 微信小程序对接操作说明

* step 1: 登录凭证校验 cn.acooly.auth.wechat.authenticator.oauth.mini.WechatMiniClientService#loginAuthVerify


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

*  //组件开关 
* acooly.auth.wechat.enable=true
* //微信openapi请求地址(已默认)
* acooly.auth.wechat.webClient.apiUrl=https://api.weixin.qq.com
*  //微信公众号的appid 详情 step 3
* acooly.auth.wechat.webClient.appid=xxxxxxxxxxx
* //微信公众号的secret 详情 step 3
* acooly.auth.wechat.webClient.secret=xxxxxxxxxxxxxxxxxxxxxx
* //snsapi_base （不弹出授权页面，直接跳转，只能获取用户openid）  
* //snsapi_userinfo （弹出授权页面，可通过openid拿到昵称、性别、所在地。即使在未关注的情况下，只要用户授权，也能获取其信息 (已默认)
* acooly.auth.wechat.webClient.scope=snsapi_userinfo
* // 令牌(Token)  详情 step 5
* acooly.auth.wechat.webClient.serverToken=HelloWorld
*  //微信授权后跳转业务系统地址(URL);可以参考 cn.acooly.auth.wechat.authenticator.web.portal.WechatWebApiControl#backRedirect
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

 * 参考 cn.acooly.auth.wechat.authenticator.oauth.web.WechatWebClientService
 
		/**
		 * 微信页面确认授权(step 1)
		 * 
		 * @return
		 */
		public String getWechatOauthUrl(String redirectUri);
	
		/**
		 * 微信页面确认授权(step 2)
		 * 
		 * @return
		 */
		public WechatUserInfoDto getWechatUserInfo(HttpServletRequest request, HttpServletResponse response);
	
	
	
	
### 4.3.2 提供微信小程序基础功能说明
- 参考 cn.acooly.auth.wechat.authenticator.oauth.mini.WechatMiniClientService

		/**
		 * 获取小程序全局唯一后台接口调用凭据（access_token）
		 * 
		 * @return
		 */
		public String getAccessToken();
	
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
		 * @param scene
		 *              <li>
		 *              最大32个可见字符，只支持数字，大小写英文以及部分特殊字符：!#$&'()*+,/:;=?@-._~，其它字符请自行编码为合法字符（因不支持%，中文无法使用
		 *              urlencode 处理，请使用其他编码方式）
		 * @param page
		 *              <li>必须是已经发布的小程序存在的页面（否则报错），例如 pages/index/index, 根路径前不要填加
		 *              /,不能携带参数（参数请放在scene字段里），如果不填写这个字段，默认跳主页面
		 * @return
		 */
	
		public String getMiniProgramImgCode(String scene, String page);

### 4.3.2 网站应用微信登录功能说明
- 参考 cn.acooly.auth.wechat.authenticator.oauth.login.WechatWebLoginClientService
				
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
		 * 第二步：使用微信回调跳转参数 code；换取用户的基本信息（openId，unionid）等
		 * <li>https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code
		 * 
		 * @param code 用户授权成功后，微信回调跳转参数 code
		 * @return
		 */
		public WechatWebLoginInfoDto getOpenIdAndUnionid(HttpServletRequest request, HttpServletResponse response);


	
	
