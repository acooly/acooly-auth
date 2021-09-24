package cn.acooly.auth.wechat.authenticator.web.portal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.acooly.auth.wechat.authenticator.oauth.login.dto.WechatWebLoginInfoDto;
import cn.acooly.auth.wechat.authenticator.service.WechatWebLoginService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping(value = "/wechat/webLogin/api/")
public class WechatWebLoginApiController {

	@Autowired
	private WechatWebLoginService wechatWebLoginService;

	/**
	 * 
	 * http://www.xxxx.com/wechat/webLogin/api/index.html
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
		String redirectUri = request.getParameter("redirectUri");
		try {
			redirectUri = wechatWebLoginService.wechatWebLoginOauth(redirectUri);
		} catch (Exception e) {
			log.error("微信-页面授权登录页面失败:{}", redirectUri);
		}
		return "redirect:" + redirectUri;
	}

	/**
	 * 
	 * http://www.xxxx.com/wechat/webLogin/api/backRedirect.html
	 * 
	 * 微信回调跳转
	 * 
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping({ "backRedirect" })
	public String backRedirect(HttpServletRequest request, HttpServletResponse response, Model model) {
		try {
			WechatWebLoginInfoDto dto = wechatWebLoginService.getOpenIdAndUnionid(request, response);
			model.addAttribute("wechatUserInfo", dto);
			log.info("获取用户信息:{}", dto);
		} catch (Exception e) {
			log.error("微信回调跳转获取用户信息失败{}", e);
		}
		return "/wechat/wechat_user_info";
	}

}
