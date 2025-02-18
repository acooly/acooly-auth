package cn.acooly.auth.wechat.authenticator.oauth.web.impl;

import cn.acooly.auth.wechat.authenticator.WechatProperties;
import cn.acooly.auth.wechat.authenticator.oauth.web.WechatWebClientBaseService;
import cn.acooly.auth.wechat.authenticator.oauth.web.dto.WechatOpenIdDto;
import cn.acooly.auth.wechat.authenticator.oauth.web.dto.WechatUserInfoDto;
import cn.acooly.auth.wechat.authenticator.oauth.web.enums.WechatWebClientEnum;
import com.acooly.core.common.exception.BusinessException;
import com.acooly.core.utils.mapper.JsonMapper;
import com.acooly.module.distributedlock.DistributedLockFactory;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.esotericsoftware.minlog.Log;
import com.github.kevinsawicki.http.HttpRequest;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * 微信网页授权
 * 
 * @author CuiFuQ
 *
 */
@Slf4j
@Service("wechatWebClientBaseService")
public class WechatWebClientBaseServiceImpl implements WechatWebClientBaseService {

	@Autowired
	private WechatProperties wechatProperties;

	@SuppressWarnings("rawtypes")
	@Autowired
	private RedisTemplate redisTemplate;

	@Autowired
	private DistributedLockFactory factory;

	private final String WECHAT_ACCESS_TOKEN = "wechat_web_access_token";

	public static Integer REDIS_TRY_LOCK_TIME = 3;

	@Override
	public String wechatOauth(String redirectUri, String scope, String state) {
		StringBuffer wechatUrl = new StringBuffer();
		try {
			wechatUrl.append(wechatProperties.getWebClient().getOauthUrl());
			wechatUrl.append("?appid=");
			wechatUrl.append(wechatProperties.getWebClient().getAppid());

			// 授权后重定向的回调链接地址， 请使用 urlEncode 对链接进行处理
			wechatUrl.append("&redirect_uri=");
			if (StringUtils.isNotBlank(redirectUri)) {
				wechatUrl.append(redirectUri);
			} else {
				wechatUrl.append(URLEncoder.encode(wechatProperties.getWebClient().getRedirectUri(), "utf-8"));
			}

			// 返回类型，请填写code
			wechatUrl.append("&response_type=");
			wechatUrl.append(wechatProperties.getWebClient().getResponseType());

			// 应用授权作用域
			wechatUrl.append("&scope=");
			if (StringUtils.isNotBlank(scope)) {
				wechatUrl.append(scope);
			} else {
				wechatUrl.append(wechatProperties.getWebClient().getScope());
			}

			// 重定向后会带上state参数
			wechatUrl.append("&state=");
			if (StringUtils.isNotBlank(state)) {
				wechatUrl.append(state);
			} else {
				wechatUrl.append(wechatProperties.getWebClient().getState());
			}

			// 无论直接打开还是做页面302重定向时候，必须带此参数
			wechatUrl.append("#wechat_redirect");
		} catch (Exception e) {
			Log.info("微信组件开关：已关闭");
			return wechatProperties.getWebClient().getRedirectUri();
		}
		log.info("访问微信公众号授权地址:{}", wechatUrl);
		return wechatUrl.toString();
	}

