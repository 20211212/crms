package com.yjxxt.crm.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yjxxt.crm.base.BaseService;
import com.yjxxt.crm.bean.User;
import com.yjxxt.crm.bean.UserRole;
import com.yjxxt.crm.mapper.UserMapper;
import com.yjxxt.crm.mapper.UserRoleMapper;
import com.yjxxt.crm.model.UserModel;
import com.yjxxt.crm.query.UserQuery;
import com.yjxxt.crm.utils.AssertUtil;
import com.yjxxt.crm.utils.Md5Util;
import com.yjxxt.crm.utils.UserIDBase64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

@Service
public class UserService extends BaseService<User,Integer> {
    @Resource
    private UserMapper mapper;

    @Resource
    private UserRoleMapper userRoleMapper;

    /**
     * 登录
     * 校验
     * **/
    public UserModel userLogin(String userName,String userPwd){
        checkUserLoginParam(userName,userPwd);
        //用户是否存在
        User temp = mapper.selectUserByName(userName);
        AssertUtil.isTrue(temp==null,"用户不存在");
        //用户密码是否正确
        checkUserPwd(userPwd,temp.getUserPwd());
        //构建返回对象
        return  builderUserInfor(temp);
    }

    /**
     * 用户
     * 加密
     * **/
    private UserModel builderUserInfor(User user) {
        //构建目标对象
        UserModel userModel = new UserModel();
        //加密
        userModel.setUserIdStr(UserIDBase64.encoderUserID(user.getId()));
        userModel.setUserName(user.getUserName());
        userModel.setTrueName(user.getTrueName());
        //返回目标对象
        return userModel;
    }

    /**
     * 输入
     * 校验
     * **/
    private void checkUserLoginParam(String userName, String userPwd) {
        //用户非空
        AssertUtil.isTrue(userName=="" || userName.trim()=="","用户名不能为空");
        //密码非空
        AssertUtil.isTrue(userPwd=="" || userPwd.trim()=="","用户密码不能为空");
    }

    /**
     * 密码
     * 加密
     * **/
    private void checkUserPwd(String userPwd,String userPwd1){
        //对输入的密码加密
        userPwd= Md5Util.encode(userPwd);
        //加密的密码和数据中的密码比对
        AssertUtil.isTrue(!userPwd.equals(userPwd1),"用户密码不正确");
    }

    /**
     * 密码
     * 修改
     * **/
    public void changeUserPwd(Integer userId,String oldpassword,String newPassword,String confirmPwd){
        //用户登录了修改，userId
        User user = mapper.selectByPrimaryKey(userId);

        checkPasswordParams(user,oldpassword,newPassword,confirmPwd);
        user.setUserPwd(Md5Util.encode(newPassword));
        //确认密码修改是否成功
        AssertUtil.isTrue(mapper.updateByPrimaryKeySelective(user)<1,"修改失败");
    }

    /**
     * 修改密码
     * 校验
     * **/
    private void checkPasswordParams(User user, String oldpassword, String newPassword, String confirmPwd) {
        System.out.println(user);
        AssertUtil.isTrue(user==null,"用户没找到");
        //原始密码非空
        AssertUtil.isTrue(StringUtils.isBlank(oldpassword),"请输入原始密码");
        //新密码不能和原密码一致
        AssertUtil.isTrue(!(user.getUserPwd().equals(Md5Util.encode(oldpassword))),"原始密码不正确");
        //新密码非空
        AssertUtil.isTrue(StringUtils.isBlank(newPassword),"新密码不能为空");
        //确认新密码和原始密码是否一致
        AssertUtil.isTrue(newPassword.equals((oldpassword)),"新密码和原始密码一致");
        //确认密码非空
        AssertUtil.isTrue(StringUtils.isBlank(confirmPwd),"确认密码不能为空");
        //确认密码和新密码一致
        AssertUtil.isTrue(!(confirmPwd.equals(newPassword)),"确认密码与新密码不一致");
    }

    public List<Map<String,Object>> querySales(){
            return mapper.selectSales();
    }

    /**
     * 实例化  分页
     * **/
    public Map<String,Object> findUserByParams(UserQuery userQuery){
        //实例化map
        Map<String,Object> map=new HashMap<String,Object>();

        //初始化分页单位
        PageHelper.startPage(userQuery.getPage(),userQuery.getLimit());
        //开始分页
        PageInfo<User> plist=new PageInfo<User>(mapper.selectByParams(userQuery));
        //准备数据
        map.put("code",0);
        map.put("msg","success");
        map.put("count",plist.getTotal());
        map.put("data",plist.getList());
        //返回目标map
        return map;
    }

