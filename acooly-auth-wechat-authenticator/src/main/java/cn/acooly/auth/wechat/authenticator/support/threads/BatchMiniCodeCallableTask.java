package cn.acooly.auth.wechat.authenticator.support.threads;

import cn.acooly.auth.wechat.authenticator.oauth.mini.WechatMiniClientService;
import cn.acooly.auth.wechat.authenticator.oauth.mini.dto.WechatMiniProgramCodeDto;
import com.google.common.collect.Maps;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class BatchMiniCodeCallableTask implements Callable<Map<String, String>> {


    private WechatMiniClientService wechatMiniClientService;

    /**
     * 小程序appid
     **/
    private List<WechatMiniProgramCodeDto> wechatMiniProgramCodeDtoList;


    public BatchMiniCodeCallableTask( WechatMiniClientService wechatMiniClientService, List<WechatMiniProgramCodeDto> wechatMiniProgramCodeDtoList) {
        super();
        this.wechatMiniClientService = wechatMiniClientService;
        this.wechatMiniProgramCodeDtoList = wechatMiniProgramCodeDtoList;
    }

    /**
     * Computes a result, or throws an exception if unable to do so.
     *
     * @return computed result
     * @throws Exception if unable to compute a result
     */
    @Override
    public Map<String, String> call() throws Exception {
        HashMap<String, String> returnMapData = Maps.newHashMap();
        for (WechatMiniProgramCodeDto dto : wechatMiniProgramCodeDtoList) {
            String url = wechatMiniClientService.getMiniProgramImgCode(dto.getAccessToken(), dto.getAppId(), dto.getScene(), dto.getPage(), dto.isCheckPath(), dto.getEnvVersion());
            returnMapData.put(dto.getScene(), url);
        }
        return returnMapData;
    }
}