	@Override
	public String getAccessToken() {
		String accessToken = (String) redisTemplate.opsForValue().get(WECHAT_ACCESS_TOKEN);
		if (StringUtils.isNotBlank(accessToken)) {
			return accessToken;
		}

		String accessTokenLock = WECHAT_ACCESS_TOKEN + "_lock";
		Lock lock = factory.newLock(accessTokenLock);
		try {
			log.info("微信公众号[获取access_token]-加锁-start");
			if (lock.tryLock(REDIS_TRY_LOCK_TIME, TimeUnit.SECONDS)) {
				accessToken = (String) redisTemplate.opsForValue().get(WECHAT_ACCESS_TOKEN);
				// 再次获取缓存accessToken;获取成功后返回
				if (StringUtils.isNotBlank(accessToken)) {
					return accessToken;
				}

				// 重新获取 accessToken
				String openidUrl = wechatProperties.getWebClient().getApiUrl()
						+ WechatWebClientEnum.cgi_bin_token.code();
				Map<String, Object> requestData = Maps.newTreeMap();
				requestData.put("appid", wechatProperties.getWebClient().getAppid());
				requestData.put("secret", wechatProperties.getWebClient().getSecret());
				requestData.put("grant_type", "client_credential");

				String requestUrl = HttpRequest.append(openidUrl, requestData);

				log.info("微信公众号[获取access_token],请求地址:{}", requestUrl);
				HttpRequest httpRequest = HttpRequest.get(requestUrl).acceptCharset(HttpRequest.CHARSET_UTF8);
				httpRequest.trustAllCerts();
				httpRequest.trustAllHosts();
				int httpCode = httpRequest.code();
				String resultBody = httpRequest.body(HttpRequest.CHARSET_UTF8);
				log.info("微信公众号[获取access_token],响应数据:{}", resultBody);

				JSONObject bodyJson = JSON.parseObject(resultBody);
				if (httpCode != 200) {
					log.info("微信公众号获取accessToken失败，" + bodyJson.get("errmsg"));
					throw new BusinessException(bodyJson.getString("errmsg"), bodyJson.getString("errcode"));
				}
				accessToken = setRedisAccessToken(bodyJson);
			} else {
				log.info("微信公众号[获取access_token]-加锁失败");
			}
		} catch (Exception e) {
			log.info("微信公众号[获取access_token]-加锁异常{}", e);
			throw new BusinessException("获取access_token失败,稍后重试");
		} finally {
			lock.unlock();
		}
		return accessToken;

	}

	@SuppressWarnings("unchecked")
	@Override
	public void cleanAccessToken() {
		redisTemplate.opsForValue().set(WECHAT_ACCESS_TOKEN, null);
	}

	public String refreshAccessToken() {
		cleanAccessToken();
		return getAccessToken();
	}

	@Override
	public boolean accessTokenCheck(String openId) {
		String openidUrl = wechatProperties.getWebClient().getApiUrl() + WechatWebClientEnum.sns_auth.code();
		Map<String, Object> requestData = Maps.newTreeMap();
		requestData.put("openid", openId);
		requestData.put("access_token", getAccessToken());

		String requestUrl = HttpRequest.append(openidUrl, requestData);
		log.info("微信公众号[检验授权凭证（access_token）是否过期],请求地址:{}", requestUrl);

		HttpRequest httpRequest = HttpRequest.get(requestUrl).acceptCharset(HttpRequest.CHARSET_UTF8);
		httpRequest.trustAllCerts();
		httpRequest.trustAllHosts();
		int httpCode = httpRequest.code();
		String resultBody = httpRequest.body(HttpRequest.CHARSET_UTF8);
		log.info("微信公众号[检验授权凭证（access_token）是否过期],响应数据:{}", resultBody);
		JSONObject obj = JSON.parseObject(resultBody);

		if (httpCode != 200) {
			log.info("验证微信公众号授权码网络异常errcode:{},errmsg:{}", obj.getString("errcode"), obj.getString("errmsg"));
			return false;
		}
		if (obj.getInteger("errcode") != 0) {
			return false;
		}

		return true;
	}

	@Override
	public WechatUserInfoDto getUserInfoByAccessToken(String openId, String accessToken) {
		return getUserInfoBase(WechatWebClientEnum.sns_userinfo, openId, accessToken);
	}

	@Override
	public WechatUserInfoDto getUserInfoByOpenId(String openId) {
		return getUserInfoBase(WechatWebClientEnum.cgi_bin_user_info, openId, getAccessToken());
	}

