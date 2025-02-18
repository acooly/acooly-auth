package cn.acooly.auth.wechat.authenticator.oauth.mini.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class WechatMiniSession implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 用户唯一标识
	 */
	@JsonProperty("openid")
	private String openId;

	/**
	 * 会话密钥
	 */
	@JsonProperty("session_key")
	private String sessionKey;

	/**
	 * 用户在开放平台的唯一标识符，在满足 UnionID 下发条件的情况下会返回
	 */
	@JsonProperty("unionid")
	private String unionId;

	/**
	 * 错误码
	 * 
	 * <li>-1: 系统繁忙，此时请开发者稍候再试
	 * <li>0 : 请求成功
	 * <li>40029: code 无效
	 * <li>45011: 频率限制，每个用户每分钟100次
	 */
	@JsonProperty("errcode")
	private String errCode;

	/**
	 * 错误信息
	 */
	@JsonProperty("errmsg")
	private String errMsg;

}
