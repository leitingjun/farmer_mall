package com.mall.order.biz;

import com.mall.order.biz.context.TransHandlerContext;

/**
 *  ciggar
 * create-date: 2019/8/2-下午9:58
 */
public interface TransOutboundInvoker {

    /**
     *  启动流程
     */
    void start();

    /**
     *  终止流程
     */
    void shutdown();

    /**
     *  用于获取返回值
     */
    <T extends TransHandlerContext> T getContext();
}
