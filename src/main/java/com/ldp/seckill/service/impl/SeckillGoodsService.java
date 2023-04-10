package com.ldp.seckill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ldp.seckill.mapper.GoodsMapper;
import com.ldp.seckill.mapper.SeckillGoodsMapper;
import com.ldp.seckill.pojo.Goods;
import com.ldp.seckill.pojo.SeckillGoods;
import com.ldp.seckill.service.IGoodsService;
import com.ldp.seckill.service.ISeckillGoodsService;
import org.springframework.stereotype.Service;

@Service
public class SeckillGoodsService extends ServiceImpl<SeckillGoodsMapper, SeckillGoods> implements ISeckillGoodsService {
}
