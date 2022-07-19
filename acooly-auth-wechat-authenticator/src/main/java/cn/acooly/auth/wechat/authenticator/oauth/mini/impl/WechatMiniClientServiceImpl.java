package cn.acooly.auth.wechat.authenticator.oauth.mini.impl;

import cn.acooly.auth.wechat.authenticator.WechatProperties;
import cn.acooly.auth.wechat.authenticator.oauth.mini.WechatMiniClientService;
import cn.acooly.auth.wechat.authenticator.oauth.mini.dto.WechatMiniSession;
import cn.acooly.auth.wechat.authenticator.oauth.mini.enums.WechatMiniClientEnum;
import com.acooly.core.common.exception.BusinessException;
import com.acooly.core.utils.Dates;
import com.acooly.core.utils.Ids;
import com.acooly.core.utils.Strings;
import com.acooly.core.utils.mapper.JsonMapper;
import com.acooly.module.distributedlock.DistributedLockFactory;
import com.acooly.module.obs.ObsService;
import com.acooly.module.obs.common.OssFile;
import com.acooly.module.obs.common.OssUtils;
import com.acooly.module.ofile.OFileProperties;
import com.acooly.module.ofile.enums.StorageTypeEnum;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.kevinsawicki.http.HttpRequest;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * 微信小程序授权
 *
 * @author CuiFuQ
 */
@Slf4j
@Service("wechatMiniClientService")
public class WechatMiniClientServiceImpl implements WechatMiniClientService {

    @Autowired
    private WechatProperties wechatProperties;

    @SuppressWarnings("rawtypes")
    @Autowired
    private RedisTemplate redisTemplate;

    private final String WECHAT_MINI_ACCESS_TOKEN = "WECHAT_MINI_ACCESS_TOKEN_";

    public static Integer REDIS_TRY_LOCK_TIME = 3;

    @Autowired
    private DistributedLockFactory factory;

    /**
     * 多个小程序-缓存值
     */
    private Map<String, String> miniManyClient = Maps.newHashMap();


    //- file service -start
    @Autowired
    private OFileProperties oFileProperties;
    @Autowired
    private ObsService obsService;
    //- file service -end

    /**
     * 获取指定小程序的 Secret
     *
     * @param appId
     * @return
     */
    public String getMiniSecret(String appId) {
        if (miniManyClient.isEmpty()) {
            miniManyClient = wechatProperties.getMiniManyClient();
        }
        String miniSecret = miniManyClient.get(appId);
        if (Strings.isBlank(miniSecret)) {
            throw new BusinessException("没有配置对应的小程序配置,请关注配置文件[acooly.auth.wechat.miniManyClient]");
        }
        return miniSecret;
    }

    @Override
    public WechatMiniSession loginAuthVerify(String appId, String jsCode) {
        String secret = getMiniSecret(appId);
        String openidUrl = wechatProperties.getMiniClient().getApiUrl() + WechatMiniClientEnum.sns_jscode2session.code();
        Map<String, Object> requestData = Maps.newTreeMap();
        requestData.put("appid", appId);
        requestData.put("secret", secret);
        requestData.put("js_code", jsCode);
        requestData.put("grant_type", "authorization_code");
        String requestUrl = HttpRequest.append(openidUrl, requestData);
        log.info("微信小程序[登录凭证校验], appId{},请求地址:{}", appId, requestUrl);

        HttpRequest httpRequest = HttpRequest.get(requestUrl).acceptCharset(HttpRequest.CHARSET_UTF8);
        httpRequest.trustAllCerts();
        httpRequest.trustAllHosts();
        int httpCode = httpRequest.code();
        String httpResponse = httpRequest.body(HttpRequest.CHARSET_UTF8);

        JSONObject bodyJson = JSON.parseObject(httpResponse);
        log.info("微信小程序[登录凭证校验], appId{},响应数据:{}", appId, httpResponse);

        if (httpCode != 200) {
            log.info("微信小程序[登录凭证校验]appId:{},失败:{}", appId, bodyJson);
            throw new BusinessException(httpResponse);
        }

        String errMsg = bodyJson.getString("errmsg");
        if (StringUtils.isNotBlank(errMsg)) {
            log.info("微信小程序[登录凭证校验]失败 appId:{},失败:{}", appId, bodyJson);
            throw new BusinessException(bodyJson.getString("errmsg"), bodyJson.getString("errcode"));
        }

        // wechatMiniSession 微信用户信息
        JsonMapper jsonMapper = JsonMapper.nonEmptyMapper();
        WechatMiniSession dto = jsonMapper.fromJson(httpResponse, WechatMiniSession.class);
        return dto;
    }

