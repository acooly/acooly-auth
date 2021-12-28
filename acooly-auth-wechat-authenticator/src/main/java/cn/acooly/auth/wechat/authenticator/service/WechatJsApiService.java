package cn.acooly.auth.wechat.authenticator.service;

public interface WechatJsApiService {

	/**
	 * 获取微信jsapi_ticket
	 * 
	 * <li>jsapi_ticket
	 *
	 * <li>参考：https://developers.weixin.qq.com/doc/offiaccount/OA_Web_Apps/JS-SDK.html
	 * 
	 * @return
	 */
	public String getJsApiTicket();

	/**
	 * 清除系统缓存 JsApiTicket
	 */
	public void cleanJsApiTicket();

}
