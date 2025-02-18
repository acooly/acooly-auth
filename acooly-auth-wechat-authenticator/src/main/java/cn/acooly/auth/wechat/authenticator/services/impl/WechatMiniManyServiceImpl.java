package cn.acooly.auth.wechat.authenticator.services.impl;

import cn.acooly.auth.wechat.authenticator.oauth.mini.WechatMiniClientService;
import cn.acooly.auth.wechat.authenticator.oauth.mini.dto.WechatMiniProgramCodeDto;
import cn.acooly.auth.wechat.authenticator.oauth.mini.dto.WechatMiniSession;
import cn.acooly.auth.wechat.authenticator.services.WechatMiniManyService;
import cn.acooly.auth.wechat.authenticator.support.threads.BatchMiniCodeCallableTask;
import com.acooly.core.common.boot.Env;
import com.acooly.core.common.exception.BusinessException;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Slf4j
@Service("wechatMiniManyService")
public class WechatMiniManyServiceImpl implements WechatMiniManyService {

    @Autowired
    private WechatMiniClientService wechatMiniClientService;

    @Override
    public String getAccessToken(String appId) {
        return wechatMiniClientService.getAccessToken(appId);
    }

    @Override
    public void cleanAccessToken(String appId) {
        wechatMiniClientService.cleanAccessToken(appId);
    }

    @Override
    public WechatMiniSession loginAuthVerify(String appId, String jsCode) {
        return wechatMiniClientService.loginAuthVerify(appId, jsCode);
    }

    @Override
    public String getMiniProgramImgCode(String appId, String scene, String page) {
        return wechatMiniClientService.getMiniProgramImgCode(appId, scene, page);
    }

    /**
     * 获取小程序码，适用于需要的码数量极多的业务场景。
     * <p>
     * 功能相同：String getMiniProgramImgCode(String appId, String scene, String page)
     *
     * @param dto
     * @return
     */
    @Override
    public String getMiniProgramImgCode(WechatMiniProgramCodeDto dto) {
        return wechatMiniClientService.getMiniProgramImgCode(dto.getAccessToken(),dto.getAppId(),dto.getScene(),dto.getPage(),dto.isCheckPath(),dto.getEnvVersion());
    }


    /**
     * 批量获取小程序码，适用于需要的码数量极多的业务场景。
     *
     * @param wechatMiniProgramCodeDtoList
     * @return Map<scene, 访问路径>
     */

    public Map<String, String> getBatchMiniProgramImgCode(List<WechatMiniProgramCodeDto> wechatMiniProgramCodeDtoList) {
        Map<String, String> miniImgUrl = Maps.newHashMap();
        long time = System.currentTimeMillis();
        int miniCodeSize = wechatMiniProgramCodeDtoList.size();
        if (miniCodeSize > 500) {
            throw new BusinessException("微信小程序[批量获取小程序码]单次不能操作500数量");
        }

        //线程池大小，最大控制在100个
        int nThreads = 1;
        if (miniCodeSize > 50) {
            nThreads = miniCodeSize / 5;
        } else {
            nThreads = miniCodeSize;
        }
        log.info("微信小程序[批量获取小程序码]，线程数量：{}", nThreads);
        ExecutorService taskExecutor = Executors.newFixedThreadPool(nThreads);


        List<Future<Map<String, String>>> futures = new CopyOnWriteArrayList<>();

        //线程-并发执行
        try {
            for (int i = 0; i < nThreads; i++) {
                List<WechatMiniProgramCodeDto> threadsList = wechatMiniProgramCodeDtoList.subList(miniCodeSize / nThreads * i, miniCodeSize / nThreads * (i + 1));
                Future<Map<String, String>> futureResult = taskExecutor.submit(new BatchMiniCodeCallableTask(wechatMiniClientService, threadsList));
                futures.add(futureResult);
            }
            //获取线程并发执行结果
            for (Future<Map<String, String>> futureMap : futures) {
                miniImgUrl.putAll(futureMap.get());
            }
        } catch (Exception e) {
            log.info("微信小程序[批量获取小程序码]异常{}", e);
        } finally {
            taskExecutor.shutdown();
        }

        time = System.currentTimeMillis() - time;
        log.info("微信小程序[批量获取小程序码]结果，线程数量：{},预生成数量:{},实际生成数量:{},用时：{} s", nThreads, miniCodeSize, miniImgUrl.keySet().size(), (time / 1000.00));

        return miniImgUrl;
    }


}
