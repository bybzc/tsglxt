package com.by.tsgl.bean;
/**
 * @author leibaoyu
 * @version 1.0
 * @date 2021/11/3 20:23
 */
import lombok.Data;

@Data
public class Library {
    private int library_id;
    private String library_name;
    private String library_address;
    private Book books[];
}
