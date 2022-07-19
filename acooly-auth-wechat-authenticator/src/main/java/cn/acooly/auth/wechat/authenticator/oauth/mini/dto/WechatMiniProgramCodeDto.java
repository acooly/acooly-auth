package cn.acooly.auth.wechat.authenticator.oauth.mini.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class WechatMiniProgramCodeDto implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 小程序appid
	 **/
	private String appId;
	/**
	 * 小程序--请求参数【最大32个可见字符】
	 **/
	private String scene;
	/**
	 * 小程序--跳转的路径
	 **/
	private String page;

	public WechatMiniProgramCodeDto(String appId, String scene, String page) {
		this.appId = appId;
		this.scene = scene;
		this.page = page;
	}
}
