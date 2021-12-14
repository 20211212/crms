layui.use(['form', 'layer'], function () {
    var form = layui.form,
        layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery;

    form.on("submit(addOrUpdateSaleChance)",function (obj){

        console.log(obj.field);

        //判断是添加还是修改，id==null为添加，id!=null为修改

        var url = ctx+"/sale_chance/save";
        //判断当前页面的隐藏域是否有Id,有就修改，没有就添加
        if ($("input[name=id]").val()){
            url=ctx+"/sale_chance/update"
        }
        //发送ajax
        $.ajax({
            type:"post",
            url:url,
            data:obj.field,
            dataType:"json",
            success:function (obj){
                if (obj.code==200){
                    //layer.msg("添加成功")
                    //刷新页面
                    window.parent.location.reload();
                }else {
                    layer.msg(obj.msg,{icon:5 });
                }
            }
        })
        //取消跳转
        return false;
    });

//    取消按钮
    $("#closeBtn").click(function (){
        //获取当前弹击层的索引
        var idx=parent.layer.getFrameIndex(window.name);
        //根据索引关闭
        parent.layer.close(idx);
    })

    // 如果是修改操作，判断当前修改记录的指派人的值
    var assignMan = $("input[name='man']").val();
    $.ajax({
        type:"post",
        url:ctx+"/user/sales",
        dataType:"json",
        success:function (data){
            for (var x in data){
                if (assignMan == data[x].id) {
                    $("#assignMan").append("<option selected value='" + data[x].id + "'>" + data[x].uname + "</option>");
                }else {
                    $("#assignMan").append("<option value='" + data[x].id + "'>" + data[x].uname + "</option>");
                }
            }
            // 重新渲染下拉框内容
            layui.form.render("select");
        }
    });
});