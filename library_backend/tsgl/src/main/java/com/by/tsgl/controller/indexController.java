package com.by.tsgl.controller;

import com.by.tsgl.bean.Message;
import com.by.tsgl.bean.User;
import com.by.tsgl.service.AccountService;
import com.by.tsgl.service.InfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller

public class indexController {

    @Autowired
    private AccountService accountService;
    @Autowired
    private InfoService infoService;

    //   登录
    @ResponseBody
    @RequestMapping("/login")
    public Map<String, Object> isLogin(@RequestBody  Map o){
        String account = o.get("account").toString();
        String password = o.get("password").toString();

        return accountService.login(account, password);

    }

    //   注册
    @ResponseBody
    @PostMapping("/register")
    public Map<String,Object> register(@RequestBody User u) {
        Map<String,Object> map =new HashMap<>();
        try{
            if(accountService.isAccount(u.getAccount())){
                System.out.println("注册：账号已存在！！");
                map.put("isSucceed",false);
                map.put("msg","账号已存在！");
                return map;
            }
            accountService.addAcc(u);
        }catch (Exception e){
            System.out.println("注册：出错了！");
            e.printStackTrace();
            map.put("isSucceed",false);
            map.put("msg","注册出现未知错误，请联系管理员");
            return map;
        }
        map.put("isSucceed",true);
        return map;
    }

    @ResponseBody
    @PostMapping("/modUserInfo")
    public Map<String, Object> modUserInfo(@RequestBody User user){
        return accountService.modifyUserInfo(user);
    }

    /**
     * @description 用户获取所有通知信息
     * @author leibaoyu
     * @date 2021/11/22 20:19
     * @param account
     * @return java.util.List<com.by.tsgl.bean.Message>
     * @throws
     */
    @ResponseBody
    @RequestMapping("/getnotify")
    public List<Message> getAllNotify(@RequestParam("account") String account) throws IOException {
        return accountService.getAllNotify(account);
    }

    /**
     * @description 获取首页 的基本信息
     * @author leibaoyu
     * @date 2021/11/23 13:34
     * @param account
     * @return java.util.Map<java.lang.String, java.lang.Object>
     * @throws
     */
    @ResponseBody
    @GetMapping("/indexmsg")
    public Map<String,Object>getIndexMsg(@RequestParam("account") String account){
        return infoService.getIndexMsg(account);
    }

    @ResponseBody
    @GetMapping("/getUserInfo")
    public User getUserInfo(@RequestParam("account") String account){
        return infoService.getUserInfo(account);
    }

}

