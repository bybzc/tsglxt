package com.by.tsgl.bean;

import lombok.Data;

@Data
public class ReaderGrade {
    //    属于读者的权益
    private int grade_id;
    private String grade_name;
    private int borrow_day;
    private int borrow_booknum;
    private int renew_num;
    private int renew_day;
}
