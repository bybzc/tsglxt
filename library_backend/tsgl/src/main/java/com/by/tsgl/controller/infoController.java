package com.by.tsgl.controller;

import com.by.tsgl.bean.Announcement;
import com.by.tsgl.bean.Book;
import com.by.tsgl.bean.Message;
import com.by.tsgl.service.InfoService;
import com.by.tsgl.util.MODE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author leibaoyu
 * @version 1.0
 * @date 2021/11/12 9:16
 */
@RestController
public class infoController {
    @Autowired
    private InfoService infoService;


    /**
     * @description 管理员添加公告
     * @author leibaoyu
     * @date 2021/11/12 11:54
     * @param announcement
     * @return boolean
     * @throws
     */
    @PostMapping("/admin/addannouncement")
    public boolean addAnnouncement(@RequestBody Announcement announcement) {
        return infoService.addAnnouncement(announcement);
    }

    /**
     * @description 管理员删除公告
     * @author leibaoyu
     * @date 2021/11/13 17:45
     * @param announcement_id
     * @return java.util.List<java.lang.String>
     * @throws
     */
    @GetMapping("/admin/delannouncement")
    public List<String> delAnnouncement(@RequestParam("announcement_id") List<String> announcement_id) {

        return infoService.delAnnouncement(announcement_id);
    }

    /**
     * @description 管理员修改公告
     * @author leibaoyu
     * @date 2021/11/13 19:31
     * @param announcement
     * @return boolean
     * @throws
     */
    @PostMapping("/admin/modifyannouncement")
    public boolean modifyAnnouncement(@RequestBody Announcement announcement) {
        return infoService.modifyAnnouncement(announcement);
    }

    /**
     * @description 获取几个最新的公告
     * @author leibaoyu
     * @date 2021/11/12 10:46
     * @param size
     * @return java.util.List<com.by.tsgl.bean.Announcement>
     * @throws
     */
    @GetMapping("/announcement")
    public List<Announcement> announcements(@RequestParam(value = "num",required = false,defaultValue = "1") int size) {
        return infoService.getLatestAnnouncementByNum(size);
    }

    /**
     * @description 获取某个管理员的所有发布的所有公告
     * @author leibaoyu
     * @date 2021/11/12 10:49
     * @param admin_id
     * @return java.util.List<com.by.tsgl.bean.Announcement>
     * @throws
     */
    @GetMapping("/announcement_admin")
    public List<Announcement> announcements_admin(@RequestParam("admin_id") String admin_id){
        return infoService.getAnnouncementsByAdminId(admin_id);
    }

    //获取有发布过公告的管理员信息
    @GetMapping("getAnnouncementAdminInfo")
    public List<Map<String,Object>> getAnnouncementAdminInfo(){
        return infoService.getAnnouncementAdminInfo();
    }

    /**
     * @description  图书借阅榜单
     * @author leibaoyu
     * @date 2021/11/13 21:33
     * @param
     * @return java.util.Map<java.lang.Integer, java.util.Map < java.lang.String, java.lang.Object>>
     * @throws
     */
    @GetMapping("/rank")
    public Map<Integer,Map<String,Object>> rangingList() {
        return infoService.getRankList();
    }



    /**
     * @description 用户留言
     * @author leibaoyu
     * @date 2021/11/13 23:02
     * @param msg
     * @return boolean
     * @throws
     */
    @PostMapping("/message")
    public boolean leaveMessage(@RequestBody Message msg) {
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        msg.setDate(sdf.format(new Date()));
        msg.setState("未读");
        return infoService.msgOrReply(msg,MODE.LEAVE_MESSAGE.getVal());
    }
    /**
     * @description 回复留言
     * @author leibaoyu
     * @date 2021/11/13 23:03
     * @param msg
     * @return boolean
     * @throws
     */
    @ResponseBody
    @PostMapping("/admin/reply")
    public boolean replyMsg(@RequestBody Message msg) {
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        msg.setDate(sdf.format(new Date()));
        msg.setState("已回复");
        return infoService.msgOrReply(msg,MODE.REPLY_MESSAGE.getVal());
    }

    /**
     * @description 获取回复
     * @author leibaoyu
     * @date 2021/11/14 0:50
     * @param account
     * @return java.util.List<com.by.tsgl.bean.Message>
     * @throws
     */
    @ResponseBody
    @GetMapping("/getreply")
    public List<Message> getReply(@RequestParam String account) {
        return infoService.getMsgOrReply(account,MODE.REPLY_MESSAGE.getVal());
    }

    /**
     * @description 获取某个用户的留言或者全部留言
     * @author leibaoyu
     * @date 2021/11/14 0:50
     * @param account
     * @return java.util.List<com.by.tsgl.bean.Message>
     * @throws
     */
    @ResponseBody
    @GetMapping("/admin/messages")
    public List<Message> getMsgs(@RequestParam(value = "account",defaultValue = "null") String account) {
        return infoService.getMsgOrReply(account,MODE.LEAVE_MESSAGE.getVal());
    }

    /**
     * @description 返回已经某个用户已借图书集合
     * @author leibaoyu
     * @date 2021/11/16 23:14
     * @param account
     * @return java.util.List<com.by.tsgl.bean.Book>
     * @throws
     */
    @ResponseBody
    @GetMapping("/borrowbooklist")
    public List<Map<String,Object>> getBorrowedBooks(@RequestParam("account") String account) throws ParseException {
        return infoService.getBorrowedBooks(account);
    }

    @ResponseBody
    @GetMapping("/admin/allborrowedbooks")
    public List<Map<String,Object>> getAllBorrowedBooks(){
        return infoService.getAllBorrowedBooks();
    }

    /**
     * @description 读者获取个人信息
     * @author leibaoyu
     * @date 2021/11/24 10:06
     * @param account
     * @return java.util.Map<java.lang.String, java.lang.Object>
     * @throws
     */
    @ResponseBody
    @RequestMapping("/myinfo")
    public Map<String,Object>getMyInfo(@RequestParam("account") String account){
        return infoService.getMyInfo(account);
    }
}

