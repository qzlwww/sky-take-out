package com.sky.service.impl;

import com.sky.entity.User;
import com.sky.mapper.OrdersMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrdersMapper ordersMapper;
    @Autowired
    private UserMapper userMapper;

    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        List<Double> turnoverList = new ArrayList<>();

        LocalDate date = begin;
        while (!date.isAfter(end)) {
            dateList.add(date);

            LocalDateTime dateTimeBegin = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime dateTimeEnd = LocalDateTime.of(date, LocalTime.MAX);

            Double turnover = ordersMapper.sumTurnoverByStatusAndTime(dateTimeBegin, dateTimeEnd);
            turnoverList.add(turnover != null ? turnover : 0.0);

            date = date.plusDays(1);
        }

        return TurnoverReportVO.builder()
                .dateList(String.join(",", dateList.stream().map(String::valueOf).collect(Collectors.toList())))
                .turnoverList(turnoverList.stream().map(String::valueOf).reduce((a, b) -> a + "," + b).orElse(""))
                .build();
    }

    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        List<Integer> newUserList = new ArrayList<>();
        List<Integer> totalUserList = new ArrayList<>();

        int totalUsers = 0;
        LocalDate date = begin;
        while (!date.isAfter(end)) {
            dateList.add(date);

            LocalDateTime dateTimeBegin = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime dateTimeEnd = LocalDateTime.of(date, LocalTime.MAX);

            Integer newUsers = userMapper.countByCreateTime(dateTimeBegin, dateTimeEnd);
            newUsers = newUsers != null ? newUsers : 0;
            totalUsers += newUsers;
            newUserList.add(newUsers);
            totalUserList.add(totalUsers);

            date = date.plusDays(1);
        }

        return UserReportVO.builder()
                .dateList(String.join(",", dateList.stream().map(String::valueOf).collect(Collectors.toList())))
                .newUserList(newUserList.stream().map(String::valueOf).reduce((a, b) -> a + "," + b).orElse(""))
                .totalUserList(totalUserList.stream().map(String::valueOf).reduce((a, b) -> a + "," + b).orElse(""))
                .build();
    }

    @Override
    public OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        List<Integer> orderCountList = new ArrayList<>();
        List<Integer> validOrderCountList = new ArrayList<>();

        int totalOrderCount = 0;
        int validOrderCount = 0;

        LocalDate date = begin;
        while (!date.isAfter(end)) {
            dateList.add(date);

            LocalDateTime dateTimeBegin = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime dateTimeEnd = LocalDateTime.of(date, LocalTime.MAX);

            Integer orderCount = ordersMapper.countTotalOrders(dateTimeBegin, dateTimeEnd);
            Integer validCount = ordersMapper.countCompletedOrders(dateTimeBegin, dateTimeEnd);

            orderCount = orderCount != null ? orderCount : 0;
            validCount = validCount != null ? validCount : 0;

            totalOrderCount += orderCount;
            validOrderCount += validCount;

            orderCountList.add(orderCount);
            validOrderCountList.add(validCount);

            date = date.plusDays(1);
        }

        Double orderCompletionRate = totalOrderCount > 0 ? (double) validOrderCount / totalOrderCount * 100 : 0.0;

        return OrderReportVO.builder()
                .dateList(String.join(",", dateList.stream().map(String::valueOf).collect(Collectors.toList())))
                .orderCountList(orderCountList.stream().map(String::valueOf).reduce((a, b) -> a + "," + b).orElse(""))
                .validOrderCountList(validOrderCountList.stream().map(String::valueOf).reduce((a, b) -> a + "," + b).orElse(""))
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .build();
    }

    @Override
    public SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end) {
        LocalDateTime dateTimeBegin = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime dateTimeEnd = LocalDateTime.of(end, LocalTime.MAX);

        return ordersMapper.getSalesTop10(dateTimeBegin, dateTimeEnd).stream()
                .collect(
                        () -> SalesTop10ReportVO.builder().nameList("").numberList("").build(),
                        (vo, item) -> {
                            if (!vo.getNameList().isEmpty()) {
                                vo.setNameList(vo.getNameList() + ",");
                                vo.setNumberList(vo.getNumberList() + ",");
                            }
                            vo.setNameList(vo.getNameList() + item.getName());
                            vo.setNumberList(vo.getNumberList() + item.getNumber());
                        },
                        (vo1, vo2) -> {
                            if (!vo2.getNameList().isEmpty()) {
                                if (!vo1.getNameList().isEmpty()) {
                                    vo1.setNameList(vo1.getNameList() + ",");
                                    vo1.setNumberList(vo1.getNumberList() + ",");
                                }
                                vo1.setNameList(vo1.getNameList() + vo2.getNameList());
                                vo1.setNumberList(vo1.getNumberList() + vo2.getNumberList());
                            }
                        }
                );
    }
}