    public String getAccessToken(String appId) {
        String accessToken = (String) redisTemplate.opsForValue().get(WECHAT_MINI_ACCESS_TOKEN + appId);
        if (StringUtils.isNotBlank(accessToken)) {
            return accessToken;
        }

        String accessTokenLock = WECHAT_MINI_ACCESS_TOKEN + appId + "_lock";
        Lock lock = factory.newLock(accessTokenLock);
        try {

            log.info("微信小程序[获取access_token]-加锁-start,appId:{}", appId);
            if (lock.tryLock(REDIS_TRY_LOCK_TIME, TimeUnit.SECONDS)) {
                accessToken = (String) redisTemplate.opsForValue().get(WECHAT_MINI_ACCESS_TOKEN + appId);
                // 再次获取缓存accessToken;获取成功后返回
                if (StringUtils.isNotBlank(accessToken)) {
                    return accessToken;
                }

                // 重新获取 accessToken
                String openidUrl = wechatProperties.getMiniClient().getApiUrl() + WechatMiniClientEnum.cgi_bin_token.code();
                Map<String, Object> requestData = Maps.newTreeMap();
                requestData.put("appid", appId);
                requestData.put("secret", getMiniSecret(appId));
                requestData.put("grant_type", "client_credential");

                String requestUrl = HttpRequest.append(openidUrl, requestData);
                log.info("微信小程序[获取access_token],appId:{},请求地址:{}", appId, requestUrl);
                HttpRequest httpRequest = HttpRequest.get(requestUrl).acceptCharset(HttpRequest.CHARSET_UTF8);
                httpRequest.trustAllCerts();
                httpRequest.trustAllHosts();
                int httpCode = httpRequest.code();
                String resultBody = httpRequest.body(HttpRequest.CHARSET_UTF8);
                log.info("微信小程序[获取access_token],appId:{},响应数据:{}", appId, resultBody);

                JSONObject bodyJson = JSON.parseObject(resultBody);
                if (httpCode != 200) {
                    log.info("微信小程序[获取access_token]失败,appId:{},响应数据:{}", appId, bodyJson.get("errmsg"));
                    throw new BusinessException(bodyJson.getString("errmsg"), bodyJson.getString("errcode"));
                }
                log.info("微信小程序重新获取access_token,appId:{},数据{}", appId, bodyJson);
                accessToken = setRedisAccessToken(appId, bodyJson);

            } else {
                log.info("微信小程序[获取access_token]-加锁失败,appId:{}", appId);
            }

        } catch (Exception e) {
            log.info("微信小程序[获取access_token]appId:{},-加锁异常{}", appId, e);
            throw new BusinessException("获取微信小程序access_token失败,稍后重试");
        } finally {
            lock.unlock();
        }
        return accessToken;

    }

    /**
     * 设置redis，时间过期：微信有效时间-10分钟 ，重新获取
     *
     * @param obj
     */
    @SuppressWarnings("unchecked")
    private String setRedisAccessToken(String appId, JSONObject obj) {
        log.info("redis设置微信小程序appId:{}, access_token：{}", appId, obj);
        String accessToken = obj.getString("access_token");
        Long expiresIn = obj.getLong("expires_in");
        redisTemplate.opsForValue().set(WECHAT_MINI_ACCESS_TOKEN + appId, accessToken, (expiresIn - 600), TimeUnit.SECONDS);
        return accessToken;
    }

    public String getMiniProgramImgCode(String appId, String scene, String page) {
        if (Strings.isBlank(scene)) {
            throw new BusinessException("scene不能为空");
        }
        String accessToken = getAccessToken(appId);
        String imgCodeUrl = getMiniProgramImgCode(appId, accessToken, scene, page, true, "release");
        return imgCodeUrl;
    }

