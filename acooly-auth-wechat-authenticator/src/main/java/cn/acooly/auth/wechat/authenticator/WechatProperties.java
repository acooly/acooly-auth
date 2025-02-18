package cn.acooly.auth.wechat.authenticator;

import com.google.common.collect.Maps;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.util.Strings;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

import static cn.acooly.auth.wechat.authenticator.WechatProperties.PREFIX;

/**
 * @author cuifuq
 */
@ConfigurationProperties(prefix = PREFIX)
@Data
public class WechatProperties {

	public static final String PREFIX = "acooly.auth.wechat";

	private Boolean enable = true;

	/**
	 * 此组件文件存储-桶名称（如：微信小程序码 文件存储）
	 *
	 *  <li>此配置开关 依赖：acooly.ofile.storageType=OBS</li>
	 * 	<li>obs默认名称；参考：acooly.obs.aliyun.bucketName</li>
	 * 	<li>重要说明：微信文件的BucketName 仅支持"公共读" </li>
	 *
	 */
	private String obsFileBucketName;

	/**
	 * 公众号
	 * 
	 * @author CuiFuQ
	 *
	 */

	private WebClient webClient = new WebClient();

	@Getter
	@Setter
	public static class WebClient {

		/** 微信api地址 **/
		private String apiUrl = "https://api.weixin.qq.com";

		/** 微信公众号授权地址：https://open.weixin.qq.com/connect/oauth2/authorize */
		private String oauthUrl = "https://open.weixin.qq.com/connect/oauth2/authorize";

		/** 公众号的唯一标识 */
		private String appid;

		/** 公众号的secret */
		private String secret;

		/** 授权后重定向的回调链接地址， 请使用 urlEncode 对链接进行处理 */
		private String redirectUri;

		/** 返回类型，请填写code */
		private String responseType = "code";

		/**
		 * 应用授权作用域
		 * <li>snsapi_base （不弹出授权页面，直接跳转，只能获取用户openid）</li>
		 * 
		 * <li>snsapi_userinfo （弹出授权页面，可通过openid拿到昵称、性别、所在地。并且，
		 * 即使在未关注的情况下，只要用户授权，也能获取其信息 ）</li>
		 */
		private String scope = "snsapi_userinfo";

		/** 重定向后会带上state参数 **/
		private String state = "STATE";

//		---------------
		/** 微信开发者中心 -->服务器配置---> Token **/
		private String serverToken;

	}

	/**
	 * 小程序
	 * 
	 * @author CuiFuQ
	 *
	 */
	private MiniClient miniClient = new MiniClient();

	@Getter
	@Setter
	public static class MiniClient {

		/** 小程序api地址 **/
		private String apiUrl = "https://api.weixin.qq.com";

		/** 小程序的唯一标识 */
		private String appid;

		/** 小程序的secret */
		private String secret;

	}

	/**
	 * 设置多个小程序
	 * <li>Map<appId,secret> ：Map<小程序appid,小程序secret>
	 * <li>配置文件：acooly.auth.wechat.miniManyClient[小程序appid]=小程序secret
	 */
	private Map<String, String> miniManyClient = Maps.newHashMap();

	/**
	 * 网站应用(微信授权登录)
	 * 
	 * @author CuiFuQ
	 *
	 */
	private WebLoginClient webLoginClient = new WebLoginClient();

	@Getter
	@Setter
	public static class WebLoginClient {

		/** 微信公众号授权地址：https://open.weixin.qq.com/connect/qrconnect */
		private String oauthUrl = "https://open.weixin.qq.com/connect/qrconnect";

		/** 微信授权登陆api地址 **/
		private String apiUrl = "https://api.weixin.qq.com";

		/** 网站应用的唯一标识 */
		private String appid;

		/** 网站应用的secret */
		private String secret;

		/** 授权后重定向的回调链接地址， 请使用 urlEncode 对链接进行处理 */
		private String redirectUri;

		/** 返回类型，请填写code */
		private String responseType = "code";

		/** 网页授权作用域 */
		private String scope = "snsapi_login";

		/** 重定向后会带上state参数 **/
		private String state = "STATE";

	}

	/**
	 * 多个小程序-客户端映射关系
	 * 
	 * @return
	 */
	public Map<String, String> getMiniManyClient() {
		Map<String, String> miniClients = miniManyClient;
		if (Strings.isNotBlank(miniClient.getAppid())) {
			miniClients.put(miniClient.getAppid(), miniClient.getSecret());
		}
		return miniClients;
	}

}
