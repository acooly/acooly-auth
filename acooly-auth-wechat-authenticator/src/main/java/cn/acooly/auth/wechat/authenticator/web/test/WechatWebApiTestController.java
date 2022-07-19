package cn.acooly.auth.wechat.authenticator.web.test;

import cn.acooly.auth.wechat.authenticator.WechatProperties;
import cn.acooly.auth.wechat.authenticator.oauth.web.dto.WechatUserInfoDto;
import cn.acooly.auth.wechat.authenticator.service.WechatWebService;
import cn.acooly.auth.wechat.authenticator.service.WechatWebTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Profile("!online")
@Slf4j
@Controller
@RequestMapping(value = "/test/wechat/web")
public class WechatWebApiTestController {

	@Autowired
	private WechatWebService wechatWebService;

	@Autowired
	private WechatWebTokenService wechatWebTokenService;

	@Autowired
	private WechatProperties wechatProperties;

	/**
	 * 
	 * http://www.xxxx.com/wechat/webApi/index.html
	 * 
	 * <li>redirectUri 需要 URLEncoder.encode("xxxxxxx", "utf-8")
	 * <li>
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("index")
	public String index(HttpServletRequest request, HttpServletResponse response) {
		String redirectUri = "http://wechat.ycysaas.cn/test/wechat/web/backRedirect.html";
		try {
			redirectUri = wechatWebService.getWechatOauthUrl(redirectUri);
		} catch (Exception e) {
			log.error("微信-用户授权页面失败:{}", redirectUri);
		}
		return "redirect:" + redirectUri;
	}

	/**
	 * 
	 * http://www.xxxx.com/wechat/webApi/backRedirect.html
	 * 
	 * 微信回调跳转
	 * 
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping({ "backRedirect" })
	@ResponseBody
	public WechatUserInfoDto backRedirect(HttpServletRequest request, HttpServletResponse response, Model model) {
		// 使用的token不同，获取的信息信息存在差异
		WechatUserInfoDto dto = wechatWebService.getWechatUserInfo(request, response);
		return dto;
	}

}
