package com.yjxxt.crm.service;

import com.yjxxt.crm.base.BaseService;
import com.yjxxt.crm.bean.Module;
import com.yjxxt.crm.dto.TreeDto;
import com.yjxxt.crm.mapper.ModuleMapper;
import com.yjxxt.crm.mapper.PermissionMapper;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ModuleService extends BaseService<Module,Integer> {
    @Resource
    public ModuleMapper moduleMapper;
    @Resource
    public PermissionMapper permissionMapper;

    /**
     * 查询所有的资源信息
     * **/
    public List<TreeDto> findModules(){
        return moduleMapper.selectModules();
    }

    public List<TreeDto> findModulesByRoleId(Integer roleId){
        //获取所有资源信息
        List<TreeDto> tlist = moduleMapper.selectModules();
        //获取当前角色拥有的资源信息
        List<Integer> roleHasModules=permissionMapper.selectModuleByRoleId(roleId);
        //判断
        for (TreeDto treeDto:tlist){
            if (roleHasModules.contains(treeDto.getId())){
                treeDto.setChecked(true);
            }
        }
        //判断比较，checked=true;
        return tlist;
    }

    public Map<String, Object> queryModules() {
        //准备数据
        Map<String,Object> map = new HashMap<>();
        //查询搜有资源
        List<Module> mlsit = moduleMapper.selectAllModules();
        //存储
        map.put("code",0);
        map.put("msg","success");
        map.put("count",mlsit.size());
        map.put("data",mlsit);
        //返回目标
        return map;
    }

}
