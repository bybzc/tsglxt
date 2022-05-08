package com.by.tsgl.service;

import com.by.tsgl.bean.BasicUser;
import com.by.tsgl.bean.User;
import com.by.tsgl.mapper.AccountMapper;
import com.by.tsgl.mapper.RoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RoleService {
    @Autowired
    RoleMapper roleMapper;
    @Autowired
    AccountMapper accountMapper;

    public Map<String, Object> addUser(BasicUser[] uList){
        Map<String, Object> map = new HashMap<>();
        String returnMsg = "";
        String unsuccessMsg = "下列账号已存在：";
        String unsuccessMsg4unknownReson = "下列账号因未知原因注册失败：";
        String successMsg = "";
        int umsgLen = unsuccessMsg.length();
        int um4urLen = unsuccessMsg4unknownReson.length();
        for ( BasicUser u : uList){
            try {
                if (accountMapper.isAccount(u.getAccount())) {//账号存在
//                    map.put("isSucceed","halfSuccess");
                    unsuccessMsg = unsuccessMsg + u.getAccount() + " ";
                } else {
                    accountMapper.addAcc(u);
                    successMsg += u.getAccount() + " ";
                }
            } catch (Exception e) {
                System.out.println("注册：出错了！");
                e.printStackTrace();
                unsuccessMsg4unknownReson = unsuccessMsg4unknownReson + u.getAccount();
            }
        }

        if (unsuccessMsg.length() != umsgLen) {//有不成功的
            returnMsg += unsuccessMsg + '\n';
        }
        if (unsuccessMsg4unknownReson.length() != um4urLen) {//有未知原因不成功的
            returnMsg += unsuccessMsg4unknownReson + '\n';
        }
        if (returnMsg.length() != 0 && successMsg.length() != 0) {//存在注册不成功和成功的
            map.put("isSucceed", "halfSuccess");
            returnMsg += "以下账号注册成功：" + successMsg + '\n';
        } else if (successMsg.length() != 0) {//都成功的
            map.put("isSucceed", "success");
            returnMsg += "以下账号注册成功：" + successMsg + '\n';
        } else {//都不成功的
            map.put("isSucceed", "unSuccess");
        }
        System.out.println(returnMsg);
        map.put("msg", returnMsg);
        return map;

    }

    public Map<String, Object> promoteToAdmin(String account){
        System.out.println(account);
        Map<String, Object> map = new HashMap<>();
        String returnMsg = "";
        try {
            if (accountMapper.isAccount(account)) {//普通账号存在
                if(accountMapper.isAdmin(account)){
                    map.put("isSuccess",false);
                    map.put("msg","该账号已是管理员身份，无法提升为管理员账号");
                }
                else if(accountMapper.isInfoNotFull(account)){
                    //检查账号信息是否填写完全（仅允许信息完整的用户提升为管理员）
                    map.put("isSuccess",false);
                    map.put("msg","该账号信息不完整，无法提升为管理员账号");
                }
                else{
                    roleMapper.promoteToAdmin(account);
                    map.put("isSuccess",true);
                }
            } else {
                map.put("isSuccess",false);
                map.put("msg","该账号不存在，无法提升为管理员账号");
            }
        } catch (Exception e) {
            System.out.println("升级管理员：出错了！");
            e.printStackTrace();
            map.put("isSuccess",false);
            map.put("msg","升级管理员出现未知错误，请与维护人员联系");
        }
        return map;

    }

    public boolean delAccount(BasicUser basicUser){
//        Map<String,Object>map=new HashMap<>();
//        try{
//            if(accountMapper.isAccount(basicUser.getAccount())){
//                //TODO 要删除账号 需要检索所有与账号相关的记录 包括图书是否归还，押金是否退回，逻辑较复杂，且没有太大意义，故暂不实现。
//            }
//            else{
//                //不存在这样的账号
//                map.put("isSuccess",false);
//                map.put("msg","该账号不存在");
//            }
//        }catch (Exception e){
//            System.out.println(e);
//        }
        return false;
    }
    public Map<String,Object> delAdmin(String account){
        Map<String,Object>map=new HashMap<>();
        try{
            System.out.println(account);
            String userId=accountMapper.getUserId(account);
            System.out.println(userId);
            if(userId==null){
                map.put("isSuccess",false);
                map.put("msg","账号不存在！");
                return map;
            }
            else{
                //账号存在
                //在这里不判断该用户是否是管理员身份，无论是不是，只要从管理员表中删除即可
                roleMapper.deleteAdmin(userId);
                map.put("isSuccess",true);
                map.put("msg","删除管理员成功");

                //TODO: 完善管理员删除的逻辑，包括与公告、回复相关的设计。
            }
        }catch (Exception e){
            map.put("isSuccess",false);
            map.put("msg","存在未知错误！");
            return map;
        }


        return map;
    }


//    public boolean modifyRole(BasicUser basicUser, int mode){
//        if(mode==1){        //用户
//
//        }else if(mode==2){  //管理员用户
//
//        }else{
//            return false;
//        }
//        return true;
//    }

    public List<Map<String,Object>> getCommonUsers(){
        return roleMapper.getCommonUserInfo();
    }
    public Map<String,Object> getUser(String account){
        if(accountMapper.getUserId(account)==null){
            Map map=new HashMap<>();
            map.put("error","账户不存在");
            return map;
        }

        return roleMapper.getCommonUserInfoByAccount(account);
    }
    public List<Map<String,Object>>getAdminsInfo(){
        return roleMapper.getAdminsInfo();
    }
    public Map<String,Object>getAdmin(String account){
        if(accountMapper.getUserId(account)==null){
            Map map=new HashMap<>();
            map.put("error","账户不存在");
            return map;
        }
        return roleMapper.getAdminByAccount(account);
    }


//
//    enum Role{
//        READER,ADMIN;
//    }
}
