package cn.acooly.auth.wechat.authenticator.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.acooly.auth.wechat.authenticator.oauth.web.WechatWebClientBaseService;
import cn.acooly.auth.wechat.authenticator.oauth.web.dto.WechatUserInfoDto;
import cn.acooly.auth.wechat.authenticator.service.WechatWebTokenService;

@Service("wechatWebTokenService")
public class WechatWebTokenServiceImpl implements WechatWebTokenService {

	@Autowired
	private WechatWebClientBaseService wechatWebClientBaseService;

	@Override
	public String getAccessToken() {
		return wechatWebClientBaseService.getAccessToken();
	}

	@Override
	public void cleanAccessToken() {
		wechatWebClientBaseService.cleanAccessToken();
	}

	@Override
	public String refreshAccessToken() {
		return wechatWebClientBaseService.refreshAccessToken();
	}

	@Override
	public boolean isSubscribe(String openId) {
		boolean flag = false;
		WechatUserInfoDto dto = wechatWebClientBaseService.getUserInfoByOpenId(openId);
		String subscribeStr = dto.getSubscribe();
		if (StringUtils.isNotBlank(subscribeStr)) {
			if (Integer.parseInt(subscribeStr) != 0) {
				flag = true;
			}
		}
		return flag;
	}

	@Override
	public WechatUserInfoDto getUserInfoBySubscribe(String openId) {
		return wechatWebClientBaseService.getUserInfoByOpenId(openId);
	}

	@Override
	public WechatUserInfoDto getUserInfoByOpenId(String openId) {
		return wechatWebClientBaseService.getUserInfoByOpenId(openId);
	}

}
