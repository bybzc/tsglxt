package com.by.tsgl.bean;
/**
 * @author leibaoyu
 * @version 1.0
 * @date 2021/11/3 20:23
 */
import lombok.Data;

@Data
public class Reader {
    private String reader_id;
    private String deposit_num;
    private String borrowing_num;
    private String reader_state;
    private String grade_id;
    private String user_id;

//    属于读者的权益
    private String grade_name;
    private int borrow_day;
    private int borrow_booknum;
    private int renew_num;
    private int renew_day;

//    private ReaderGrade readerGrade;

    public void setGrade(ReaderGrade readerGrade){
        this.grade_name=readerGrade.getGrade_name();
        this.borrow_day=readerGrade.getBorrow_day();
        this.borrow_booknum=readerGrade.getBorrow_booknum();
        this.renew_num=readerGrade.getRenew_num();
        this.renew_day=readerGrade.getRenew_day();
    }
}
