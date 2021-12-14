package com.yjxxt.crm.controller;

import com.yjxxt.crm.annotation.RequiredPermission;
import com.yjxxt.crm.base.BaseController;
import com.yjxxt.crm.base.ResultInfo;
import com.yjxxt.crm.bean.User;
import com.yjxxt.crm.model.UserModel;
import com.yjxxt.crm.query.UserQuery;
import com.yjxxt.crm.service.UserService;
import com.yjxxt.crm.utils.LoginUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("user")
public class UserController extends BaseController {
    @Autowired
    private UserService service;

    @RequestMapping("toPasswordPage")
    public String updatePwd(){
        return "user/password";
    };

    @RequestMapping("toSettingPage")
    public String setting(HttpServletRequest req){
        //获取用户Id
        int userId = LoginUserUtil.releaseUserIdFromCookie(req);
        //调用方法
        User user = service.selectByPrimaryKey(userId);
        //存储
        req.setAttribute("user",user);
        //转发
        return "user/setting";
    }

    @RequestMapping("login")
    @ResponseBody
    public ResultInfo say(String userName,String userPwd){
            ResultInfo resultInfo = new ResultInfo();
            UserModel userModel = service.userLogin(userName,userPwd);
            resultInfo.setResult(userModel);
            return resultInfo;
    }

    @RequestMapping("setting")
    @ResponseBody
    public ResultInfo sayUpdate(User user){
        ResultInfo resultInfo = new ResultInfo();
        //修改信息
        service.updateByPrimaryKeySelective(user);
        //返回
        return resultInfo;

    }

    @RequestMapping("save")
    @ResponseBody
    public ResultInfo save(User user){
        //用户添加
        service.addUser(user);
        //返回
        return success("添加成功");
    }

    @RequestMapping("update")
    @ResponseBody
    public ResultInfo update(User user){
        //用户添加
        service.chanceUser(user);
        //返回
        return success("修改成功");
    }

    @RequestMapping("delete")
    @ResponseBody
    public ResultInfo deleteUser(Integer[] ids){
        service.deleteUserByIds(ids);
        return success("用户记录删除成功");
    }

    /**
     * 进入用户添加或更新页面
     * @param id
     * @param model
     * @return
     */
    @RequestMapping("addOrUpdateUserPage")
    public String addUserPage(Integer id, Model model){
        if(null != id){
            model.addAttribute("user",service.selectByPrimaryKey(id));
        }
        return "user/add_update";
    }


    @RequestMapping("updatePwd")
    @ResponseBody
    public ResultInfo updatePwd(HttpServletRequest req,String oldPassword,String newPassword,String confirmPwd){
        ResultInfo resultInfo = new ResultInfo();
        //获取Cokid中的userId
        int userId = LoginUserUtil.releaseUserIdFromCookie(req);
        //修改密码操作
            service.changeUserPwd(userId,oldPassword,newPassword,confirmPwd);
        return resultInfo;

    }

    @RequestMapping("sales")
    @ResponseBody
    public List<Map<String,Object>> findSales(){
        List<Map<String,Object>> list = service.querySales();
        return list;
    }

    @RequestMapping("list")
    @ResponseBody
    public Map<String,Object> list(UserQuery userQuery){
        return service.findUserByParams(userQuery);
    }

    @RequestMapping("index")
    @RequiredPermission(code = "60")
    public String index(){
        return "user/user";
    }

    @RequestMapping("addOrUpdatePage")
    public String addOrUpdatePage(Integer id,Model model){
        User user = service.selectByPrimaryKey(id);
        model.addAttribute("user",user);
        return "user/add_update";
    }

}
