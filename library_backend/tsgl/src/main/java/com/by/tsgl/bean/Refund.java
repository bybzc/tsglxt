package com.by.tsgl.bean;

import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author leibaoyu
 * @version 1.0
 * @date 2021/11/8 21:35
 */
@Data
public class Refund {
    private String refund_id;
    private String subject;
    private double amount;
    private String state;
    private String time;
    private String method;

    private String trade_no;
    private String remark;


    public Refund(){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.time=sdf.format(new Date());
    }
}
