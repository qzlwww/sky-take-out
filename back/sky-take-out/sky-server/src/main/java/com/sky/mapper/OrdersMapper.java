package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.GoodsSalesDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OrdersMapper {

    void insert(Orders orders);

    Page<Orders> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    @Select("select * from orders where id = #{id}")
    Orders getById(Long id);

    @Select("select * from orders where number = #{number}")
    Orders getByNumber(String number);

    void update(Orders orders);

    @Update("update orders set status = #{status} where id = #{id}")
    void updateStatus(Orders orders);

    Integer countByUserIdAndStatus(Long userId, Integer status);

    @Select("select * from orders where user_id = #{userId} and status = #{status} order by order_time desc")
    List<Orders> getByUserIdAndStatus(Long userId, Integer status);

    Integer countByStatus(Integer status);

    Integer countConfirmedAndDeliveryInProgress();

    @Select("select sum(amount) from orders where status = 5 and order_time between #{begin} and #{end}")
    Double sumTurnoverByStatusAndTime(LocalDateTime begin, LocalDateTime end);

    @Select("select sum(amount) from orders where status = 5")
    Double sumTurnoverByCompletedStatus();

    @Select("select count(*) from orders where status = 5 and order_time between #{begin} and #{end}")
    Integer countCompletedOrders(LocalDateTime begin, LocalDateTime end);

    @Select("select count(*) from orders where order_time between #{begin} and #{end}")
    Integer countTotalOrders(LocalDateTime begin, LocalDateTime end);

    List<GoodsSalesDTO> getSalesTop10(LocalDateTime begin, LocalDateTime end);

    List<Orders> conditionQuery(OrdersPageQueryDTO ordersPageQueryDTO);
}
