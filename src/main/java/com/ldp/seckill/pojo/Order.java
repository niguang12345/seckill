package com.ldp.seckill.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_order")
public class Order implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    /**
     * 订单ID
     */
    private Long id;
    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 商品ID
     */
    private Long goodsId;
    /**
     * 收货地址ID
     */
    private Long deliverAddrId;
    /**
     *
     */
    private String goodsName;
    private Long goodsCount;
    private BigDecimal goodsPrice;
    private Integer orderChannel;
    /**
     * 订单状态，0新建未支付，1已支付，2已发货，3.已收，4已退款，5.已完成
     */
    private Integer status;
    private Date createDate;
    private Date payDate;

}
