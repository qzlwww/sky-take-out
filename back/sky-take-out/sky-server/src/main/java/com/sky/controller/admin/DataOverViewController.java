package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.DataOverViewService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/overview")
@Slf4j
@Api(tags = "数据概览相关接口")
public class DataOverViewController {

    @Autowired
    private DataOverViewService dataOverViewService;

    @GetMapping("/businessData")
    @ApiOperation("业务数据概览")
    public Result<BusinessDataVO> businessData() {
        BusinessDataVO businessDataVO = dataOverViewService.getBusinessData();
        return Result.success(businessDataVO);
    }

    @GetMapping("/orderData")
    @ApiOperation("订单数据概览")
    public Result<OrderOverViewVO> orderData() {
        OrderOverViewVO orderOverViewVO = dataOverViewService.getOrdersOverView();
        return Result.success(orderOverViewVO);
    }

    @GetMapping("/dishData")
    @ApiOperation("菜品数据概览")
    public Result<DishOverViewVO> dishData() {
        DishOverViewVO dishOverViewVO = dataOverViewService.getDishOverView();
        return Result.success(dishOverViewVO);
    }

    @GetMapping("/setmealData")
    @ApiOperation("套餐数据概览")
    public Result<SetmealOverViewVO> setmealData() {
        SetmealOverViewVO setmealOverViewVO = dataOverViewService.getSetmealOverView();
        return Result.success(setmealOverViewVO);
    }
}
