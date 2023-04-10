package com.ldp.seckill.VO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public enum RespBeanEnum {
    SUCCESS(200, "SUCCESS"),
    ERROR(500, "服务器异常"),
    LOGIN_ERROR(500100, "用户名密码错误"),
    MOBILE_ERROR(500200, "手机号码格式不正确"),
    SESSION_ERROR(500300, "请登录"),
    PASSWORD_UPDATE_REEOR(500400,"密码修改错误"),
    STATUS_ILLEGAL(500500,"状态错误"),
    ACCESS_LIMIT(500600,"超过限制"),

    ///商品秒杀错误
    EMPTY_STOCK(500301, "商品售罄"),
    REPATE_ERROR(500302, "请勿重复购买"),
    //订单信息
    ORDER_NOT_EXIST(500401, "订单并不存在");

    private final Integer code;
    private final String message;
}

