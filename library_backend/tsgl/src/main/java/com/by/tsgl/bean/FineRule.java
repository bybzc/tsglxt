package com.by.tsgl.bean;

import lombok.Data;

/**
 * @author leibaoyu
 * @version 1.0
 * @date 2021/11/11 12:01
 */
@Data
public class FineRule {
    private String frule_id;
    private String frule_name;
    private String frule_condition;
    private double frule_amount;
    private double frule_ratio;
}
