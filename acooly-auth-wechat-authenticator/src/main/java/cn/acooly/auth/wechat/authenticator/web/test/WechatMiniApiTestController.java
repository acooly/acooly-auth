package cn.acooly.auth.wechat.authenticator.web.test;

import cn.acooly.auth.wechat.authenticator.WechatProperties;
import cn.acooly.auth.wechat.authenticator.oauth.mini.dto.WechatMiniProgramCodeDto;
import cn.acooly.auth.wechat.authenticator.oauth.mini.dto.WechatMiniSession;
import cn.acooly.auth.wechat.authenticator.oauth.web.WechatWebClientBaseService;
import cn.acooly.auth.wechat.authenticator.service.WechatMiniService;
import cn.acooly.auth.wechat.authenticator.services.WechatMiniManyService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@Profile("!online")
@Slf4j
@Controller
@RequestMapping(value = "/test/wechat/miniApi")
public class WechatMiniApiTestController {

    @Autowired
    private WechatMiniService wechatMiniService;
    @Autowired
    private WechatWebClientBaseService wechatWebClientBaseService;
    @Autowired
    private WechatProperties wechatProperties;

    @Autowired
    private WechatMiniManyService wechatMiniManyService;

    /**
     * http://www.xxxx.com/wechat/miniApi/index.html
     *
     * <li>redirectUri 需要 URLEncoder.encode("xxxxxxx", "utf-8")
     * <li>
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("index")
    @ResponseBody
    public String index(HttpServletRequest request, HttpServletResponse response) {

//		wechatMiniService.loginAuthVerify("001g5Drr1VTwTk0KkTtr1Myjrr1g5Drb");

        String scene = "1002658";
        String page = "pages/index";
        String img = wechatMiniService.getMiniProgramImgCode(scene, page);

//		wechatWebClientBaseService.getUserInfoByAccessToken("oa-6s1dIQwpJxe9IuKCyHXJQMDvU", wechatMiniService.getAccessToken());

        return img;
    }


    /**
     * http://www.xxxx.com/wechat/miniApi/index.html
     *
     * <li>redirectUri 需要 URLEncoder.encode("xxxxxxx", "utf-8")
     * <li>
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("batchMiniImgCode")
    @ResponseBody
    public Object batchMiniImgCode(HttpServletRequest request, HttpServletResponse response) {
        Map<String, String> scenePageList = Maps.newHashMap();


        List<WechatMiniProgramCodeDto> wechatMiniProgramCodeDtoList = Lists.newArrayList();
        for (int i = 0; i <15; i++) {
            wechatMiniProgramCodeDtoList.add(new WechatMiniProgramCodeDto("wx856bdf8cb330bd5d", "0" + i, "pages/index"));
        }
        Map<String, String> imgs = wechatMiniManyService.getBatchMiniProgramImgCode(wechatMiniProgramCodeDtoList);

        System.out.println("最终生成二维码数量："+imgs.keySet().size());

        return imgs;
    }

    /**
     * http://www.xxxx.com/wechat/miniApi/index.html
     *
     * <li>redirectUri 需要 URLEncoder.encode("xxxxxxx", "utf-8")
     * <li>
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("loginAuthVerify")
    @ResponseBody
    public WechatMiniSession loginAuthVerify(HttpServletRequest request, HttpServletResponse response) {
        String jsCode = request.getParameter("jsCode");
        WechatMiniSession wechatMiniSession = wechatMiniService.loginAuthVerify(jsCode);
        return wechatMiniSession;
    }

}