    /**
     * @param appId       小程序的唯一标识
     * @param accessToken 接口调用凭证，该参数为 URL 参数
     * @param scene       最大32个可见字符，只支持数字，大小写英文以及部分特殊字符：!#$&'()*+,/:;=?@-._~，其它字符请自行编码为合法字符（因不支持%，中文无法使用urlencode 处理，请使用其他编码方式）
     * @param page        必须是已经发布的小程序存在的页面（否则报错），例如 pages/index/index, 根路径前不要填加 /,不能携带参数（参数请放在scene字段里），如果不填写这个字段，默认跳主页面
     * @param checkPath   默认是true，检查page 是否存在，为 true 时 page 必须是已经发布的小程序存在的页面（否则报错）；为 false 时允许小程序未发布或者 page 不存在， 但page 有数量上限（60000个）请勿滥用。
     * @param envVersion  要打开的小程序版本。正式版为 "release"，体验版为 "trial"，开发版为 "develop"。默认是正式版。
     * @return
     */
    public String getMiniProgramImgCode(String appId, String accessToken, String scene, String page, boolean checkPath, String envVersion) {
        String imgCodeUrl = null;
        String openidUrl = wechatProperties.getMiniClient().getApiUrl() + WechatMiniClientEnum.wxa_getwxacodeunlimit.code();
        Map<String, Object> requestData = Maps.newTreeMap();
        requestData.put("access_token", accessToken);

        String requestUrl = HttpRequest.append(openidUrl, requestData);

        JSONObject paramJson = new JSONObject();
        paramJson.put("scene", scene);
        paramJson.put("page", page);
        paramJson.put("check_path", checkPath);
        paramJson.put("env_version", envVersion);

        log.info("微信小程序[获取小程序码],appId:{},请求地址:{},请求数据：{}", appId, requestUrl, paramJson);

        HttpRequest httpRequest = HttpRequest.post(requestUrl).acceptCharset(HttpRequest.CHARSET_UTF8).send(paramJson.toString());
        httpRequest.trustAllCerts();
        httpRequest.trustAllHosts();

        int httpCode = httpRequest.code();
        if (httpCode != 200) {
            log.info("微信小程序[获取小程序码]appId:{}失败", appId);
            throw new BusinessException("微信小程序[获取小程序码]失败");
        }

        byte[] bodyByte = httpRequest.bytes();

        ByteArrayInputStream inputStream = null;
        FileOutputStream outputStream = null;

        log.info("微信小程序[获取小程序码],appId:{},响应报文长度：{}", appId, bodyByte.length);

        if (bodyByte.length < 1000) {
            String bodyStr = new String(bodyByte);
            log.info("微信小程序[获取小程序码]appId:{},响应报文：{}", appId, bodyStr);
            log.info("微信小程序[获取小程序码]appId:{},删除redis key:{}", appId, WECHAT_MINI_ACCESS_TOKEN + appId);
            redisTemplate.delete(WECHAT_MINI_ACCESS_TOKEN + appId);
            throw new BusinessException("获取小程序码,失败：{}", bodyStr);
        }

        //系统设置 obs存储
        if (oFileProperties.getStorageType().equals(StorageTypeEnum.OBS.getCode())) {
            String bucketName = wechatProperties.getObsFileBucketName();
            String key = OssUtils.generateKey("wechat-mini", Ids.oid() + ".png");
            OssFile ossFile = obsService.putObject(bucketName, key, inputStream = new ByteArrayInputStream(bodyByte));
            imgCodeUrl = ossFile.getUrl();
        } else {
            try {
                inputStream = new ByteArrayInputStream(bodyByte);
                StringBuffer storageRoot = new StringBuffer();
                storageRoot.append(oFileProperties.getStorageRoot());
                String bucketName = wechatProperties.getObsFileBucketName();
                if (Strings.isBlank(bucketName)) {
                    bucketName = "wechat";
                }
                storageRoot.append(bucketName + File.separator + "wechat-mini");
                storageRoot.append(File.separator);
                storageRoot.append(dateFilePath());
                storageRoot.append(File.separator);

                File file = new File(storageRoot.toString());
                File files = new File(storageRoot.toString(), Ids.oid() + ".png");

                String fileAbsolutePath = files.getAbsolutePath();

                // \var\log\webapps\nfs\wechat\miniLogo\2019\03\31\o19033109521185600007.png
                log.info("微信小程序[获取小程序码]appId:{},本地存储路径：{}", appId, fileAbsolutePath);

                if (!file.exists()) {
                    file.mkdirs();
                }

                if (!files.exists()) {
                    files.createNewFile();
                }

                outputStream = new FileOutputStream(files);
                int len = 0;
                byte[] buf = new byte[1024];
                while ((len = inputStream.read(buf, 0, 1024)) != -1) {
                    outputStream.write(buf, 0, len);
                }
                outputStream.flush();

                String serverRoot = oFileProperties.getServerRoot();

                String filePath = fileAbsolutePath.replace(oFileProperties.getStorageRoot(), "");
                if (!Strings.startsWith(filePath, "/")) {
                    filePath = "/" + filePath;
                }
                imgCodeUrl = serverRoot + filePath;

            } catch (Exception e) {
                log.error("微信小程序[获取小程序码]appId:{}本地存储失败{}", appId, e);
            } finally {
                try {
                    inputStream.close();
                    outputStream.close();
                } catch (IOException e) {
                    log.error("微信小程序[获取小程序码]失败{}", e);
                }
            }
        }
        log.info("微信小程序[获取小程序码]appId:{},浏览器访问地址：{}", appId, imgCodeUrl);
        return imgCodeUrl;
    }

    /**
     * 时间的文件路径
     *
     * @return
     */
    private String dateFilePath() {
        String pathTimestamp = Dates.format(new Date(), "yyyyMMddHHmmssSSS");
        String yearPart = StringUtils.left(pathTimestamp, 4);
        String monthPart = StringUtils.substring(pathTimestamp, 4, 6);
        String dayPart = StringUtils.substring(pathTimestamp, 6, 8);
        String subPath = File.separator + yearPart + File.separator + monthPart + File.separator + dayPart;
        return subPath;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void cleanAccessToken(String appId) {
        redisTemplate.opsForValue().set(WECHAT_MINI_ACCESS_TOKEN + appId, null);
    }

}
