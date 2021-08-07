package com.mall.promo.dal.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author wangjun
 * @Date 2021/1/29 17:47
 **/

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tb_promo_item")
public class PromoItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    Integer id;

    @Column(name = "ps_id")
    Integer psId;

    @Column(name = "item_id")
    Integer itemId;

    @Column(name = "seckill_price")
    BigDecimal seckillPrice;

    @Column(name = "item_stock")
    Integer itemStock;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPsId() {
        return psId;
    }

    public void setPsId(Integer psId) {
        this.psId = psId;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public BigDecimal getSeckillPrice() {
        return seckillPrice;
    }

    public void setSeckillPrice(BigDecimal seckillPrice) {
        this.seckillPrice = seckillPrice;
    }

    public Integer getItemStock() {
        return itemStock;
    }

    public void setItemStock(Integer itemStock) {
        this.itemStock = itemStock;
    }

}
