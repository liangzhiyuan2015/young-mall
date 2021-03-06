package com.young.mall.service;

import com.young.db.entity.YoungOrder;
import com.young.db.entity.YoungOrderGoods;
import com.young.db.entity.YoungUser;
import com.young.mall.common.ResBean;
import com.young.mall.domain.vo.OrderCommentVo;
import com.young.mall.domain.vo.SubmitOrderVo;
import me.chanjar.weixin.common.error.WxErrorException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @Description: 订单业务
 * @Author: yqz
 * @CreateDate: 2020/12/10 11:33
 */
public interface ClientOrderService {

    /**
     * 订单信息
     *
     * @param userId
     * @return 订单信息
     */
    Map<String, Object> orderInfo(Integer userId);

    /**
     * 订单列表
     *
     * @param userId   用户id
     * @param showType 订单状态
     * @param page     分页
     * @param size     页面大小
     * @return
     */
    Map<String, Object> list(Integer userId, Integer showType, Integer page, Integer size);


    /**
     * 通过订单状态查询订单列表
     *
     * @param userId      用户id
     * @param orderStatus 订单状态
     * @param page        分页
     * @param size        大小
     * @return
     */
    List<YoungOrder> queryByOrderStatus(Integer userId, List<Short> orderStatus, Integer page, Integer size);

    /**
     * 根据订单id查询订单
     *
     * @param orderId 订单id
     * @return
     */
    YoungOrder findById(Integer orderId);

    /**
     * 订单详情
     *
     * @param userId  用户ID
     * @param orderId 订单ID
     * @return
     */
    Map<String, Object> detail(Integer userId, Integer orderId);


    /**
     * 订单物流跟踪
     *
     * @param userId  用户ID
     * @param orderId 订单ID
     * @return
     */
    Map<String, Object> expressTrace(Integer userId, Integer orderId);

    /**
     * 待评价订单商品信息
     *
     * @param userId  用户ID
     * @param orderId 订单ID
     * @param goodsId 商品ID
     * @return 待评价订单商品信息
     */
    YoungOrderGoods getGoodsByIds(Integer userId, Integer orderId, Integer goodsId);

    /**
     * 添加评论
     *
     * @param commentVo
     * @return
     */
    ResBean comment(OrderCommentVo commentVo);

    /**
     * 更新订单中未评价的订单商品可评价数量
     *
     * @param order
     * @return
     */
    Integer updateWithOptimisticLocker(YoungOrder order);

    /**
     * 提交订单
     * 1. 创建订单表项和订单商品表项;
     * 2. 购物车清空;
     * 3. 优惠券设置已用;
     * 4. 商品货品库存减少;
     * 5. 如果是团购商品，则创建团购活动表项。
     *
     * @param userId      用户id
     * @param submitOrder 提交订单的数据
     * @return
     */
    ResBean submit(Integer userId, SubmitOrderVo submitOrder);

    /**
     * 添加订单
     *
     * @param order
     * @return
     */
    Integer add(YoungOrder order);

    /**
     * 付款订单的预支付会话标识
     * <p>
     * 1. 检测当前订单是否能够付款
     * 2. 微信商户平台返回支付订单ID
     * 3. 设置订单付款状态
     *
     * @param userId  用户ID
     * @param orderId
     * @param request
     * @return
     */
    ResBean prepay(Integer userId, Integer orderId, HttpServletRequest request);


    /**
     * 取消订单
     * <p>
     * 1. 检测当前订单是否能够取消；
     * 2. 设置订单取消状态；
     * 3. 商品货品库存恢复；
     * 4. TODO 优惠券；
     * 5. TODO 团购活动。
     *
     * @param userId  用户id
     * @param orderId 订单id
     * @return
     */
    ResBean cancel(Integer userId, Integer orderId);

    /**
     * 退货
     *
     * @param user    用户
     * @param orderId 订单id
     * @return
     */
    ResBean refund(YoungUser user, Integer orderId);

    /**
     * 确认收货
     *
     * @param userId  用户id
     * @param orderId 订单id
     * @return
     */
    ResBean confirm(Integer userId, Integer orderId);

    /**
     * 删除订单
     *
     * @param userId  用户id
     * @param orderId 订单id
     * @return
     */
    ResBean delete(Integer userId, Integer orderId);
}