    /**
     * 用户
     * 添加
     * **/
    @Transactional(propagation = Propagation.REQUIRED)
    public void  addUser(User user){
        //验证
        checkUser(user.getUserName(),user.getEmail(),user.getPhone());
        //用户名唯一
        User temp2 = mapper.selectUserByName(user.getUserName());
        AssertUtil.isTrue(temp2!=null && !temp2.getId().equals(user.getId()),"用户名已经存在");
        //设定默认值
        user.setIsValid(1);
        user.setCreateDate(new Date());
        user.setUpdateDate(new Date());
        //密码加密
        user.setUserPwd(Md5Util.encode("123456"));
        //验证是否成功
        AssertUtil.isTrue(insertHasKey(user)<1,"添加失败");
        //****
        relaionUserRole(user.getId(),user.getRoleIds());
    }

    /**
     * 用户
     * 输入验证
     * **/
    private void checkUser(String userName, String email, String phone) {

        AssertUtil.isTrue(StringUtils.isBlank(userName),"用户名不能为空");
        AssertUtil.isTrue(StringUtils.isBlank(email),"邮箱不能为空");
        AssertUtil.isTrue(StringUtils.isBlank(phone),"手机号不能为空");
        AssertUtil.isTrue(StringUtils.isBlank(phone),"请输入合法的手机号");
    }

    /**
     * 用户
     * 修改
     * **/
    @Transactional(propagation = Propagation.REQUIRED)
    public void chanceUser(User user){
        //根据ID获取用户信息
        User temp = mapper.selectByPrimaryKey(user.getId());
        //判断
        AssertUtil.isTrue(temp==null,"待修改的记录不存在");
        //确认参数
        checkUser(user.getUserName(),user.getEmail(),user.getPhone());
        //设定默认值
        user.setUpdateDate(new Date());
        //判断修改是否成功
        AssertUtil.isTrue(updateByPrimaryKeySelective(user)<1,"修改失败");
        //
        relaionUserRole(user.getId(),user.getRoleIds());

    }

    /**
     * 用户
     * 删除
     * **/
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteUserByIds(Integer[] ids) {
        //验证
        AssertUtil.isTrue(ids == null || ids.length == 0, "请选择要删除的数据");
        //遍历
        for (Integer userId:ids) {
            //统计原来有多少个角色
            int count=userRoleMapper.countUserRoleNum(userId);
            //删除原来的角色
            if (count>0){
                AssertUtil.isTrue(userRoleMapper.deleteUserRoleByuserId(userId)!=count,"用户角色删除失败");
            }
        }
        //删除是否成功
        AssertUtil.isTrue(mapper.deleteBatch(ids) != ids.length, "批量删除失败");
    }

    /**
     *
     * 操作中间表角色
     *
     * **/
    private void relaionUserRole(Integer userId, String roleIds) {
        //准备集合存储对象
        List<UserRole> urlist=new ArrayList<UserRole>();
        //userId,roleId;
        AssertUtil.isTrue(StringUtils.isBlank(roleIds),"请选择角色信息");
        //统计原来有多少个角色
        int count=userRoleMapper.countUserRoleNum(userId);
        //删除原来的角色
        if (count>0){
            AssertUtil.isTrue(userRoleMapper.deleteUserRoleByuserId(userId)!=count,"用户角色删除失败");
        }
        String[] RoleStrId = roleIds.split(",");
        //遍历
        for (String rid:RoleStrId){
            //准备对象
            UserRole userRole=new UserRole();
            userRole.setUserId(userId);
            userRole.setRoleId(Integer.parseInt(rid));
            userRole.setCreateDate(new Date());
            userRole.setUpdateDate(new Date());
            //存储集合
            urlist.add(userRole);
        }
//
        // 批量添加
        AssertUtil.isTrue(userRoleMapper.insertBatch(urlist)!=urlist.size(),"");

//        int count = userRoleMapper.countUserRoleNum(userId);
//        if (count > 0) {
//            AssertUtil.isTrue(userRoleMapper.deleteUserRoleByuserId(userId) != count,
//                    "用户角色分配失败!");
//        }
//        if (StringUtils.isNotBlank(roleIds)) {
//        //重新添加新的角色
//            List<UserRole> userRoles = new ArrayList<UserRole>();
//            for (String s : roleIds.split(",")) {
//                UserRole userRole = new UserRole();
//                userRole.setUserId(userId);
//                userRole.setRoleId(Integer.parseInt(s));
//                userRole.setCreateDate(new Date());
//                userRole.setUpdateDate(new Date());
//                userRoles.add(userRole);
//            }
//            AssertUtil.isTrue(userRoleMapper.insertBatch(userRoles) < userRoles.size(), "用户角色分配失败!");
//        }

    }
}
