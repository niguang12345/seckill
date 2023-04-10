package com.ldp.seckill.service.impl;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ldp.seckill.VO.GoodsVo;
import com.ldp.seckill.mapper.GoodsMapper;
import com.ldp.seckill.mapper.SeckillGoodsMapper;
import com.ldp.seckill.mapper.UserMapper;
import com.ldp.seckill.pojo.Goods;
import com.ldp.seckill.pojo.User;
import com.ldp.seckill.service.IGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods> implements IGoodsService {
    @Autowired
    private GoodsMapper goodsMapper;

    @Override
    public List<GoodsVo> findGoodsVo() {
        return goodsMapper.findGoodsVo();
    }

    @Override
    public GoodsVo findGoodsVoByGoodsId(Long goodsId) {
        return goodsMapper.findGoodsVoByGoodsId(goodsId);
    }
}
