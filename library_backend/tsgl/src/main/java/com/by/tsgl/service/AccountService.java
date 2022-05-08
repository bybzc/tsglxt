package com.by.tsgl.service;
/**
 *
 */

import com.by.tsgl.bean.BasicUser;
import com.by.tsgl.bean.Message;
import com.by.tsgl.bean.User;
import com.by.tsgl.mapper.AccountMapper;
import com.by.tsgl.mapper.DataStateMapper;
import com.by.tsgl.mapper.RoleMapper;
import com.by.tsgl.util.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.lang.reflect.Member;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AccountService {
    @Autowired
    private AccountMapper accountMapper;
    @Autowired
    private DataStateMapper dataStateMapper;

    @Autowired
    private RoleMapper roleMapper;
    /**
     * 登录逻辑
     * @param account
     * @param password
     * @return
     */
    public Map<String, Object> login(String account, String password) {
        Map<String, Object>rc=new HashMap<>();
        BasicUser basicUser = accountMapper.getAccount(account,password);
        if (basicUser != null && basicUser.getPassword().equals(password)){
            rc.put("isSucceed",1);
            String user_id = accountMapper.getUserId(account);
            rc.put("user_id",user_id);
            String token = TokenUtil.sign(account,password);//发送token
            rc.put("token",token);
            if(isAdmin(account)){
                rc.put("isAdmin",1);
                String admin_id=roleMapper.getAdminId(user_id);
                rc.put("admin_id",admin_id);
            }else{
                rc.put("isAdmin",0);
            }
        }
        else{
            rc.put("isSucceed",0);
        }
        return rc;
    }

    /**
     * 判断是否是管理员
     * @param account
     * @return
     */
    public boolean isAdmin(String account) {
        if(accountMapper.isAdmin(account))
            return true;
        return false;
    }

    /**
     * 判断账户是否存在
     * @param account
     * @return
     */
    public boolean isAccount(String account){
        return accountMapper.isAccount(account);
    }

    /**
     *添加用户信息
     * @param u
     */
    public void addAcc(User u){
        accountMapper.addAcc(u);
    }

    public Map<String, Object> modifyUserInfo(User user) {
        String account = user.getAccount();
        if(account==null){//不存在account属性
            Map<String,Object> map =new HashMap<>();
            map.put("msg","[系统错误]请求参数中缺少account属性！");
            map.put("isSuccess",false);
            return map;
        }
        else if(!accountMapper.isAccount(account)) {
            //账号不存在
            Map<String, Object> map = new HashMap<>();
            map.put("msg", "该账号不存在！");
            map.put("isSuccess", false);
            return map;
        }
        //账号正常，可以修改

        try{
            System.out.println(user);
            System.out.println("阿巴阿巴"+user.getUser_tel());
            BasicUser basicUser = accountMapper.getAccount(account, user.getPassword());
            if (basicUser != null && basicUser.getPassword().equals(user.getPassword())){
                accountMapper.modifyUserInfo(user);
            }
            else{
                Map<String, Object> map = new HashMap<>();
                map.put("msg", "密码错误");
                map.put("isSuccess", false);
                return map;
            }


        }catch (Exception e){
            Map<String, Object> map = new HashMap<>();
            map.put("msg", "更新失败！请确认信息为第一次注册");
            map.put("isSuccess", false);
            System.out.println(e);
            return map;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("msg", "更新成功！");
        map.put("isSuccess", true);
        return map;
    }

    //将已经看见的信息设置为已读
    @Transactional
    public List<Message> getAllNotify(String account){
       List<Message>messageList= dataStateMapper.getNotify(account,null);
        Message msg=new Message();
       for(Message message:messageList){
           if(message.getState().equals("已读"))
               continue;
           msg.setId(message.getId());
           msg.setState("已读");
           dataStateMapper.updateNotify(msg);
       }
       return messageList;
    }
}
