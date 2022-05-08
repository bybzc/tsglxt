package com.by.tsgl.bean;
/**
 * @author leibaoyu
 * @version 1.0
 * @date 2021/11/3 20:23
 */
import lombok.Data;

@Data
public class Announcement {
    private String announcement_id;
    private String announcement_title;
    private String announcement_content;
    private String announcement_date;
    private String announcement_publisher_id;
}
