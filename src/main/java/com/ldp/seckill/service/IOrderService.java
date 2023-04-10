package com.ldp.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ldp.seckill.VO.GoodsVo;
import com.ldp.seckill.VO.OrderDetailVo;
import com.ldp.seckill.pojo.Order;
import com.ldp.seckill.pojo.User;

public interface IOrderService  extends IService<Order> {
    Order seckill(User user, GoodsVo goods);

    OrderDetailVo detail(Long orderId);

    String createPath(User user, Long goodsId);

    boolean checkPath(String path, User user, Long goodsId);
}
