package com.ldp.seckill.controller;

import com.ldp.seckill.VO.OrderDetailVo;
import com.ldp.seckill.VO.RespBean;
import com.ldp.seckill.VO.RespBeanEnum;
import com.ldp.seckill.pojo.User;
import com.ldp.seckill.service.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private IOrderService orderService;

    @RequestMapping("/detail")
    @ResponseBody
    public RespBean orderDetail(User user, Long orderId) {
        if (user == null) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        OrderDetailVo detail = orderService.detail(orderId);
        return RespBean.success(detail);
    }
}
