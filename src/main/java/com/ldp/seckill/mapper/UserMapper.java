package com.ldp.seckill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ldp.seckill.pojo.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
