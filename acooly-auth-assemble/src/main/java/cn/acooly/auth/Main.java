package cn.acooly.auth;

import com.acooly.core.common.BootApp;
import com.acooly.core.common.boot.Apps;
import org.springframework.boot.SpringApplication;

/**
 * @author acooly
 */
@BootApp(sysName = "acooly-auth", httpPort = 8011)
public class Main {
    public static void main(String[] args) {
        Apps.setProfileIfNotExists("online");
        new SpringApplication(Main.class).run(args);
    }
}