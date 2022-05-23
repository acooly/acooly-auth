package cn.acooly.auth.wechat.authenticator.web.portal;

import java.security.MessageDigest;
import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.acooly.auth.wechat.authenticator.WechatProperties;
import cn.acooly.auth.wechat.authenticator.oauth.web.dto.WechatUserInfoDto;
import cn.acooly.auth.wechat.authenticator.service.WechatWebService;
import cn.acooly.auth.wechat.authenticator.service.WechatWebTokenService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping(value = "/wechat/webApi/")
public class WechatWebApiController {

	@Autowired
	private WechatWebService wechatWebService;

	private WechatWebTokenService wechatWebTokenService;

	@Autowired
	private WechatProperties wechatProperties;

	/**
	 * 
	 * http://www.xxxx.com/wechat/webApi/index.html
	 * 
	 * <li>redirectUri: •redirectUri: 授权后重定向的回调链接地址， 请使用 urlEncode 对链接进行处理 ----:需要
	 * URLEncoder.encode("xxxxxxx", "utf-8")
	 * <li>scope : •snsapi_base （不弹出授权页面，直接跳转，只能获取用户openid），snsapi_userinfo
	 * （弹出授权页面，可通过openid拿到昵称、性别、所在地。并且， 即使在未关注的情况下，只要用户授权，也能获取其信息 ）
	 * <li>state : •state: 重定向后会带上state参数，开发者可以填写a-zA-Z0-9的参数值，最多128字节
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("index")
	public String index(HttpServletRequest request, HttpServletResponse response) {
		String redirectUri = request.getParameter("redirectUri");
		String scope = request.getParameter("scope");
		String state = request.getParameter("state");
		try {
			redirectUri = wechatWebService.getWechatOauthUrl(redirectUri, scope, state);
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
	public String backRedirect(HttpServletRequest request, HttpServletResponse response, Model model) {
		try {
			WechatUserInfoDto dto = wechatWebService.getWechatUserInfo(request, response);
			dto = wechatWebTokenService.getUserInfoBySubscribe(dto.getOpenId());
			model.addAttribute("wechatUserInfo", dto);

			log.info("获取用户信息:{}", dto);
		} catch (Exception e) {
			log.error("微信回调跳转获取用户信息失败{}", e);
		}
		return "/wechat/wechat_user_info";
	}

	/**
	 * 微信回调验证（微信开发平台-->服务器配置）
	 * 
	 * http://www.xxxx.com/wechat/webApi/callback.html
	 * 
	 * 
	 * @param request
	 * @param response
	 * @param model
	 */
	@RequestMapping({ "callback" })
	@ResponseBody
	public void callback(HttpServletRequest request, HttpServletResponse response, Model model) {
		try {
			String token = wechatProperties.getWebClient().getServerToken();
			if (StringUtils.isBlank(token)) {
				log.error("配置文件[acooly.wechat.serverToken]未设置,微信回调跳转验证失败");
				return;
			}
			// 微信加密签名
			String signature = request.getParameter("signature");
			// 随机字符串
			String echostr = request.getParameter("echostr");
			// 时间戳
			String timestamp = request.getParameter("timestamp");
			// 随机数
			String nonce = request.getParameter("nonce");

			String[] str = { token, timestamp, nonce };
			Arrays.sort(str); // 字典序排序
			String bigStr = str[0] + str[1] + str[2];

			String digest = Hex.encodeHexString(MessageDigest.getInstance("SHA1").digest(bigStr.getBytes()))
					.toLowerCase();

			// 确认请求来至微信
			if (digest.equals(signature)) {
				response.getWriter().print(echostr);
			} else {
				log.error("微信回调成功,验证失败");
			}
		} catch (Exception e) {
			log.error("微信回调跳转失败", e);
		}
	}
}
