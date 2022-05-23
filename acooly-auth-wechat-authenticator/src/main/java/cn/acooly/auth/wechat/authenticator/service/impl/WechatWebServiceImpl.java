package cn.acooly.auth.wechat.authenticator.service.impl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.acooly.auth.wechat.authenticator.oauth.web.WechatWebClientService;
import cn.acooly.auth.wechat.authenticator.oauth.web.dto.WechatUserInfoDto;
import cn.acooly.auth.wechat.authenticator.service.WechatWebService;

@Service("wechatWebService")
public class WechatWebServiceImpl implements WechatWebService {

	@Autowired
	private WechatWebClientService wechatWebClientService;

	@Override
	public String getWechatOauthUrl(String redirectUri) {
		return wechatWebClientService.getWechatOauthUrl(redirectUri, null, null);
	}

	@Override
	public String getWechatOauthUrl(String redirectUri, String scope, String state) {
		return wechatWebClientService.getWechatOauthUrl(redirectUri, scope, state);
	}

	@Override
	public WechatUserInfoDto getWechatUserInfo(HttpServletRequest request, HttpServletResponse response) {
		return wechatWebClientService.getWechatUserInfo(request, response);
	}

}
