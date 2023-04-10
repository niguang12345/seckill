package com.ldp.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ldp.seckill.VO.GoodsVo;
import com.ldp.seckill.pojo.Order;
import com.ldp.seckill.pojo.SeckillOrder;
import com.ldp.seckill.pojo.User;

public interface ISeckillOrderService extends IService<SeckillOrder> {

    Long getResult(User user, Long goodsId);
}
