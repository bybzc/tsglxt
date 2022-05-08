package com.by.tsgl.mapper;

import com.by.tsgl.bean.*;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mapper
public interface InfoMapper {



    /**增删改查基本功能*/
    //添加公告。
    void addAnnouncement(Announcement announcement) ;
    //删除公告
    void delAnnouncement( String announcement_id) ;
    //修改公告
    void modifyAnnouncement( Announcement announcement) ;
    //获取最新的 size 个公告
    List<Announcement> getLatestAnnouncement(int size);
    //获取某个管理员发布的公告
    List<Announcement> getAdminAnnouncement(String admin_id);
    //根据公告id获取公告
    Announcement getAnnouncementById(String announcement_id);

    /***图书借阅排行榜*/
    List<Map<String, Object>> getRankList();

    /***留言模块*/
    void leaveMsg(Message msg);
    void replyMsg(Message msg);
    Message getMsgsByMsgId(String msg_id) ;
    void updateMsgState(String msg_id);
    List<Message> getReplyByUserID(String user_id);
    List<Message> getAllMsgs();

    //获取读者id
    Reader getReaderByAccount(String account);
    //获取用户交易记录
    Deal getDealById(String deal_id);
    Deal getDealByOutTradeNo(String out_trade_no);
    //获取退款记录
    Refund getRefund(String trade_no);
    //返回罚款记录
    Fine getFine(String reader_id);
    //获取已经借的书籍
    List<Map<String,Object>> getBorrowedBooks(String reader_id);

     //获取发布过公告的管理员信息
    List<Map<String,Object>> getAnnouncementAdminInfo();

    //获取图书馆内的图书
    int getBookNum();
    //获取访问量
    int getAccessNum();
    //b_situation默认传空值即可  根据图书状态获取数量
    List<Book>getBookByState(String state,String b_situation);

    List<Map<String,Object>> getAllBorrowedBooks();
}
