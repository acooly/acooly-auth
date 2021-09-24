package cn.acooly.auth.wechat.authenticator.web.test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.acooly.auth.wechat.authenticator.WechatProperties;
import cn.acooly.auth.wechat.authenticator.oauth.web.WechatWebClientBaseService;
import cn.acooly.auth.wechat.authenticator.service.WechatMiniService;
import lombok.extern.slf4j.Slf4j;

@Profile("!online")
@Slf4j
@Controller
@RequestMapping(value = "/test/wechat/miniApi")
public class WechatMiniApiTestController {

	@Autowired
	private WechatMiniService wechatMiniService;

	@Autowired
	private WechatWebClientBaseService wechatWebClientBaseService;

	@Autowired
	private WechatProperties wechatProperties;

	/**
	 * 
	 * http://www.xxxx.com/wechat/miniApi/index.html
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

//		wechatMiniService.loginAuthVerify("001g5Drr1VTwTk0KkTtr1Myjrr1g5Drb");

		String scene = "hello";
		String page = null;
		String img = wechatMiniService.getMiniProgramImgCode(scene, page);

//		wechatWebClientBaseService.getUserInfoByAccessToken("oa-6s1dIQwpJxe9IuKCyHXJQMDvU", wechatMiniService.getAccessToken());

		return img;
	}
}
