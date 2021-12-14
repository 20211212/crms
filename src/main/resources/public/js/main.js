layui.use(['element', 'layer', 'layuimini','jquery','jquery_cookie'], function () {
    var $ = layui.jquery,
        layer = layui.layer,
        $ = layui.jquery_cookie($);

    // 菜单初始化
    $('#layuiminiHomeTabIframe').html('<iframe width="100%" height="100%" frameborder="0"  src="welcome"></iframe>')
    layuimini.initTab();

    $(".login-out").click(function (){
        //清空用户信息
        $.removeCookie("userIdStr",{damain:"localhost",path:"/crm"})
        $.removeCookie("userName",{damain:"localhost",path:"/crm"})
        $.removeCookie("trueName",{damain:"localhost",path:"/crm"})
        //跳转页面
        window.parent.location.href=ctx+"/index";
    })

});