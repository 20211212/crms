layui.use(['form','jquery','jquery_cookie','layer'], function () {
    var form = layui.form,
        layer = layui.layer,
        $ = layui.jquery,
        $ = layui.jquery_cookie($);

    //监听提交
    form.on('submit(saveBtn)', function(data){
        var fieldData=data.field;

        //发送ajax
        $.ajax({
            type:"post",
            url:ctx+"/user/setting",
            data:{
                userName:data.field.userName,
                phone:data.field.phone,
                email:data.field.email,
                trueName:data.field.trueName,
                id:data.field.id
            },
            dataType:"json",
            success:function (data){
                if (data.code=200){
                    layer.msg("修改成功",function (){
                        //清空用户信息
                        $.removeCookie("userIdStr",{damain:"localhost",path:"/crm"})
                        $.removeCookie("userName",{damain:"localhost",path:"/crm"})
                        $.removeCookie("trueName",{damain:"localhost",path:"/crm"})
                        //跳转页面
                        window.parent.location.href=ctx+"/index";
                    })
                }else {
                    //修改失败的提示
                    layer.msg(data.msg)
                }
            }
        })
        return false;
    });
});