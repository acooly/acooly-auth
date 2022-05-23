

<div style="padding: 5px;font-family:微软雅黑;">


<h4>微信公众号</h4>
<table class="tableForm" width="100%">
	<tr>
		<th>开发平台:</th>
		<td>https://developers.weixin.qq.com/doc/offiaccount/Basic_Information/Access_Overview.html</td>
	</tr>
	<tr>
		<th>appid:</th>
		<td>${webClient.appid}</td>
	</tr>					
	<tr>
		<th>redirectUri:</th>
		<td>${webClient.redirectUri}</td>
	</tr>
	<!--
	<tr>
		<th>scope:</th>
		<td>${webClient.scope}<br/>
		<p>应用授权作用域<br/>snsapi_base （不弹出授权页面，直接跳转，只能获取用户openid），<br/>snsapi_userinfo （弹出授权页面，可通过openid拿到昵称、性别、所在地。并且， 即使在未关注的情况下，只要用户授权，也能获取其信息 ）</p>
		</td>
	</tr>
	-->
	<tr>
		<th>公众号接口<br/>access_token:</th>
		<td>${webAccessToken}
		<br/>
			<a href="#" class="easyui-linkbutton" plain="true" onclick="refresh_access_token('web_access_token')"><i class="fa fa-refresh fa-lg fa-fw fa-col"></i>刷新access_token</a>
		<br/>
			access_token是公众号的全局唯一接口调用凭据，公众号调用各接口时都需使用access_token。access_token的有效期目前为2个小时;每日调用量：2000次
		</td>
	</tr>		
	<tr>
		<th>公众号<br/>jsapi_ticket:</th>
		<td>${jsApiTicket}
		<br/>
			<a href="#" class="easyui-linkbutton" plain="true" onclick="refresh_access_token('jsapi_ticket')"><i class="fa fa-refresh fa-lg fa-fw fa-col"></i>刷新jsApiTicket</a>
		<br/>
		jsapi_ticket是公众号用于调用微信JS接口的临时票据;;jsapi_ticket的有效期为7200秒
		</td>
	</tr>			
</table>
<br/>
<h4>小程序</h4>
<table class="tableForm" width="100%">
	<tr>
		<th>开发平台:</th>
		<td>https://developers.weixin.qq.com/miniprogram/dev/api-backend</td>
	</tr>
	<#list miniManyClient! as map>
	    <#list map?keys as keystr>
			<tr>
				<th>appid:</th>
				<td>${keystr}</td>
			</tr>	
			<tr>
				<th>小程序接口<br/>access_token:</th>
				<td>${map[keystr]}
				<br/>
				   <a href="#" class="easyui-linkbutton" plain="true" onclick="refresh_access_token('mini_access_token@${keystr}')"><i class="fa fa-refresh fa-lg fa-fw fa-col"></i>刷新access_token</a>
				<br/>
				access_token的有效期目前为2个小时</td>
			</tr>
	    </#list>
	</#list>
					
</table>

<br/>
<h4>网站应用</h4>
<table class="tableForm" width="100%">
	<tr>
		<th>开发平台:</th>
		<td>https://developers.weixin.qq.com/doc/oplatform/Website_App/WeChat_Login/Wechat_Login.html</td>
	</tr>
	<tr>
		<th>appid:</th>
		<td>${webLoginClient.appid}</td>
	</tr>					
	<tr>
		<th>redirectUri:</th>
		<td>${webLoginClient.redirectUri}</td>
	</tr>
	<tr>
		<th>scope:</th>
		<td>${webClient.scope}</td>
	</tr>
</table>
</div>


<script type="text/javascript">

function refresh_access_token(key){
	$.messager.confirm("确认","是否刷新 ["+key+"]  ？！",function (f) {
            if(!f){
                return;
            }
            $.ajax({
                url:'/manage/wechat/auth/refreshData.html',
                data:{'refreshKey':key},
                type:'post',
                dataType:'json',
                success:function (res) {
                    console.log(res)
                    $.acooly.messager('提示', res.message,res.success ? 'success' : 'danger');
                }
            })
        })

}



</script>
