<#assign jodd=JspTaglibs["http://www.springside.org.cn/jodd_form"] />
<html style="text-align: center; font-size: xx-large;">

用户公众号信息<br/>
${wechatUserInfo.nickname}
${wechatUserInfo.openid}
${wechatUserInfo.unionid}
<img src="${wechatUserInfo.headimgurl}" />

<br/><br/><br/>

${wechatUserInfo}
</html>