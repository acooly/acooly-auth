package cn.acooly.auth.wechat.authenticator.oauth.ticket.impl;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.acooly.core.common.exception.BusinessException;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.kevinsawicki.http.HttpRequest;
import com.google.common.collect.Maps;

import cn.acooly.auth.wechat.authenticator.WechatProperties;
import cn.acooly.auth.wechat.authenticator.oauth.ticket.WechatTicketClientService;
import cn.acooly.auth.wechat.authenticator.oauth.web.WechatWebClientBaseService;
import cn.acooly.auth.wechat.authenticator.oauth.web.enums.WechatWebClientEnum;
import lombok.extern.slf4j.Slf4j;

/**
 * 获取jsapi_ticket
 * 
 * @author CuiFuQ
 *
 */
@Slf4j
@Service("wechatTicketClientService")
public class WechatTicketClientServiceImpl implements WechatTicketClientService {

	@Autowired
	private WechatProperties wechatProperties;

	@Autowired
	private RedisTemplate redisTemplate;

	@Autowired
	private WechatWebClientBaseService wechatWebClientBaseService;

	private final String WECHAT_JSAPI_TICKET = "wechat_web_jsApi_ticket";

	@Override
	public String getJsApiTicket() {
		String jsApiTicket = (String) redisTemplate.opsForValue().get(WECHAT_JSAPI_TICKET);

		// 重新获取 jsApiTicket
		if (StringUtils.isBlank(jsApiTicket)) {
			String openidUrl = wechatProperties.getWebClient().getApiUrl() + WechatWebClientEnum.cgi_bin_ticket.code();
			Map<String, Object> requestData = Maps.newTreeMap();
			requestData.put("access_token", wechatWebClientBaseService.getAccessToken());
			requestData.put("type", "jsapi");
			String requestUrl = HttpRequest.append(openidUrl, requestData);

			log.info("微信公众号[获取jsapi_ticket],请求地址:{}", requestUrl);
			HttpRequest httpRequest = HttpRequest.get(requestUrl).acceptCharset(HttpRequest.CHARSET_UTF8);
			httpRequest.trustAllCerts();
			httpRequest.trustAllHosts();
			int httpCode = httpRequest.code();
			String resultBody = httpRequest.body(HttpRequest.CHARSET_UTF8);
			log.info("微信公众号[获取jsapi_ticket],响应数据:{}", resultBody);

			JSONObject bodyJson = JSON.parseObject(resultBody);
			if (httpCode != 200) {
				log.info("微信公众号获取jsapi_ticket失败，" + bodyJson.get("errmsg"));
				throw new BusinessException(bodyJson.getString("errmsg"), bodyJson.getString("errcode"));
			}
			log.info("公众号重新获取jsapi_ticket数据{}", bodyJson);
			jsApiTicket = setJsApiTicket(bodyJson);
		}
		return jsApiTicket;
	}

	@Override
	public void cleanJsApiTicket() {
		redisTemplate.opsForValue().set(WECHAT_JSAPI_TICKET, null);

	}

	private String setJsApiTicket(JSONObject bodyJson) {
		log.info("redis设置微信公众号 jsapi_ticket：{}", bodyJson);
		String jsApiTicket = bodyJson.getString("ticket");
		Long expiresIn = bodyJson.getLong("expires_in");
		redisTemplate.opsForValue().set(WECHAT_JSAPI_TICKET, jsApiTicket, (expiresIn - 900), TimeUnit.SECONDS);
		return jsApiTicket;
	}

}
