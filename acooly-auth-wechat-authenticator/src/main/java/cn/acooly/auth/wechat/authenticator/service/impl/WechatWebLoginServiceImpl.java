package cn.acooly.auth.wechat.authenticator.service.impl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.acooly.auth.wechat.authenticator.oauth.login.WechatWebLoginClientService;
import cn.acooly.auth.wechat.authenticator.oauth.login.dto.WechatWebLoginInfoDto;
import cn.acooly.auth.wechat.authenticator.service.WechatWebLoginService;

@Service("wechatWebLoginService")
public class WechatWebLoginServiceImpl implements WechatWebLoginService {

	@Autowired
	private WechatWebLoginClientService wechatWebLoginClientService;

	@Override
	public String wechatWebLoginOauth(String redirectUri) {
		return wechatWebLoginClientService.wechatWebLoginOauth(redirectUri);
	}

	@Override
	public WechatWebLoginInfoDto getOpenIdAndUnionid(HttpServletRequest request, HttpServletResponse response) {
		return wechatWebLoginClientService.getOpenIdAndUnionid(request, response);
	}

}
