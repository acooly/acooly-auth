package cn.acooly.auth.wechat.authenticator.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.acooly.auth.wechat.authenticator.oauth.login.dto.WechatWebLoginInfoDto;

/**
 * 网站应用授权登录
 * 
 * @author CuiFuQ
 *
 */
public interface WechatWebLoginService {

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
}
