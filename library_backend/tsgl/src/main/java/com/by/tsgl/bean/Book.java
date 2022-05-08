package com.by.tsgl.bean;
/**
 * @author leibaoyu
 * @version 1.0
 * @date 2021/11/3 20:23
 */
import lombok.Data;

@Data
public class Book {
    private String book_id;
    private String nameOfBook;
    private String author;
    private String press;           //出版社
    private String isbn;
    private String language;        //语种
    private String abstractMsg;
    private String keyword;        //主题词
    private double price;
    private String call_number;     //分类
    private int library_id;
    private String position;        //图书所在位置
    private String state;              //图书状态，在馆1，已经借出但还有2，已经借出但没有3，遗失4
    private String situation;          //图书情况，完好1，基本良好2，一般受损3，严重受损4，遗失5
    private String image;
    private String library_name;
}
