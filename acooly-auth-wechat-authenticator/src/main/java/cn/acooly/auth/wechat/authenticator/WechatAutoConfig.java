package cn.acooly.auth.wechat.authenticator;

import static cn.acooly.auth.wechat.authenticator.WechatProperties.PREFIX;

import java.util.List;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.acooly.core.common.dao.support.StandardDatabaseScriptIniter;
import com.acooly.module.security.config.SecurityAutoConfig;
import com.google.common.collect.Lists;

/**
 * @author cuifuqiang
 */
@Configuration
@EnableConfigurationProperties({WechatProperties.class})
@ConditionalOnProperty(value = PREFIX + ".enable", matchIfMissing = true)
@ComponentScan(basePackages = "cn.acooly.auth.wechat.authenticator")
@AutoConfigureAfter(SecurityAutoConfig.class)

public class WechatAutoConfig {

	@Bean
	public StandardDatabaseScriptIniter wechatScriptIniter() {
		return new StandardDatabaseScriptIniter() {

			@Override
			public String getEvaluateTable() {
				return "wechat";
			}

			@Override
			public String getComponentName() {
				return "wechat";
			}

			@Override
			public List<String> getInitSqlFile() {
				return Lists.newArrayList();
			}
		};
	}
}
