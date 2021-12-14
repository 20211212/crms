package com.yjxxt.crm.controller;

import com.yjxxt.crm.annotation.RequiredPermission;
import com.yjxxt.crm.base.BaseController;
import com.yjxxt.crm.base.ResultInfo;
import com.yjxxt.crm.bean.Role;
import com.yjxxt.crm.query.RoleQuery;
import com.yjxxt.crm.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@RequestMapping("role")
@Controller
public class RoleControlle extends BaseController{
    @Autowired
    private RoleService roleService;

    @RequestMapping("findRoles")
    @ResponseBody
    public List<Map<String,Object>> findRoles(Integer userId){
        return roleService.findRoles(userId);
    }

    @RequestMapping("index")
    public String index(){
        return "role/role";
    }

    @RequestMapping("list")
    @ResponseBody
    @RequiredPermission(code = "60")
    public Map<String,Object> list(RoleQuery roleQuery){
        return roleService.findRoleByParam(roleQuery);
    }

    @RequestMapping("addOrUpdateRolePage")
    public String addUserPage(Integer roleId, Model model){
        if(null !=roleId){
            Role role = roleService.selectByPrimaryKey(roleId);
            model.addAttribute("role",role);
        }
        return "role/add_update";
    }

    @RequestMapping("save")
    @ResponseBody
    public ResultInfo save(Role role){
        roleService.addRole(role);
        return success("角色添加成功");
    }

    @RequestMapping("update")
    @ResponseBody
    public ResultInfo update(Role role){
        roleService.changeRole(role);
        return success("角色修改成功");
    }

    @RequestMapping("delete")
    @ResponseBody
    public ResultInfo delete(Integer id){
        roleService.removeRoleById(id);
        return success("角色删除成功");
    }

    //授权
    @RequestMapping("toAddGrantPage")
    public String toAddGrantPage(Integer roleId, Model model){
        model.addAttribute("roleId",roleId);
        return "role/grant";
    }

    @RequestMapping("addGrant")
    @ResponseBody
    public ResultInfo addGrant(Integer roleId,Integer[] mids){
        roleService.addGrant(roleId,mids);
        return success("权限添加成功");
    }
}
