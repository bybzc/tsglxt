package com.by.tsgl.bean;

import lombok.Data;

/**
 * @author leibaoyu
 * @version 1.0
 * @date 2021/11/11 11:02
 */
@Data
public class Fine {
    private String fine_id;
    private double fine_amount;
    private String fine_time;
    private String fine_out_trade_no;
//    private String borrow_id;
//    private String fine_rule_id;

}
