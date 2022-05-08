package com.by.tsgl.bean;

import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author leibaoyu
 * @version 1.0
 * @date 2021/11/5 10:31
 */
@Data
public class Deal {
    private String deal_id;
    private String reader_id;
    private String outTradeNo;      //商户订单号，商家自定义
    private String tradeNo;         //交易流水号，支付宝系统
    private String time;
    private String totalAmount;     //交易金额
    private String dealState;
    private String collectionAccount;   //收款账户
    private String pay_method;
    private String subject;
    public Deal(String reader_id,String outTradeNo,String totalAmount,String subject,String collectionAccount,String tradeNo){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.time=sdf.format(new Date());
        this.totalAmount=totalAmount;
        this.outTradeNo=outTradeNo;
        this.reader_id=reader_id;
        this.dealState="待支付";
        this.tradeNo=tradeNo;
        this.collectionAccount=collectionAccount;
        this.subject=subject;
        this.pay_method="支付宝";
    }
}
