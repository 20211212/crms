layui.use(['form','jquery','jquery_cookie','layer'], function () {
    var form = layui.form,
        layer = layui.layer,
        $ = layui.jquery,
        $ = layui.jquery_cookie($);

    //监听提交
    form.on('submit(saveBtn)', function(data){
        // layer.alert(JSON.stringify(data.field), {
        //     title: '最终的提交信息'
        // })
        var fieldData=data.field;

        //发送ajax
        $.ajax({
            type:"post",
            url:ctx+"/user/updatePwd",
            data:{
                "oldPassword":fieldData.old_password,
                "newPassword":fieldData.new_password,
                "confirmPwd":fieldData.again_password
            },
            dataType:"json",
            success:function (data){
                if (data.code=200){
                    layer.msg("修改密码成功，系统三秒后保存",function (){
                        //清空用户信息
                        $.removeCookie("userIdStr",{damain:"localhost",path:"/crm"})
                        $.removeCookie("userName",{damain:"localhost",path:"/crm"})
                        $.removeCookie("trueName",{damain:"localhost",path:"/crm"})
                        //跳转页面
                        window.parent.location.href=ctx+"/index";
                    })
                }else {
                    //登录失败的提示
                    layer.msg(data.msg)
                }
            }
        })
        return false;
    });
});