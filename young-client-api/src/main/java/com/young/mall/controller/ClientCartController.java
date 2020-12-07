package com.young.mall.controller;

import cn.hutool.core.bean.BeanUtil;
import com.young.db.entity.YoungCart;
import com.young.db.entity.YoungGoods;
import com.young.db.entity.YoungGoodsProduct;
import com.young.mall.common.ResBean;
import com.young.mall.domain.ClientUserDetails;
import com.young.mall.domain.enums.WxResponseCode;
import com.young.mall.service.ClientCartService;
import com.young.mall.service.ClientGoodsProductService;
import com.young.mall.service.ClientGoodsService;
import com.young.mall.service.ClientUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * @Description: 购物车业务
 * @Author: yqz
 * @CreateDate: 2020/11/28 16:46
 */
@Api(tags = "ClientCartController")
@RestController
@RequestMapping("/client/cart")
public class ClientCartController {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ClientUserService clientUserService;

    @Autowired
    private ClientCartService clientCartService;

    @Autowired
    private ClientGoodsService clientGoodsService;

    @Autowired
    private ClientGoodsProductService clientGoodsProductService;


    @ApiOperation("购物车首页")
    @GetMapping("/index")
    public ResBean index() {
        ClientUserDetails userInfo = clientUserService.getUserInfo();
        if (BeanUtil.isEmpty(userInfo)) {
            return ResBean.unauthorized("请登录！");
        }
        return clientCartService.index(userInfo.getYoungUser().getId());
    }

    @ApiOperation("用户购物车数量")
    @GetMapping("/goodscount")
    public ResBean goodsCount() {
        ClientUserDetails userInfo = clientUserService.getUserInfo();
        if (BeanUtil.isEmpty(userInfo)) {
            return ResBean.unauthorized("请登录！");
        }
        List<YoungCart> cartList = clientCartService.queryByUid(userInfo.getYoungUser().getId());
        int goodsCount = 0;
        for (YoungCart cart : cartList) {
            goodsCount += cart.getNumber();
        }
        return ResBean.success(goodsCount);
    }

    @ApiOperation("/add")
    @PostMapping("/add")
    public ResBean addGoodsToCart(@RequestBody YoungCart cart) {

        ClientUserDetails userInfo = clientUserService.getUserInfo();
        if (BeanUtil.isEmpty(userInfo)) {
            logger.info("用户添加购物车失败，未登录。");
            return ResBean.unauthorized("请登录！");
        }
        Integer productId = cart.getProductId();
        int number = cart.getNumber().intValue();
        Integer goodsId = cart.getGoodsId();
        //非空校验
        if (!ObjectUtils.allNotNull(productId, number, goodsId)) {
            return ResBean.validateFailed();
        }

        // 判断商品是否可以购买
        YoungGoods goods = clientGoodsService.findById(goodsId);
        if (BeanUtil.isEmpty(goods) || !goods.getIsOnSale()) {
            logger.info("添加商品到购物车失败：{}", WxResponseCode.GOODS_UNSHELVE);
            return ResBean.failed(WxResponseCode.GOODS_UNSHELVE);
        }

        //根据商品货品表的货品id，查询商品货品
        YoungGoodsProduct goodsProduct = clientGoodsProductService.findById(productId);
        // 取得规格的信息,判断规格库存
        if (BeanUtil.isEmpty(goodsProduct) || number > goodsProduct.getNumber()) {
            logger.info("{}->库存不足", cart.getGoodsName());
            return ResBean.failed("该商品目前库存不足");
        }
        // 判断购物车中是否存在此规格商品
        YoungCart existCart = clientCartService.queryExist(goodsId, productId, userInfo.getYoungUser().getId());
        if (BeanUtil.isEmpty(existCart)) {

            BeanUtil.copyProperties(goods, cart);
            cart.setUserId(userInfo.getYoungUser().getId());
            cart.setChecked(true);
            clientCartService.add(cart);
        } else {
            // 取得规格的信息,判断规格库存
            int num = existCart.getNumber() + number;
            //判断购物车中累计的数量是否大于总库存
            if (num > goodsProduct.getNumber()) {
                logger.info("加入商品到购物车失败:{}", WxResponseCode.GOODS_NO_STOCK);
                return ResBean.failed(WxResponseCode.GOODS_NO_STOCK);
            }
            existCart.setNumber(((short) number));
            if (clientCartService.updateById(existCart) == 0) {
                logger.info("加入商品到购物车失败:更新购物车信息失败!");
                return ResBean.failed(505, "更新数据失败");
            }
        }
        return this.goodsCount();
    }

}
