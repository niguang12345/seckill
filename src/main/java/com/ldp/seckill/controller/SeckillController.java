package com.ldp.seckill.controller;

import com.ldp.seckill.VO.GoodsVo;
import com.ldp.seckill.VO.RespBean;
import com.ldp.seckill.VO.RespBeanEnum;
import com.ldp.seckill.VO.SeckillMessage;
import com.ldp.seckill.config.AccessLimit;
import com.ldp.seckill.pojo.SeckillOrder;
import com.ldp.seckill.pojo.User;
import com.ldp.seckill.rabbitmq.MQSender;
import com.ldp.seckill.service.IGoodsService;
import com.ldp.seckill.service.IOrderService;
import com.ldp.seckill.service.ISeckillOrderService;
import com.ldp.seckill.util.JsonUtil;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/seckill")
public class SeckillController implements InitializingBean {
    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private ISeckillOrderService seckillOrderService;
    @Autowired
    private IOrderService orderService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private MQSender mqSender;

    //用于内存标记，标记当前商品是否有库存
    private Map<Long,Boolean> emptyStockMap=new HashMap<>();

    /**
     * 优化前：QPS:118
     * 前后端优化:
     * redis优化:1002
     */
    @RequestMapping(value = "/{path}/doSeckill",method = RequestMethod.POST)
    @ResponseBody
    public RespBean doSeckill(@PathVariable String path, User user, Long goodsId) {
        //   判断用户是否登录 ，如果没有登录则返回登录页
        if (user == null) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        //model.addAttribute("user", user);
        //根据id查询商品 当前代码只和redis打交道，不做任何数据库操作
       // GoodsVo goods = goodsService.findGoodsVoByGoodsId(goodsId);
        //如果商品库存小于1，则给个提示，返回页面
       /* if (goods.getGoodsStock() < 1) {
            //model.addAttribute("errmsg", RespBeanEnum.EMPTY_STOCK);
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }*/
      ValueOperations ops=redisTemplate.opsForValue();
        //检查重复购买 让操作通过redis而不是通过数据库
        //select * from seckillgoods where user_id=xxx and goods_id=xxx
     /*   SeckillOrder seckillOrder = seckillOrderService.getOne(new QueryWrapper<SeckillOrder>().
                eq("user_id", user.getId()).
                eq("goods_id", goodsId));*/
        boolean check=orderService.checkPath(path,user,goodsId);
        if(!check){
            return RespBean.error(RespBeanEnum.STATUS_ILLEGAL);
        }
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.opsForValue()
                .get("order:" + user.getId() + ":" + goodsId);
        if (seckillOrder != null) {
            //model.addAttribute("errmsg",RespBeanEnum.REPATE_ERROR);
            return RespBean.error(RespBeanEnum.REPATE_ERROR);
        }
       /* Order order = orderService.seckill(user, goods);
        if (order == null) {
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }*/
        Long stock = ops.decrement("seckillGoods:" + goodsId);
        //检查内存标记，减少对redis的无意义访问
        if(!emptyStockMap.get(goodsId)){
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }

        if(stock<0){
            emptyStockMap.put(goodsId,false);
            //如果库存是0，那么减完会变成-1，所以要加回成0
            ops.increment("seckillGoods:"+goodsId);
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        SeckillMessage message=new SeckillMessage(user,goodsId);
        mqSender.sendSeckillMessage(JsonUtil.object2JsonStr(message));

    /*  model.addAttribute("order",order);
     model.addAttribute("goods",goods);*/
        return RespBean.success();
    }


    //实现InitializingBean接口，重写方法，当系统启动，启动流程加载完配置文件之后会自动执行这个方法
    //在系统初始化的时候，读取数据库秒杀商品，将商品库存放入redis中
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> list=goodsService.findGoodsVo();
        if(CollectionUtils.isEmpty(list)){
            return ;
        }
        //
        list.forEach(goodsVo ->{
            redisTemplate.opsForValue().set("seckillGoods:"+goodsVo.getId(),goodsVo.getGoodsStock());
            //通过内存标记，当前商品是否有库存，减少无意义的对redis进行访问
            emptyStockMap.put(goodsVo.getId(),true);
        });
    }

    @RequestMapping(value="/result",method= RequestMethod.GET)
    @ResponseBody
   public RespBean getResult(User user,Long goodsId){
        if(user==null){
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        Long orderId=seckillOrderService.getResult(user,goodsId);
        return RespBean.success(orderId);
   }

    /**
     *获取秒杀地址
     * @param goodsId 商品ID
     * @return 秒杀真实地址
     */
    @AccessLimit(second = 5, maxCount = 5)
    @RequestMapping("/path")
    @ResponseBody
    public RespBean getPath(User user, Long goodsId) {
        String path = orderService.createPath(user, goodsId);
        return RespBean.success(path);
    }
}
