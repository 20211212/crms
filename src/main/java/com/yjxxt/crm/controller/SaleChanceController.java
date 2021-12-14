package com.yjxxt.crm.controller;

import com.yjxxt.crm.annotation.RequiredPermission;
import com.yjxxt.crm.base.BaseController;
import com.yjxxt.crm.base.ResultInfo;
import com.yjxxt.crm.bean.SaleChance;
import com.yjxxt.crm.query.SaleChanceQuery;
import com.yjxxt.crm.service.SaleChanceService;
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
@RequestMapping("sale_chance")
public class SaleChanceController extends BaseController {

    @Autowired
    private SaleChanceService saleChanceService;
    @Autowired
    private UserService userService;

    @RequestMapping("index")
    private String index(){
        return "saleChance/sale_chance";
    }

    @RequestMapping("addOrUpdateDialog")
    public String addOrUpdateDialog(Integer id, Model model){
        // 如果id不为空，表示是修改操作，修改操作需要查询被修改的数据
        if (id!=null){
            // 通过主键查询营销机会数据
            SaleChance saleChance = saleChanceService.selectByPrimaryKey(id);
            // 将数据存到作用域中
            model.addAttribute("saleChance",saleChance);
        }
        return "saleChance/add_update";
    }

    @RequestMapping("list")
    @ResponseBody
    @RequiredPermission(code = "10")
    public Map<String,Object> sayList(SaleChanceQuery saleChanceQuery,Integer flag, HttpServletRequest request){
        if (null != flag && flag == 1) {
            // 获取当前登录用户的ID
            Integer userId = LoginUserUtil.releaseUserIdFromCookie(request);
            saleChanceQuery.setAssignMan(userId);
        }

        //调用方法获取数据
        Map<String,Object> map=saleChanceService.querySaleChanceByParams(saleChanceQuery);
        //返回
        return map;
    }

    @RequestMapping("save")
    @ResponseBody
    public ResultInfo save(HttpServletRequest request,SaleChance saleChance){
        //获取登录用户信息
        int userId = LoginUserUtil.releaseUserIdFromCookie(request);
        String trueName = userService.selectByPrimaryKey(userId).getTrueName();
        //创建人
        saleChance.setCreateMan(trueName);
        //添加操作
        saleChanceService.addSaleChance(saleChance);
        //返回目标对象
        return success("添加成功");
    }

    @RequestMapping("update")
    @ResponseBody
    public ResultInfo update(SaleChance saleChance){
        //添加操作
        saleChanceService.changeSaleChance(saleChance);
        //返回目标对象
        return success("修改成功");
    }

    @RequestMapping("dels")
    @ResponseBody
    public ResultInfo deletes(Integer [] ids){
        //删除操作
        saleChanceService.removeSaleChanceIds(ids);
        //返回目标对象
        return success("删除成功");
    }


}
