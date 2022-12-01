package com.itheima.mall.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.mall.common.R;
import com.itheima.mall.domain.CartPromotionItem;
import com.itheima.mall.domain.OmsCartItem;
import com.itheima.mall.domain.PmsSkuStock;
import com.itheima.mall.domain.UmsMember;
import com.itheima.mall.service.IOmsCartItemService;
import com.itheima.mall.service.IPmsBrandService;
import com.itheima.mall.service.IPmsSkuStockService;
import com.itheima.mall.service.IUmsMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 购物车表 前端控制器
 * </p>
 *
 * @author 小刘
 * @since 2022-11-25
 */
@RestController
@RequestMapping("/cartItem")
public class OmsCartItemController {

    @Autowired
    private IPmsSkuStockService iPmsSkuStockService;
    @Autowired
    private IUmsMemberService iUmsMemberService;
    @Autowired
    private IPmsBrandService iPmsBrandService;

    @Autowired
    private IOmsCartItemService iOmsCartItemService;

    /**
     * 添加商品到购物车
     *
     * @return
     */
    @RequestMapping("/add")
    public R add(@RequestBody OmsCartItem cartItem) {

        //获取商品sku
        LambdaQueryWrapper<PmsSkuStock> pmsSkuStockLambdaQueryWrapper = new LambdaQueryWrapper<>();
        pmsSkuStockLambdaQueryWrapper.eq(PmsSkuStock::getProductId, cartItem.getProductId());
        pmsSkuStockLambdaQueryWrapper.eq(PmsSkuStock::getSkuCode, cartItem.getProductSkuCode());
        PmsSkuStock skuStock = iPmsSkuStockService.getOne(pmsSkuStockLambdaQueryWrapper);
        if (skuStock == null) {
            return R.success("商品不存在");
        }
        OmsCartItem one = iOmsCartItemService.getOne(
                new LambdaQueryWrapper<OmsCartItem>().eq(OmsCartItem::getMemberId, 1l).
                        eq(OmsCartItem::getProductSkuId, skuStock.getId())

        );
        if (one != null) {
            one.setQuantity(one.getQuantity() + cartItem.getQuantity());
            iOmsCartItemService.updateById(one);
        } else {
            //获取登录会员
            UmsMember umsMember = iUmsMemberService.getById(1);
            cartItem.setMemberNickname(umsMember.getNickname());


            cartItem.setPrice(skuStock.getPrice());
            cartItem.setMemberId(umsMember.getId());
            cartItem.setProductPic(skuStock.getPic());
            cartItem.setCreateDate(LocalDateTime.now());
            cartItem.setProductAttr(skuStock.getSpData());
            iOmsCartItemService.save(cartItem);
        }


        return R.success("添加商品成功");
    }

    /**
     *
     * @return
     */
    @RequestMapping("/list")
    public R list(@RequestParam(value = "pageSize", required = true, defaultValue = "6") Integer pageSize,
                  @RequestParam(value = "page", required = true, defaultValue = "1") Integer pageNum) {

        Page<OmsCartItem> omsCartItemPage = new Page<>(pageNum, pageSize);

        Page<OmsCartItem> page = iOmsCartItemService.page(omsCartItemPage, new LambdaQueryWrapper<OmsCartItem>().eq(OmsCartItem::getMemberId, 1).orderByDesc(OmsCartItem::getCreateDate));

        return R.success(page);
    }

    /**
     * 查询促销购物车
     *
     * @return
     */
    @RequestMapping("/list/promotion")
    public R listPromotion(@RequestParam("cartIds") List<Long> cartIds) {

        List<CartPromotionItem> cartPromotionItemList = iOmsCartItemService.listPromotion(1l, cartIds);

        return R.success(cartPromotionItemList);
    }

}

