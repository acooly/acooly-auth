package cn.acooly.auth.wechat.authenticator.services;

import cn.acooly.auth.wechat.authenticator.oauth.mini.dto.WechatMiniSession;

public interface WechatMiniManyService {

	/**
	 * 获取小程序全局唯一后台接口调用凭据（access_token）
	 * 
	 * @return
	 */
	public String getAccessToken(String appId);

	/**
	 * 清除 access_token
	 * 
	 * @return
	 */
	public void cleanAccessToken(String appId);

	/**
	 * 登录凭证校验
	 * 
	 * @param jsCode
	 *               <li>调用接口获取登录凭证（code）
	 *               <li>详情参考 wx.login(Object object)
	 * 
	 * @return
	 */
	public WechatMiniSession loginAuthVerify(String appId, String jsCode);

	/**
	 * 获取小程序码，适用于需要的码数量极多的业务场景。
	 * 
	 * 
	 * @return
	 */
	public String getMiniProgramImgCode(String appId, String scene, String page);

}
