package com.sky.service.impl;

import com.sky.mapper.DishMapper;
import com.sky.mapper.OrdersMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.DataOverViewService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DataOverViewServiceImpl implements DataOverViewService {

    @Autowired
    private OrdersMapper ordersMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    @Override
    public BusinessDataVO getBusinessData() {
        Double turnover = ordersMapper.sumTurnoverByCompletedStatus();
        Integer validOrderCount = ordersMapper.countByStatus(5);
        Integer totalOrderCount = ordersMapper.countConfirmedAndDeliveryInProgress();
        Double orderCompletionRate = totalOrderCount > 0 ? (double) validOrderCount / totalOrderCount : 0.0;
        Double unitPrice = validOrderCount > 0 ? turnover / validOrderCount : 0.0;
        Integer newUsers = userMapper.countAll();

        return BusinessDataVO.builder()
                .turnover(turnover != null ? turnover : 0.0)
                .validOrderCount(validOrderCount != null ? validOrderCount : 0)
                .orderCompletionRate(orderCompletionRate)
                .unitPrice(unitPrice)
                .newUsers(newUsers != null ? newUsers : 0)
                .build();
    }

    @Override
    public OrderOverViewVO getOrdersOverView() {
        Integer waitingOrders = ordersMapper.countByStatus(2);
        Integer deliveredOrders = ordersMapper.countByStatus(3);
        Integer completedOrders = ordersMapper.countByStatus(5);
        Integer cancelledOrders = ordersMapper.countByStatus(6);
        Integer allOrders = waitingOrders + deliveredOrders + completedOrders + cancelledOrders;

        return OrderOverViewVO.builder()
                .waitingOrders(waitingOrders != null ? waitingOrders : 0)
                .deliveredOrders(deliveredOrders != null ? deliveredOrders : 0)
                .completedOrders(completedOrders != null ? completedOrders : 0)
                .cancelledOrders(cancelledOrders != null ? cancelledOrders : 0)
                .allOrders(allOrders)
                .build();
    }

    @Override
    public DishOverViewVO getDishOverView() {
        Integer sold = dishMapper.countByStatus(1);
        Integer discontinued = dishMapper.countByStatus(0);

        return DishOverViewVO.builder()
                .sold(sold != null ? sold : 0)
                .discontinued(discontinued != null ? discontinued : 0)
                .build();
    }

    @Override
    public SetmealOverViewVO getSetmealOverView() {
        Integer sold = setmealMapper.countByStatus(1);
        Integer discontinued = setmealMapper.countByStatus(0);

        return SetmealOverViewVO.builder()
                .sold(sold != null ? sold : 0)
                .discontinued(discontinued != null ? discontinued : 0)
                .build();
    }
}
