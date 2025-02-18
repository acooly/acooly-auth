package cn.acooly.auth.wechat.authenticator.services;

import cn.acooly.auth.wechat.authenticator.oauth.mini.dto.WechatMiniProgramCodeDto;
import cn.acooly.auth.wechat.authenticator.oauth.mini.dto.WechatMiniSession;

import java.util.List;
import java.util.Map;

public interface WechatMiniManyService {

    /**
     * 获取小程序全局唯一后台接口调用凭据（access_token）
     *
     * @return
     */
    public String getAccessToken(String appId);

    /**
     * 清除 access_token
     *
     * @return
     */
    public void cleanAccessToken(String appId);

    /**
     * 登录凭证校验
     *
     * @param jsCode <li>调用接口获取登录凭证（code）
     *               <li>详情参考 wx.login(Object object)
     * @return
     */
    public WechatMiniSession loginAuthVerify(String appId, String jsCode);

    /**
     * 获取小程序码，适用于需要的码数量极多的业务场景。(仅生产环境)
     * <p>
     * 功能相同：String getMiniProgramImgCode(WechatMiniProgramCodeDto wechatMiniProgramCodeDto)
     *
     * @return
     */
    public String getMiniProgramImgCode(String appId, String scene, String page);

    /**
     * 获取小程序码，适用于需要的码数量极多的业务场景。(支持生产环境或者其他环境)
     * <p>
     * 功能相同：String getMiniProgramImgCode(String appId, String scene, String page)
     *
     * @return
     */
    public String getMiniProgramImgCode(WechatMiniProgramCodeDto wechatMiniProgramCodeDto);

    /**
     * 批量获取小程序码，适用于需要的码数量极多的业务场景。
     *
     * @param wechatMiniProgramCodeDtoList
     * @return Map<scene, 访问路径>
     */
    public Map<String, String> getBatchMiniProgramImgCode(List<WechatMiniProgramCodeDto> wechatMiniProgramCodeDtoList);


}
