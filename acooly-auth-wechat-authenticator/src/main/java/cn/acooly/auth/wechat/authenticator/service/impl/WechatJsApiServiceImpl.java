package cn.acooly.auth.wechat.authenticator.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.acooly.auth.wechat.authenticator.oauth.ticket.WechatTicketClientService;
import cn.acooly.auth.wechat.authenticator.service.WechatJsApiService;

@Service("wechatJsApiService")
public class WechatJsApiServiceImpl implements WechatJsApiService {

	@Autowired
	private WechatTicketClientService wechatTicketClientService;

	@Override
	public String getJsApiTicket() {
		return wechatTicketClientService.getJsApiTicket();
	}

	@Override
	public void cleanJsApiTicket() {
		wechatTicketClientService.cleanJsApiTicket();
	}

}
