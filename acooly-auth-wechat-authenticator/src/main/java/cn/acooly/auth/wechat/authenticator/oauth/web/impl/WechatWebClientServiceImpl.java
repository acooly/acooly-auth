package cn.acooly.auth.wechat.authenticator.oauth.web.impl;

import cn.acooly.auth.wechat.authenticator.WechatProperties;
import cn.acooly.auth.wechat.authenticator.oauth.web.WechatWebClientBaseService;
import cn.acooly.auth.wechat.authenticator.oauth.web.WechatWebClientService;
import cn.acooly.auth.wechat.authenticator.oauth.web.dto.WechatOpenIdDto;
import cn.acooly.auth.wechat.authenticator.oauth.web.dto.WechatUserInfoDto;
import com.acooly.core.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 微信网页授权
 * 
 * @author CuiFuQ
 *
 */
@Slf4j
@Service("wechatWebClientService")
public class WechatWebClientServiceImpl implements WechatWebClientService {

	@Autowired
	private WechatProperties wechatProperties;

	@Autowired
	private WechatWebClientBaseService wechatWebClientBaseService;

	@Override
	public String getWechatOauthUrl(String redirectUri, String scope, String state) {
		// 检查微信开关
		checkWechatEnable();
		return wechatWebClientBaseService.wechatOauth(redirectUri, scope, state);
	}

	@Override
	public WechatUserInfoDto getWechatUserInfo(HttpServletRequest request, HttpServletResponse response) {
		WechatUserInfoDto wechatUserInfo = null;
		try {
			response.setContentType("text/html;charset=utf-8");
			WechatOpenIdDto wechatOpenIdDto = wechatWebClientBaseService.getOpenId(request, response);
			if (wechatProperties.getWebClient().getScope().equals("snsapi_base")) {
				wechatUserInfo = new WechatUserInfoDto();
				wechatUserInfo.setOpenId(wechatOpenIdDto.getOpenId());
			} else {
				wechatUserInfo = wechatWebClientBaseService.getUserInfoByAccessToken(wechatOpenIdDto.getOpenId(),
						wechatOpenIdDto.getAccessToken());
			}
		} catch (Exception e) {
			log.error("微信页面确认授权(step 2)获取用户信息失败{}", e);
		}
		return wechatUserInfo;
	}

	/** 微信开关 */
	private void checkWechatEnable() {
		if (!wechatProperties.getEnable()) {
			throw new BusinessException("微信组件开关：已关闭");
		}
	}

}
