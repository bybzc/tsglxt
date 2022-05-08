package com.by.tsgl.mapper;

import com.by.tsgl.bean.Admin;
import com.by.tsgl.bean.BasicUser;
import com.by.tsgl.bean.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

@Mapper
public interface AccountMapper {
    public Admin getAccount_admin(String account,String password);
    public User getAccount_user(String account,String password);
    public BasicUser getAccount(String account, String password);
    public Boolean isAccount(String account);
    public Boolean isAdmin(String account);
    public void addAcc(BasicUser u);
    //内部工具 简化sql语句，提前查出id
    public String getUserId(String account);
    //更新账号信息
    public void modifyUserInfo(User user);
    //查询用户信息是否填满 填满则为1 不填满则为2
    boolean isInfoNotFull(String account);
    User getUserInfo(String account);
}

