package cn.acooly.auth.wechat.authenticator.web.boss;

import cn.acooly.auth.wechat.authenticator.WechatProperties;
import cn.acooly.auth.wechat.authenticator.service.WechatJsApiService;
import cn.acooly.auth.wechat.authenticator.service.WechatMiniService;
import cn.acooly.auth.wechat.authenticator.service.WechatWebTokenService;
import cn.acooly.auth.wechat.authenticator.services.WechatMiniManyService;
import com.acooly.core.common.domain.Entityable;
import com.acooly.core.common.service.EntityService;
import com.acooly.core.common.web.AbstractJsonEntityController;
import com.acooly.core.common.web.support.JsonResult;
import com.acooly.core.utils.Strings;
import com.acooly.module.security.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Set;

/**
 * 微信组件管理
 */
@Slf4j
@Controller
@RequestMapping(value = "/manage/wechat/auth/")
public class WechatAuthManagerController<T extends Entityable, M extends EntityService<T>>
        extends AbstractJsonEntityController {

    {
        allowMapping = "*";
    }

    @Autowired
    private WechatWebTokenService wechatWebTokenService;

    @Autowired
    private WechatMiniService wechatMiniService;

    @Autowired
    private WechatMiniManyService wechatMiniManyService;

    @Autowired
    private WechatJsApiService wechatJsApiService;

    @Autowired
    private WechatProperties wchatProperties;

    @RequestMapping(value = "index")
    public String index(HttpServletRequest request, HttpServletResponse response, Model model) {
        // 公众号
        WechatProperties.WebClient webClient = wchatProperties.getWebClient();
        model.addAttribute("webClient", webClient);
        // 小程序
        WechatProperties.MiniClient miniClient = wchatProperties.getMiniClient();
        model.addAttribute("miniClient", miniClient);

        // 多个小程序客户端
        Map<String, String> miniManyClient = wchatProperties.getMiniManyClient();
        Set<String> miniKeys = miniManyClient.keySet();
        for (String appId : miniKeys) {
            miniManyClient.put(appId, wechatMiniManyService.getAccessToken(appId));
        }
        model.addAttribute("miniManyClient", miniManyClient);

        // 网站应用(微信授权登录)
        model.addAttribute("webLoginClient", wchatProperties.getWebLoginClient());

        // 公众号接口凭证
        if (Strings.isNotBlank(webClient.getAppid())) {
            String webAccessToken = wechatWebTokenService.getAccessToken();
            model.addAttribute("webAccessToken", webAccessToken);
        }

        // 公众号接口凭证
        if (Strings.isNotBlank(miniClient.getAppid())) {
            String miniAccessToken = wechatMiniService.getAccessToken();
            model.addAttribute("miniAccessToken", miniAccessToken);
        }

        // 获取微信jsapi_ticket
        String jsApiTicket = wechatJsApiService.getJsApiTicket();
        model.addAttribute("jsApiTicket", jsApiTicket);

        return "/manage/wechat/auth/wechat_detail";
    }

    /**
     * 刷新
     */
    @RequestMapping(value = {"refreshData"})
    @ResponseBody
    public JsonResult refreshData(HttpServletRequest request, HttpServletResponse response) {
        JsonResult result = new JsonResult();
        try {
            String refreshKey = request.getParameter("refreshKey");
            if (refreshKey.equals("web_access_token")) {
                wechatWebTokenService.refreshAccessToken();
            }

            if (refreshKey.contains("mini_access_token")) {
                String appId = refreshKey.split("@")[1];
                wechatMiniManyService.cleanAccessToken(appId);
            }
            if (refreshKey.equals("jsapi_ticket")) {
                wechatJsApiService.cleanJsApiTicket();
            }
            User user = (User) SecurityUtils.getSubject().getPrincipal();
            log.info("用户{}:手动刷新了微信缓存key：{}", user.getUsername(), refreshKey);
            result.setMessage(refreshKey + "完成更新,请刷新当前页面");
        } catch (Exception e) {
            handleException(result, "刷新微信缓存", e);
        }
        return result;
    }

}
