package com.by.tsgl.bean;

import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author leibaoyu
 * @version 1.0
 * @date 2021/11/3 20:23
 */
@Data
public class Message {
    private String id;
    private String title;
    private String content;
    private String date;
    private String state;
    private String father_id;
}
