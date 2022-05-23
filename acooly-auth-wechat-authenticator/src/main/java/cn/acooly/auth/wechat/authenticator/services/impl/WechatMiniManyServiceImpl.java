package cn.acooly.auth.wechat.authenticator.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.acooly.auth.wechat.authenticator.oauth.mini.WechatMiniClientService;
import cn.acooly.auth.wechat.authenticator.oauth.mini.dto.WechatMiniSession;
import cn.acooly.auth.wechat.authenticator.services.WechatMiniManyService;

@Service("wechatMiniManyService")
public class WechatMiniManyServiceImpl implements WechatMiniManyService {

	@Autowired
	private WechatMiniClientService wechatMiniClientService;

	@Override
	public String getAccessToken(String appId) {
		return wechatMiniClientService.getAccessToken(appId);
	}

	@Override
	public void cleanAccessToken(String appId) {
		wechatMiniClientService.cleanAccessToken(appId);
	}

	@Override
	public WechatMiniSession loginAuthVerify(String appId, String jsCode) {
		return wechatMiniClientService.loginAuthVerify(appId, jsCode);
	}

	@Override
	public String getMiniProgramImgCode(String appId, String scene, String page) {
		return wechatMiniClientService.getMiniProgramImgCode(appId, scene, page);
	}

}
