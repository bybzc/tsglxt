package com.by.tsgl.service;

/**
 * @author leibaoyu
 * @version 1.0
 * @date 2021/11/4 11:38
 */
public interface Alipay {
//    网页支付
    String page(String subject, String value,String outTradeNo);
//    手机支付
    String wap(String subject,  String value,String outTradeNo);
//    退款
    String refund(String outTradeNo, String refundAmount);
//    交易查询
    String query(String outTradeNo);
}
