package com.ldp.seckill.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ldp.seckill.VO.GoodsVo;
import com.ldp.seckill.VO.OrderDetailVo;
import com.ldp.seckill.VO.RespBeanEnum;
import com.ldp.seckill.exception.GlobalException;
import com.ldp.seckill.mapper.OrderMapper;
import com.ldp.seckill.pojo.Order;
import com.ldp.seckill.pojo.SeckillGoods;
import com.ldp.seckill.pojo.SeckillOrder;
import com.ldp.seckill.pojo.User;
import com.ldp.seckill.service.IGoodsService;
import com.ldp.seckill.service.IOrderService;
import com.ldp.seckill.service.ISeckillGoodsService;
import com.ldp.seckill.service.ISeckillOrderService;
import com.ldp.seckill.util.MD5Util;
import com.ldp.seckill.util.UUIDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {

    @Autowired
    private ISeckillOrderService seckillOrderService;

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private ISeckillGoodsService seckillGoodsService;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 功能描述:秒杀
     */
    @Transactional
    @Override
    public Order seckill(User user, GoodsVo goods) {
        //1.减库存
        SeckillGoods seckillGoods = seckillGoodsService.getOne(new QueryWrapper<SeckillGoods>().eq("goods_id", goods.getId()));
      /* seckillGoods.setStockCount(seckillGoods.getStockCount()-1);
       seckillGoodsService.updateById(seckillGoods);*/
       seckillGoodsService.update(new UpdateWrapper<SeckillGoods>()
                .setSql("stock_count=stock_count-1")
                .eq("goods_id", goods.getId())
                .gt("stock_count", 0));
        if(seckillGoods.getStockCount()<1){
            //判断如果当前数据库没有库存时，设定一个库存为null的redis key
            redisTemplate.opsForValue().set("isStockEmpty:"+goods.getId(),"0");
            return null;
        }

        //2.生成订单
        Order order = new Order();
        order.setUserId(user.getId());
        order.setGoodsId(goods.getId());
        order.setDeliverAddrId(0L);
        order.setGoodsName(goods.getGoodsName());
        order.setGoodsCount(1L);
        order.setGoodsPrice(goods.getSeckillPrice());
        order.setOrderChannel(1);
        order.setStatus(0);
        order.setCreateDate(new Date());
        orderMapper.insert(order);
        //3.生成秒杀订单
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setUserId(user.getId());
        seckillOrder.setOrderId(order.getId());
        seckillOrder.setGoodsId(goods.getId());
        seckillOrderService.save(seckillOrder);//要明确，为什么order用mappper保存，而这用service，是为了业务隔离
        //将购买记录添加到redis中，这样的判断重复购买就可以直接从redis中取走而不走数据库

        redisTemplate.opsForValue().set("order:" + user.getId() + ":" + goods.getId(), seckillOrder);

        return order;
    }

    @Override
    public OrderDetailVo detail(Long orderId) {
        if (orderId == null) {
            throw new GlobalException(RespBeanEnum.ORDER_NOT_EXIST);
        }
        Order order = orderMapper.selectById(orderId);
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(order.getGoodsId());
        OrderDetailVo orderDetailVo = new OrderDetailVo();
        orderDetailVo.setOrder(order);
        orderDetailVo.setGoodsVo(goodsVo);
        return orderDetailVo;
    }

    @Override
    public String createPath(User user, Long goodsId) {
        String path= MD5Util.md5(UUIDUtil.uuid());
        System.out.println(user+"sec");
        redisTemplate.opsForValue().set("seckillPath:"+user.getId()+":"+goodsId,path,1, TimeUnit.MINUTES);
        return path;
    }
@Override
   public  boolean checkPath(String path, User user, Long goodsId){
        if(user==null||goodsId<0|| !StringUtils.hasLength(path)){
            return false;
        }
   String check= (String)redisTemplate.opsForValue().get("seckillPath:"+user.getId()+":"+goodsId);
        return  path.equals(check);
}
}
