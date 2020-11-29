package com.young.mall.service.impl;

import com.young.db.dao.YoungCartMapper;
import com.young.db.entity.YoungBrand;
import com.young.db.entity.YoungCart;
import com.young.db.entity.YoungCartExample;
import com.young.mall.common.ResBean;
import com.young.mall.domain.BrandCartGoods;
import com.young.mall.service.ClientCartService;
import com.young.mall.service.MallBrandService;
import com.young.mall.system.SystemConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * @Description: 购物车业务
 * @Author: yqz
 * @CreateDate: 2020/11/28 16:55
 */
@Service
public class ClientCartServiceImpl implements ClientCartService {

    @Autowired
    private YoungCartMapper youngCartMapper;

    @Autowired
    private MallBrandService mallBrandService;

    @Override
    public ResBean<Map<String, Object>> index(Integer uid) {

        List<YoungCart> cartList = queryByUid(uid);
        //商品数量
        Integer goodsCount = 0;
        //总价
        BigDecimal goodsAmount = new BigDecimal(0);
        //选中状态的商品数量
        Integer checkedGoodsCount = 0;
        //选中状态商品的总价
        BigDecimal checkedGoodsAmount = new BigDecimal(0);

        /*
         * public BigDecimal add(BigDecimal value);      //加法
         * public BigDecimal subtract(BigDecimal value); //减法
         * public BigDecimal multiply(BigDecimal value); //乘法
         * public BigDecimal divide(BigDecimal value);   //除法
         * */
        for (YoungCart cart : cartList) {
            goodsCount += cart.getNumber();
            goodsAmount = goodsAmount.add(cart.getPrice().multiply(new BigDecimal(cart.getNumber())));
            //判断商品是否是选中状态
            if (cart.getChecked()) {
                checkedGoodsCount += cart.getNumber();
                checkedGoodsAmount = checkedGoodsAmount.add(cart.getPrice().multiply(new BigDecimal(cart.getNumber())));
            }
        }

        Map<String, Object> cartTotal = new HashMap<>(6);
        cartTotal.put("goodsCount", goodsCount);
        cartTotal.put("goodsAmount", goodsAmount);
        cartTotal.put("checkedGoodsCount", checkedGoodsCount);
        cartTotal.put("checkedGoodsAmount", checkedGoodsAmount);

        Map<String, Object> result = new HashMap<>(8);
        result.put("cartTotal", cartTotal);

        // 如果需要拆订单，则需要按店铺显示购物车商品
        if (SystemConfig.isMultiOrderModel()) {
            result.put("isMultiOrderModel", 1);
            List<BrandCartGoods> brandCartGoodsList = new ArrayList<>();
            for (YoungCart cart : cartList) {
                //入驻品牌商编码
                Integer brandId = cart.getBrandId();
                boolean hasExist = false;
                for (int i = 0; i < brandCartGoodsList.size(); i++) {
                    if (brandCartGoodsList.get(i).getBrandId().intValue() == brandId.intValue()) {
                        brandCartGoodsList.get(i).getCartList().add(cart);
                        hasExist = true;
                        //结束当前循环
                        break;
                    }
                }
                // 还尚未加入，则需要查询品牌入驻商铺
                if (!hasExist) {
                    Optional<YoungBrand> optional = mallBrandService.findById(brandId);
                    if (!optional.isPresent()) {
                        return ResBean.failed("查询失败");
                    }
                    YoungBrand brand = optional.get();
                    BrandCartGoods brandCartGoods = BrandCartGoods.init(brand);
                    List<YoungCart> youngCartList = new ArrayList<>();
                    youngCartList.add(cart);
                    brandCartGoods.setCartList(youngCartList);
                    brandCartGoodsList.add(brandCartGoods);
                }
            }
            result.put("brandCartGoods", brandCartGoodsList);
        } else {
            result.put("isMultiOrderModel", 0);
            result.put("cartList", cartList);
        }

        return ResBean.success(result);
    }

    @Override
    public List<YoungCart> queryByUid(Integer uid) {

        YoungCartExample example = new YoungCartExample();
        example.createCriteria().andUserIdEqualTo(uid).andDeletedEqualTo(false);
        List<YoungCart> cartList = youngCartMapper.selectByExample(example);
        return cartList;
    }

}