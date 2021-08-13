/**
 * acooly-auth-parent
 * <p>
 * Copyright 2014 Acooly.cn, Inc. All rights reserved.
 *
 * @author zhangpu
 * @date 2021-08-11 22:04
 */
package cn.acooly.auth.google.authenticator;

import com.acooly.core.utils.Encodes;
import com.acooly.core.utils.Strings;
import io.seruco.encoding.base62.Base62;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base32;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Locale;

/**
 * @author zhangpu
 * @date 2021-08-11 22:04
 */
@Slf4j
public class GoogleAuthenticatorsTest {

    @Test
    public void testCreateSecretKey() {
        String newSecretKey = GoogleAuthenticators.createSecretKey();
        log.info("Create new SecretKey: {}({})", newSecretKey, Strings.length(newSecretKey));



    }


    @Test
    public void testBaseX() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[20];
        random.nextBytes(bytes);

        String base32 =  new Base32().encodeAsString(bytes).toUpperCase(Locale.ROOT);
        log.info("base32: {} ({})",base32,Strings.length(base32));


        String safeBase64 =  Encodes.encodeUrlSafeBase64(bytes);
        log.info("safeBase64: {} ({})",safeBase64,Strings.length(safeBase64));

        String originalHex = Encodes.encodeHex(bytes);
        log.info("originalHex: {}", originalHex);


        Base62 base62 = Base62.createInstance();
        String base62String = new String(base62.encode(bytes));
        log.info("base62: {}({})", base62String, Strings.length(base62String));

        byte[] base64Decode = base62.decode(base62String.getBytes(StandardCharsets.UTF_8));
        String decodeBase62Hex = Encodes.encodeHex(base64Decode);
        log.info("decodeBase62Hex: {}", decodeBase62Hex);
        log.info("result: {}", Strings.equals(decodeBase62Hex, originalHex));


    }
}
