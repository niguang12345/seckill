package com.ldp.seckill.controller;

import com.ldp.seckill.VO.DetailVo;
import com.ldp.seckill.VO.GoodsVo;
import com.ldp.seckill.VO.RespBean;
import com.ldp.seckill.pojo.Goods;
import com.ldp.seckill.pojo.User;
import com.ldp.seckill.service.IGoodsService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.server.PathParam;
import java.sql.Time;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/goods")
public class GoodsController {
    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ThymeleafViewResolver thymeleafViewResolver;
    //调转到商品详情页面

    /**
     * 优化前：201
     * 优化后:799
     */
    @RequestMapping(value = "/toList", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String toList(Model model, User user, HttpServletRequest request, HttpServletResponse response) {
        //从redis中获取页面，如果不为空，直接返回页面
        ValueOperations valueOperations = redisTemplate.opsForValue();//开始操作redis
        String html = (String) valueOperations.get("goodsList");
        if (StringUtils.hasLength(html)) {
            return html;
        }

        model.addAttribute("user", user);
        model.addAttribute("goodsList", goodsService.findGoodsVo());

        //如果redis中页面为空，则需要生成页面的字符串存入redis中
        WebContext context = new WebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap());
        //process("goodsList",context); goodsList代表的是页面，会找goodsList.html,将context中的数据加载到goodsList.html中，生成html字符串
        html = thymeleafViewResolver.getTemplateEngine().process("goodsList", context);
        if (!StringUtils.isEmpty(html)) {
            valueOperations.set("goodsList", html, 1, TimeUnit.HOURS);
        }


        return html;
    }

    /**
     * 根据ID查询商品
     * 优化前： QPS:187
     */

    //    /detail/1   restful 风格的传参
    @RequestMapping("/detail/{goodsId}")
    @ResponseBody
    public RespBean toDetail(User user, @PathVariable Long goodsId) {
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
        //System.out.println(goodsVo);
        Date startDate = goodsVo.getStartDate();
        Date endDate = goodsVo.getEndDate();
        Date nowDate = new Date();
        //秒杀状态0代表未开始 1进行中 2 结束
        int seckillStatus = 0;
        //秒杀倒计时
        int remainSeconds = 0;
        //判断当前状态
        if (nowDate.before(startDate)) {
            //当前时间在开始时间之前，代表秒杀还未开始
            //获取距离秒杀的时间差
            remainSeconds = (int) ((startDate.getTime() - nowDate.getTime()) / 1000);
        } else if (nowDate.after(endDate)) {
            //结束状态
            seckillStatus = 2;
            remainSeconds = -1;
        } else {
            //进行中
            seckillStatus = 1;
            remainSeconds = 0;
        }
        DetailVo detailVo = new DetailVo();
        detailVo.setUser(user);
        detailVo.setGoodsVo(goodsVo);
        detailVo.setSecKillStatus(seckillStatus);
        detailVo.setRemainSeconds(remainSeconds);

       /*model.addAttribute("user",user);
       model.addAttribute("goods",goodsVo);
       model.addAttribute("remainSeconds",remainSeconds);
       model.addAttribute("secKillStatus",seckillStatus);*/
        return RespBean.success(detailVo);
    }

}
