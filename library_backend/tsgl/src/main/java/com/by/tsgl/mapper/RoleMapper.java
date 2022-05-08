package com.by.tsgl.mapper;

import com.by.tsgl.bean.Admin;
import com.by.tsgl.bean.BasicUser;
import com.by.tsgl.bean.Reader;
import com.by.tsgl.bean.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface RoleMapper {
    void promoteToAdmin(String account);

    void deleteAdmin(String user_id);

    Admin getAdminById(String admin_id);

    User getUserById(String user_id);

    String getAdminId(String user_id);

    void updateReader(Reader reader);

    User getUserByAccount(String account);

    List<Map<String,Object>> getCommonUserInfo();
    Map<String,Object>getCommonUserInfoByAccount(String account);

    List<Map<String,Object>>getAdminsInfo();
    Map<String,Object> getAdminByAccount(String account);
}
