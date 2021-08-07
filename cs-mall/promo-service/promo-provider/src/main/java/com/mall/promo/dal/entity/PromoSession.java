package com.mall.promo.dal.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Data
@Table(name = "tb_promo_session")
public class PromoSession implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    Integer id;

    @Column(name = "session_id")
    Integer sessionId;

    @Column(name = "start_time")
    Date startTime;

    @Column(name = "end_time")
    Date endTime;

    @Column(name = "yyyymmdd")
    String yyyymmdd;
}
