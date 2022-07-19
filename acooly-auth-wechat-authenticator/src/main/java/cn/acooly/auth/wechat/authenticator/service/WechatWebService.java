package cn.acooly.auth.wechat.authenticator.service;

import cn.acooly.auth.wechat.authenticator.oauth.web.dto.WechatUserInfoDto;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 微信网页授权
 * 
 * @author CuiFuQ
 *
 */
public interface WechatWebService {

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
}
