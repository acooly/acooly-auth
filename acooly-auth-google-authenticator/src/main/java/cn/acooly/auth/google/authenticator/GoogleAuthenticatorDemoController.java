/**
 * acooly-auth-parent
 * <p>
 * Copyright 2014 Acooly.cn, Inc. All rights reserved.
 *
 * @author zhangpu
 * @date 2021-08-13 10:08
 */
package cn.acooly.auth.google.authenticator;

import com.acooly.core.utils.Asserts;
import com.acooly.core.utils.Barcodes;
import com.acooly.core.utils.Servlets;
import com.acooly.core.utils.Strings;
import com.google.common.collect.Maps;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 案例Demo演示
 *
 * @author https://github.com/ghthou/google-authenticator-integration
 * @author zhangpu
 * @date 2021-08-13 10:08
 */
@Slf4j
@RequestMapping("/google/authenticator/")
@RestController
@Profile("!online")
public class GoogleAuthenticatorDemoController {

    /**
     * 服务名称（应用标准）,如 Acooly, Huobi, Google Github 等。
     * 不参与运算,只是为了在GoogleAuthenticator中与其他服务作区分
     */
    public static final String GA_TEST_ISSUER = "Acooly";

    public static final String GA_TEST_ACCOUNT = "zhangpu@acooly.cn";

    /**
     * 测试模拟数据库
     */
    private Map<String, String> databases = Maps.newHashMap();


    /**
     * 第一步：绑定用户秘钥并生成客户端二维码
     * <li>生成用户的Google Authenticator Key二维码
     * <li>用户通过手机上的Google Authenticator App扫码后，
     * 添加带服务名称和说明的用户绑定服务及秘钥的本地随机验证码生成器。</li>
     * <li>每30秒更新一次随机密码（逻辑参考TOTP）</li>
     */
    @SneakyThrows
    @GetMapping("qrCode")
    public void qrCode(HttpServletRequest request, HttpServletResponse response) {
        // 获取用户标志(从数据库或者缓存),可以是登录名,邮箱,手机(不参与运算,只是为了与其他服务作区分)
        String account = getAccount(request);
        // 在同一服务（应用）下，每个用户标志下都独立生成秘钥，并与用户唯一标志建立绑定管理，持久化到数据库中，这里使用内存Map模拟数据库
        String secretKey = loadAndSaveSecretKeyFromDb(account);

        // 生成 Google Authenticator Key Uri
        String keyUri = GoogleAuthenticators.createKeyUri(secretKey, account, GA_TEST_ISSUER);

        // 把 keyUri 生成二维码图片
        try (ServletOutputStream outputStream = response.getOutputStream()) {
            Barcodes.encodeQRcode(keyUri, "UTF-8", 300, outputStream, false);
        }
    }


    /**
     * 第二步：验证
     * <li>在完成第一步后，从你手机Google Authenticator App上复制验证码</li>
     * <li>访问：/google/authenticator/verify.html?totpCode=验证码</li>
     * <li>浏览器返回验证结果（真实情况是你的认证登录阶段通过验证码验证）</li>
     */
    @GetMapping("/verify")
    public Map<String, Object> verification(HttpServletRequest request) {
        // 获取客户端传入的totp验证码（来自你手机上Google Authenticator生成的）
        String totoCode = Servlets.getParameter(request, "totpCode");
        // 获取account
        String account = getAccount(request);
        // 获取secretKey(从数据库获取account对应的)
        String secretKey = loadAndSaveSecretKeyFromDb(account);
        Boolean verifyResult = GoogleAuthenticators.verification(secretKey, totoCode);
        Map<String, Object> result = Maps.newLinkedHashMap();
        result.put("account", account);
        result.put("totoCode", totoCode);
        result.put("result", verifyResult ? "验证通过" : "验证未通过");
        return result;
    }


    /**
     * 显示测试数据集
     * account:secretKey
     *
     * @return
     */
    @GetMapping
    public Map<String, String> showDb() {
        return databases;
    }


    private String loadAndSaveSecretKeyFromDb(String account) {
        Asserts.notEmpty(account, "用户标志");
        String secretKey = this.databases.get(account);
        if (Strings.isBlank(secretKey)) {
            secretKey = GoogleAuthenticators.createSecretKey();
            this.databases.put(account, secretKey);
        }
        return secretKey;
    }

    private String getAccount(HttpServletRequest request) {
        String account = Servlets.getParameter("account");
        if (Strings.isBlank(account)) {
            account = GA_TEST_ACCOUNT;
        }
        return account;
    }


}
