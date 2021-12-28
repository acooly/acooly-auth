package cn.acooly.auth.wechat.authenticator.oauth.ticket;

/**
 * 获取jsapi_ticket
 * 
 * @author CuiFuQ
 *
 */
public interface WechatTicketClientService {

	/**
	 * 获取微信jsapi_ticket
	 * 
	 * @return
	 */
	String getJsApiTicket();

	/**
	 * 清除系统缓存 JsApiTicket
	 */
	void cleanJsApiTicket();

}
