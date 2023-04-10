package com.ldp.seckill.rabbitmq;

import com.ldp.seckill.VO.GoodsVo;
import com.ldp.seckill.VO.SeckillMessage;
import com.ldp.seckill.pojo.SeckillOrder;
import com.ldp.seckill.pojo.User;
import com.ldp.seckill.service.IGoodsService;
import com.ldp.seckill.service.IOrderService;
import com.ldp.seckill.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MQReceiver {
    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private IOrderService orderService;
    @RabbitListener(queues="seckillQueue")
    public void receive(String message){
        log.info("接受消息:"+message);
        SeckillMessage seckillMessage = JsonUtil.jsonStr2Object(message, SeckillMessage.class);
        Long goodsId = seckillMessage.getGoodsId();
        User user = seckillMessage.getUser();
        //判断数据库的库存
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
        if(goodsVo.getGoodsStock()<1){
            return;
        }

        //判断是否重复购买
      SeckillOrder seckillOrder=(SeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsId);
        if(seckillOrder!=null){
            return;
        }
        //下单，写入数据库
        orderService.seckill(user,goodsVo);
    }
}
