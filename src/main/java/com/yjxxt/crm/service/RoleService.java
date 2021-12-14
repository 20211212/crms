package com.yjxxt.crm.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yjxxt.crm.base.BaseService;
import com.yjxxt.crm.bean.Permission;
import com.yjxxt.crm.bean.Role;

import com.yjxxt.crm.mapper.ModuleMapper;
import com.yjxxt.crm.mapper.PermissionMapper;
import com.yjxxt.crm.mapper.RoleMapper;
import com.yjxxt.crm.query.RoleQuery;
import com.yjxxt.crm.utils.AssertUtil;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

@Service
public class RoleService extends BaseService<Role,Integer> {
    @Resource
    private RoleMapper roleMapper;

    @Autowired(required = false)
    private PermissionMapper permissionMapper;

    @Autowired(required = false)
    private ModuleMapper moduleMapper;

    /**
     * 查询所有角色信息
     * **/
    public List<Map<String, Object>> findRoles(Integer userId){
        return roleMapper.quseryAllRoles(userId);
    }

    public Map<String,Object> findRoleByParam(RoleQuery roleQuery){
        //实例化对象
        Map<String,Object> map=new HashMap<String,Object>();
        //开启分页
        PageHelper.startPage(roleQuery.getPage(),roleQuery.getLimit());
        PageInfo<Role> rlist=new PageInfo<>(selectByParams(roleQuery));
        //准备数据
        map.put("code",0);
        map.put("msg","success");
        map.put("count",rlist.getTotal());
        map.put("data",rlist.getList());
        //返回目标对象
        return map;
    }

    /**角色添加**/
    @Transactional(propagation = Propagation.REQUIRED)
    public void addRole(Role role){
        AssertUtil.isTrue(StringUtils.isBlank(role.getRoleName()),"请输入角色名");
        //用户名唯一
        Role temp = roleMapper.queryRoleByRoleName(role.getRoleName());
        AssertUtil.isTrue(temp!=null,"角色名已经存在");
        //设定默认值
        role.setIsValid(1);
        role.setCreateDate(new Date());
        role.setUpdateDate(new Date());
        //验证是否成功
        AssertUtil.isTrue(insertHasKey(role)<1,"添加失败");
    }

    /**角色修改**/
    public void changeRole(Role role){
        //用户名唯一
        Role temp = roleMapper.queryRoleByRoleName(role.getRoleName());
        AssertUtil.isTrue(temp==null,"角色名不存在");
        //设定默认值
        role.setUpdateDate(new Date());
        //用户名唯一
        Role temp2 = roleMapper.queryRoleByRoleName(role.getRoleName());
        AssertUtil.isTrue(temp2!=null && !temp2.getId().equals(role.getId()),"角色名已经存在");
        //验证是否成功
        AssertUtil.isTrue(updateByPrimaryKeySelective(role)<1,"修改失败");
    }

    /**
     * 角色管理删除
     * **/
    @Transactional(propagation = Propagation.REQUIRED)
    public void removeRoleById(Integer roleId) {
        //验证
        Role temp =selectByPrimaryKey(roleId);
        AssertUtil.isTrue(null==roleId||null==temp,"待删除的记录不存在!");
        //设定默认值
        temp.setIsValid(0);
        //判断是否成功
        AssertUtil.isTrue(roleMapper.updateByPrimaryKeySelective(temp)<1,"角色记录删除失败!");
    }

    /**
     * 授权
     * **/
    @Transactional(propagation = Propagation.REQUIRED)
    public void addGrant(Integer roleId, Integer[] mids) {
        AssertUtil.isTrue(roleId==null || null==selectByPrimaryKey(roleId),"请选择角色");
        //t_permission roleId,mid
        //统计当前角色的资源数量
        int count=permissionMapper.countRoleModulesByRoleId(roleId);
        if (count>0){
            //统计当前角色的信息
            AssertUtil.isTrue(permissionMapper.deleteRoleModuleByRoleId(roleId)!=count,"角色资源分配失败");
        }
        List<Permission> plist=new ArrayList<Permission>();
        if(null !=mids && mids.length>0) {
            for (Integer mid:mids){
                //实例化对象
                Permission permission=new Permission();
                permission.setRoleId(roleId);
                permission.setModuleId(mid);
                //权限码
                permission.setAclValue(moduleMapper.selectByPrimaryKey(mid).getOptValue());
                permission.setUpdateDate(new Date());
                permission.setCreateDate(new Date());
                plist.add(permission);
            }
        }
        AssertUtil.isTrue(permissionMapper.insertBatch(plist)!=plist.size(),"授权失败");
    }

}


