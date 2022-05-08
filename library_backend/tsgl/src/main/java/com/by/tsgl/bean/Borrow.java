package com.by.tsgl.bean;
/**
 * @author leibaoyu
 * @version 1.0
 * @date 2021/11/3 20:23
 */
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class Borrow {
    private String borrow_id;
    private String borrow_state;
    private int renew_num;
    private int renew_day;

    private String book_id;
    private String reader_id;

    private String borrow_time;
    private String should_return;
    private String return_time;
}
