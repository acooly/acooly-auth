package cn.acooly.auth.wechat.authenticator.oauth.ticket.impl;

import cn.acooly.auth.wechat.authenticator.WechatProperties;
import cn.acooly.auth.wechat.authenticator.oauth.ticket.WechatTicketClientService;
import cn.acooly.auth.wechat.authenticator.oauth.web.WechatWebClientBaseService;
import cn.acooly.auth.wechat.authenticator.oauth.web.enums.WechatWebClientEnum;
import com.acooly.core.common.exception.BusinessException;
import com.acooly.module.distributedlock.DistributedLockFactory;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.kevinsawicki.http.HttpRequest;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

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

	@Autowired
	private DistributedLockFactory factory;

	private final String WECHAT_JSAPI_TICKET = "wechat_web_jsApi_ticket";

	public static Integer REDIS_TRY_LOCK_TIME = 3;

	@Override
	public String getJsApiTicket() {
		// 获取缓存的jsapi_ticket
		String jsApiTicket = (String) redisTemplate.opsForValue().get(WECHAT_JSAPI_TICKET);
		if (StringUtils.isNotBlank(jsApiTicket)) {
			return jsApiTicket;
		}

		String jsApiKeyLock = WECHAT_JSAPI_TICKET + "_lock";
		Lock lock = factory.newLock(jsApiKeyLock);
		try {
			log.info("微信公众号[获取jsapi_ticket]-加锁-start");
			if (lock.tryLock(REDIS_TRY_LOCK_TIME, TimeUnit.SECONDS)) {
				// 获取缓存的jsapi_ticket
				jsApiTicket = (String) redisTemplate.opsForValue().get(WECHAT_JSAPI_TICKET);
				// 再次获取缓存jsapi_ticket;获取成功后返回
				if (StringUtils.isNotBlank(jsApiTicket)) {
					return jsApiTicket;
				}

				String openidUrl = wechatProperties.getWebClient().getApiUrl()
						+ WechatWebClientEnum.cgi_bin_ticket.code();
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
				if (bodyJson.get("errcode").equals("40001")) {
					log.info("微信公众号接口access_token过期,重新刷新access_token");
					wechatWebClientBaseService.refreshAccessToken();
				}
				jsApiTicket = setJsApiTicket(bodyJson);
			} else {
				log.info("微信公众号[获取jsapi_ticket]-加锁失败");
			}
		} catch (Exception e) {
			log.info("微信公众号[获取jsapi_ticket]-加锁异常", e);
			throw new BusinessException("获取jsapi_ticket失败,稍后重试");
		} finally {
			lock.unlock();
		}
		return jsApiTicket;

	}

	@Override
	public void cleanJsApiTicket() {
		redisTemplate.opsForValue().set(WECHAT_JSAPI_TICKET, null);

	}

	/**
	 *  提前10分钟过期
	 * @param bodyJson
	 * @return
	 */
	private String setJsApiTicket(JSONObject bodyJson) {
		log.info("redis设置微信公众号 jsapi_ticket：{}", bodyJson);
		String jsApiTicket = bodyJson.getString("ticket");
		Long expiresIn = bodyJson.getLong("expires_in");
		redisTemplate.opsForValue().set(WECHAT_JSAPI_TICKET, jsApiTicket, (expiresIn - 600), TimeUnit.SECONDS);
		return jsApiTicket;
	}

}
