package cn.acooly.auth.wechat.authenticator.oauth.mini.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class WechatMiniProgramCodeDto implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * 生成小程序码，接口的 accessToken；默认：不填写
     * <p>
     * 说明：
     * <li>生产环境：不需要填写,系统获取</li>
     * <li>非生产环境：支持手动传入</li>
     */
    private String accessToken;

    /**
     * 小程序appid
     **/
    private String appId;
    /**
     * 最大32个可见字符，只支持数字，大小写英文以及部分特殊字符：!#$&'()*+,/:;=?@-._~，其它字符请自行编码为合法字符（因不支持%，中文无法使用 urlencode 处理，请使用其他编码方式）
     **/
    private String scene;
    /**
     * 默认是主页，页面 page，例如 pages/index/index，根路径前不要填加 /，不能携带参数（参数请放在 scene 字段里），如果不填写这个字段，默认跳主页面。
     **/
    private String page;


    /**
     * 生产环境为 true；其他环境可以有指定
     * <p>
     * 默认是true，检查page 是否存在，为 true 时 page 必须是已经发布的小程序存在的页面（否则报错）；为 false 时允许小程序未发布或者 page 不存在， 但page 有数量上限（60000个）请勿滥用。
     */
    private boolean checkPath = true;

    /**
     * 生产环境为 release；其他环境可以有指定
     * <p>
     * 要打开的小程序版本。正式版为 "release"，体验版为 "trial"，开发版为 "develop"。默认是正式版。
     */
    private String envVersion = "release";

    public WechatMiniProgramCodeDto(String accessToken, String appId, String scene, String page) {
        this.accessToken = accessToken;
        this.appId = appId;
        this.scene = scene;
        this.page = page;
    }

    public WechatMiniProgramCodeDto(String accessToken,String appId, String scene, String page, boolean checkPath, String envVersion) {
        this.accessToken = accessToken;
        this.appId = appId;
        this.scene = scene;
        this.page = page;
        this.checkPath = checkPath;
        this.envVersion = envVersion;
    }
}
