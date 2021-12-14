package com.yjxxt.crm.controller;

import com.yjxxt.crm.annotation.RequiredPermission;
import com.yjxxt.crm.base.BaseController;
import com.yjxxt.crm.base.BaseQuery;
import com.yjxxt.crm.bean.SaleChance;
import com.yjxxt.crm.service.SaleChanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("cus_dev_plan")
public class CusDevPlanController extends BaseController {

    @Autowired
    private SaleChanceService saleChanceService;

    @RequestMapping("index")
    @RequiredPermission(code = "10")
    public String index( Model model,Integer sid) {

        // 通过id查询营销机会数据
        SaleChance saleChance = saleChanceService.selectByPrimaryKey(sid);
// 将数据存到作用域中
        model.addAttribute("saleChance", saleChance);

        return "cusDevPlan/cus_dev_plan";
    }
}
