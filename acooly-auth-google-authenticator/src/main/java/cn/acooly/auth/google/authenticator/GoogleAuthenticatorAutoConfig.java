package cn.acooly.auth.google.authenticator;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhangpu@acooly.cn
 */
@Configuration
@ConditionalOnWebApplication
@EnableConfigurationProperties({GoogleAuthenticatorProperties.class})
@ConditionalOnProperty(value = GoogleAuthenticatorProperties.PREFIX + ".enable", matchIfMissing = true)
@ComponentScan("cn.acooly.auth.google.authenticator")
public class GoogleAuthenticatorAutoConfig {


}