	private WechatUserInfoDto getUserInfoBase(WechatWebClientEnum WechatWebEnumCode, String openId,
			String accessToken) {
		String openidUrl = wechatProperties.getWebClient().getApiUrl() + WechatWebEnumCode.code();
		Map<String, Object> requestData = Maps.newTreeMap();
		requestData.put("openid", openId);
		requestData.put("access_token", accessToken);
		requestData.put("lang", "zh_CN");
		String requestUrl = HttpRequest.append(openidUrl, requestData);
		log.info("微信公众号[获取微信用户信息],请求地址:{}", requestUrl);

		HttpRequest httpRequest = HttpRequest.get(requestUrl).acceptCharset(HttpRequest.CHARSET_UTF8);
		httpRequest.trustAllCerts();
		httpRequest.trustAllHosts();
		int httpCode = httpRequest.code();
		String httpResponse = httpRequest.body(HttpRequest.CHARSET_UTF8);

		JSONObject bodyJson = JSON.parseObject(httpResponse);
		log.info("微信公众号[获取微信用户信息],响应数据:{}", httpResponse);

		if (httpCode != 200) {
			log.info("获取微信公众号用户信息异常" + bodyJson);
			throw new BusinessException(httpResponse);
		}

		String errMsg = bodyJson.getString("errmsg");
		if (StringUtils.isNotBlank(errMsg)) {
			log.error("获取微信公众号用户信息失败：{}", bodyJson);
			throw new BusinessException(bodyJson.getString("errmsg"), bodyJson.getString("errcode"));
		}

		// wechatUserInfo 微信用户信息
		JsonMapper jsonMapper = JsonMapper.nonEmptyMapper();
		WechatUserInfoDto dto = jsonMapper.fromJson(httpResponse, WechatUserInfoDto.class);
		return dto;
	}

	@Override
	public WechatOpenIdDto getOpenId(HttpServletRequest request, HttpServletResponse response) {
		response.setContentType("text/html;charset=utf-8");

		String code = request.getParameter("code");
		String state = request.getParameter("state");

		log.info("微信公众号页面跳转授权响应参数code:{},state:{}", code, state);

		String openidUrl = wechatProperties.getWebClient().getApiUrl()
				+ WechatWebClientEnum.sns_oauth2_access_token.code();

		Map<String, Object> requestData = Maps.newTreeMap();
		requestData.put("appid", wechatProperties.getWebClient().getAppid());
		requestData.put("secret", wechatProperties.getWebClient().getSecret());
		requestData.put("code", code);
		requestData.put("grant_type", "authorization_code");

		String requestUrl = HttpRequest.append(openidUrl, requestData);
		HttpRequest httpRequest = HttpRequest.get(requestUrl).acceptCharset(HttpRequest.CHARSET_UTF8);
		httpRequest.trustAllCerts();
		httpRequest.trustAllHosts();
		int httpCode = httpRequest.code();
		String resultBody = httpRequest.body(HttpRequest.CHARSET_UTF8);

		JSONObject obj = JSON.parseObject(resultBody);

		if (httpCode != 200) {
			log.info("微信公众号登录获取授权失败" + obj.get("errmsg"));
			throw new BusinessException(obj.getString("errmsg"), obj.getString("errcode"));
		}
		if (null != obj.get("errcode")) {
			log.info("微信公众号登录获取授权失败" + obj.get("errmsg"));
			throw new BusinessException(obj.getString("errmsg"), obj.getString("errcode"));
		}

		JsonMapper jsonMapper = JsonMapper.nonEmptyMapper();
		WechatOpenIdDto dto = jsonMapper.fromJson(resultBody, WechatOpenIdDto.class);
		return dto;
	}

	/**
	 * 设置redis，时间过期：微信有效时间-15分钟 ，重新获取
	 * 
	 * @param obj
	 */
	@SuppressWarnings("unchecked")
	private String setRedisAccessToken(JSONObject obj) {
		log.info("redis设置微信公众号 access_token：{}", obj);
		String accessToken = obj.getString("access_token");
		Long expiresIn = obj.getLong("expires_in");
		redisTemplate.opsForValue().set(WECHAT_ACCESS_TOKEN, accessToken, (expiresIn - 900), TimeUnit.SECONDS);
		return accessToken;
	}

}
