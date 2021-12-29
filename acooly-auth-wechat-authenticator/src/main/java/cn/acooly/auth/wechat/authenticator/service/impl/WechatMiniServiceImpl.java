package cn.acooly.auth.wechat.authenticator.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.acooly.auth.wechat.authenticator.oauth.mini.WechatMiniClientService;
import cn.acooly.auth.wechat.authenticator.oauth.mini.dto.WechatMiniSession;
import cn.acooly.auth.wechat.authenticator.service.WechatMiniService;

@Service("wechatMiniService")
public class WechatMiniServiceImpl implements WechatMiniService {

	@Autowired
	private WechatMiniClientService wechatMiniClientService;

	@Override
	public WechatMiniSession loginAuthVerify(String jsCode) {
		return wechatMiniClientService.loginAuthVerify(jsCode);
	}

	@Override
	public String getMiniProgramImgCode(String scene, String page) {
		return wechatMiniClientService.getMiniProgramImgCode(scene, page);
	}

	@Override
	public String getAccessToken() {
		return wechatMiniClientService.getAccessToken();
	}

	@Override
	public void cleanAccessToken() {
		wechatMiniClientService.cleanAccessToken();
	}

}
