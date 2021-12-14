layui.use(['table','layer'],function(){
    var layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery,
        table = layui.table;
    /**
     * 用户列表展示
     */
    var tableIns = table.render({
        elem: '#userList',
        url : ctx+'/user/list',
        cellMinWidth : 95,
        page : true,
        height : "full-125",
        limits : [10,15,20,25],
        limit : 10,
        toolbar: "#toolbarDemo",
        id : "userListTable",
        cols : [[
            {type: "checkbox", fixed:"left", width:50},
            {field: "id", title:'编号',fixed:"true", width:80},
            {field: 'userName', title: '用户名', minWidth:50, align:"center"},
            {field: 'email', title: '用户邮箱', minWidth:100, align:'center'},
            {field: 'phone', title: '用户电话', minWidth:100, align:'center'},
            {field: 'trueName', title: '真实姓名', align:'center'},
            {field: 'createDate', title: '创建时间', align:'center',minWidth:150},
            {field: 'updateDate', title: '更新时间', align:'center',minWidth:150},
            {title: '操作', minWidth:150,
                templet:'#userListBar',fixed:"right",align:"center"}
        ]]
    });
//    实现搜索功能，
    $(".search_btn").click(function () {
        //这里以搜索为例
        tableIns.reload({
            where: {
                //设定异步数据接口的额外参数，任意设
                userName:$("input[name=userName]").val(),
                email:$("input[name=email]").val(),
                phone:$("input[phone=phone]").val()
            }
            ,page: {
                curr:1//重新从第一也开始
            }
        });
    });

    /*头部工具栏*/
    //头工具栏事件
    table.on('toolbar(users)', function(obj){
        var checkStatus = table.checkStatus(obj.config.id);
        switch(obj.event){
            case 'add':
                openAddOrUpdateUserDialog();
                break;
            case 'del':
                deleteUser(checkStatus.data);
                break;
        };
    });

    function deleteUser(datas) {
// 判断用户是否选择了要删除的记录
        if (datas.length == 0) {
            layer.msg("请选择要删除的记录！");
            return;
        }
// 询问用户是否确认删除
        layer.confirm("您确定要删除选中的记录吗？",{
            btn:["确认","取消"],
        },function (index) {
// 关闭确认框
            layer.close(index);
            //收集数据
            var ids=[];

            //遍历
            for (var x in datas){
                ids.push(datas[x].id);
            }
// 发送ajax请求，删除记录
            $.ajax({
                type:"post",
                url: ctx+"/user/delete",
                data:{"ids":ids.toString()},// 参数传递的是数组
                dataType:"json",
                success:function (result) {
                    if (result.code == 200) {
// 加载表格
                        tableIns.reload();
                    } else {
                        layer.msg(result.msg,{icon: 5});
                    }
                }
            });
        });
    }

    function openAddOrUpdateUserDialog(userId) {
        var title="<h3>用户模块-添加</h3>>";
        var url=ctx+"/user/addOrUpdatePage";

        // 通过id判断是添加操作还是修改操作
        if (userId) {
            // 如果id不为空，则为修改操作
            title = "<h2>用户管理 - 更新</h2>";
            url=url+"?id="+userId;
        }

        layui.layer.open({
            title:title,
            content:url,
            type:2,
            area:["500px","620px"],
            maxmin:true
        })
    }

    //监听行工具事件
    table.on("tool(users)", function(obj){
        var layEvent = obj.event;
// 监听编辑事件
        if(layEvent === "edit") {
            openAddOrUpdateUserDialog(obj.data.id);
        } else if(layEvent === "del") {
// 监听删除事件
            layer.confirm('确定是否删除？', {icon: 3, title: "用户管理"}, function
                (index) {
                $.post(ctx + "/user/delete",{ids:obj.data.id},function (data) {
                    if(data.code==200){
                        layer.msg("操作成功！");
                        tableIns.reload();
                    }else{
                        layer.msg(data.msg, {icon: 5});
                    }
                });
            });
        }
    });
});
