layui.use(['form','jquery','jquery_cookie','layer'], function () {
    var form = layui.form,
        layer = layui.layer,
        $ = layui.jquery,
        $ = layui.jquery_cookie($);

    //监听提交
    form.on('submit(login)', function(data){
       var fieldData=data.field;
       if (fieldData.username=='undefined' || fieldData.username.trim()==''){
           layer.msg("用户名不能为空");
           return false;
       }
       if (fieldData.password=='undefined' || fieldData.password.trim()==''){
           layer.msg("密码不能为空");
           return false;
       }
       //发送ajax
        $.ajax({
            type:"post",
            url:ctx+"/user/login",
            data:{
                "userName":fieldData.username,
                "userPwd":fieldData.password
            },
            dataType:"json",
            success:function (msg){
                //resultInfo
                if (msg.code==200){
                    layer.msg("登录成功！",function () {
                        // 将用户信息存到cookie中
                        var result = msg.result;
                        $.cookie("userIdStr",result.userIdStr);
                        $.cookie("userName",result.userName);
                        $.cookie("trueName",result.trueName);

                        // 如果用户选择"记住我"，则设置cookie的有效期为7天
                        if($("input[type='checkbox']").is(":checked")){
                            $.cookie("userIdStr",result.userIdStr,{expires:7});
                            $.cookie("userName",result.userName,{expires:7});
                            $.cookie("trueName",result.trueName,{expires:7});
                        }
                        // 登录成功后，跳转到首页
                        window.location.href = ctx+"/main";
                    });
                }else{
                    //失败提示
                    layer.msg(msg.msg);
                }
            }
        })
        return false;
    });

});