package com.yjxxt.crm.mapper;

import com.yjxxt.crm.base.BaseMapper;
import com.yjxxt.crm.bean.UserRole;
import org.apache.ibatis.annotations.Param;

public interface UserRoleMapper extends BaseMapper<UserRole,Integer> {
    int countUserRoleNum(@Param("userId") Integer userId);

    int deleteUserRoleByuserId(@Param("userId")Integer userId);
}