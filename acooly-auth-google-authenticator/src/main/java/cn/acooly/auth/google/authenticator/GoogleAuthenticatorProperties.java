package cn.acooly.auth.google.authenticator;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author zhangpu
 */
@ConfigurationProperties(GoogleAuthenticatorProperties.PREFIX)
@Data
public class GoogleAuthenticatorProperties {
    public static final String PREFIX = "acooly.auth.google.authenticator";
    private Boolean enable = true;
}
