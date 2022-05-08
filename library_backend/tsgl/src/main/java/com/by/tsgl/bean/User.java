package com.by.tsgl.bean;
/**
 * @author leibaoyu
 * @version 1.0
 * @date 2021/11/3 20:23
 */
import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class User extends BasicUser {
    private int user_id;
    private String user_email;
    private String user_tel;
    private int user_sex;
    private String user_id_number;
//    private String account;
//    private String password;
//    private String username;
}
