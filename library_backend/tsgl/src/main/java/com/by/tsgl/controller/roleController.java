package com.by.tsgl.controller;

import com.by.tsgl.bean.Admin;
import com.by.tsgl.bean.User;
import com.by.tsgl.mapper.AccountMapper;
import com.by.tsgl.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/admin")
public class roleController {

    @Autowired
    RoleService roleService;
    @Autowired
    private AccountMapper accountMapper;

    @ResponseBody
    @PostMapping("/adduser")
    public Map<String, Object> addUser(@RequestBody User[] uList) {
        return roleService.addUser(uList);
    }

    @ResponseBody
    @PostMapping("/setAdmin")
    public Map<String, Object> setAdmin(@RequestBody Map a){
        String account = (String)(a.get("account"));
        if(account==null){
            Map<String,Object> map =new HashMap<>();
            map.put("msg","[系统错误]请求参数中缺少account属性！");
            map.put("isSuccess",false);
            return map;
        }
        return roleService.promoteToAdmin(account);
    }

//    删除用户逻辑复杂，需要考虑到所借图书是否归还，押金是否退回，且没有太大意义，暂不实现。
//    @ResponseBody
//    @PostMapping("/deluser")
//    public boolean deleteUser(@RequestBody User u,
//                              @RequestParam("admin_id") String admin_id){
////        WARNING:禁止使用！！！！
////        return  roleService.delAccount(u,1);
//        return  roleService.delAccount(u);
//    }

    @ResponseBody
    @PostMapping("/delAdmin")
    public Map<String, Object> deleteAdmin(@RequestBody Map a){
        String account = (String)(a.get("account"));
        if(account==null){
            Map<String,Object> map =new HashMap<>();
            map.put("msg","[系统错误]请求参数中缺少account属性！");
            map.put("isSuccess",false);
            return map;
        }
        return  roleService.delAdmin(account);
    }

//    @ResponseBody
//    @PostMapping("/mfyadmin")
//    public boolean modifyAdmin(@RequestBody Admin admin){
//        return roleService.modifyRole(admin,2);
//    }

    @ResponseBody
    @GetMapping("/users")
    public List<Map<String,Object>> getUsers(@RequestParam(value="account",required=false) String account) {
        if (account==null) {     //没有参数，获取普通用户
            return roleService.getCommonUsers();
        }else{                  //针对某一个account，获取该用户信息
            List rs=new ArrayList<>();
            rs.add (roleService.getUser(account));
            return rs;
        }
    }

    @ResponseBody
    @GetMapping("/admins")
    public List<Map<String,Object>> getAdmins(@RequestParam(value="account",required=false) String account) {
        if (account == null) {     //一个参数
            return roleService.getAdminsInfo();
        } else {
            List rs = new ArrayList<>();
            rs.add(roleService.getAdmin(account));
            return rs;
        }

    }
}
