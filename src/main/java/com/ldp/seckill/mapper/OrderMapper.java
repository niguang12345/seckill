package com.ldp.seckill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ldp.seckill.pojo.Order;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {
}
