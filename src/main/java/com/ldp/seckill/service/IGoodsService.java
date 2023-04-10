package com.ldp.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ldp.seckill.VO.GoodsVo;
import com.ldp.seckill.pojo.Goods;

import java.util.List;

public  interface IGoodsService extends IService<Goods> {
    List<GoodsVo> findGoodsVo();
   GoodsVo findGoodsVoByGoodsId(Long goodsId);
}
