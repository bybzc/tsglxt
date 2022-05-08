package com.by.tsgl.service;

import com.by.tsgl.bean.*;
import com.by.tsgl.mapper.AccountMapper;
import com.by.tsgl.mapper.DataStateMapper;
import com.by.tsgl.mapper.InfoMapper;
import com.by.tsgl.mapper.ReaderMapper;
import com.by.tsgl.mapper.RoleMapper;
import com.by.tsgl.util.MODE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class InfoService {
    @Autowired
    private InfoMapper infoMapper;
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private BookService bookService;
    @Autowired
    private ReaderMapper readerMapper;
    @Autowired
    private AccountMapper accountMapper;
    @Autowired
    private DataStateMapper dataStateMapper;


    /**
     * @description 管理员添加公告
     * @author leibaoyu
     * @date 2021/11/12 13:25
     * @param announcement
     * @return boolean
     * @throws
     */
    public boolean addAnnouncement(Announcement announcement) {
        String admin_id=announcement.getAnnouncement_publisher_id();
        Admin admin= roleMapper.getAdminById(admin_id);
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        announcement.setAnnouncement_date(sdf.format(new Date()));
        if(admin==null)
            return false;
        infoMapper.addAnnouncement(announcement);
        return true;
    }

    /**
     * @description 删除公告，支持批量删除
     * @author leibaoyu
     * @date 2021/11/13 18:54
     * @param announcement_id
     * @return java.util.List<java.lang.String>
     * @throws
     */
    public List<String> delAnnouncement(List<String> announcement_id) {
        List<String> del_id = new LinkedList<>();          //成功删除的列表
        for (String id : announcement_id) {
            Announcement announcement = infoMapper.getAnnouncementById(id);
            if (announcement != null) {
                infoMapper.delAnnouncement(id);
                del_id.add(id);
            }
        }
        return del_id;
    }

    /**
     * @description 根据管理员id获取相关的所有公告
     * @author leibaoyu
     * @date 2021/11/12 10:48
     * @param admin_id
     * @return java.util.List<com.by.tsgl.bean.Announcement>
     * @throws
     */
    public List<Announcement> getAnnouncementsByAdminId(String admin_id) {
        return infoMapper.getAdminAnnouncement(admin_id);
    }
    /**
     * @description 跟据公告降序排序获取最新的几份公告
     * @author leibaoyu
     * @date 2021/11/12 10:49
     * @param size
     * @return java.util.List<com.by.tsgl.bean.Announcement>
     * @throws
     */
    public List<Announcement> getLatestAnnouncementByNum(int size){
        return infoMapper.getLatestAnnouncement(size);
    }

    /**
     * @description 修改公告
     * @author leibaoyu
     * @date 2021/11/13 19:31
     * @param announcement
     * @return boolean
     * @throws
     */
    public boolean modifyAnnouncement(Announcement announcement) {
        if (infoMapper.getAnnouncementById(announcement.getAnnouncement_id()) != null) {
            infoMapper.modifyAnnouncement(announcement);
            return true;
        }
        return false;
    }

    /**
     * @description 获取排行榜----前10名
     * @author leibaoyu
     * @date 2021/11/13 21:58
     * @param
     * @return java.util.Map<java.lang.Integer, java.util.Map < java.lang.String, java.lang.Object>>
     * @throws
     */
    public Map<Integer,Map<String,Object>> getRankList() {
        List<Map<String,Object>>list= infoMapper.getRankList();
        Map<Integer,Map<String,Object>>rankLists=new HashMap<>();
        for(int i=0;i<list.size();i++){
            Book book=bookService.searchBookById(list.get(i).get("book_id").toString());
            Map<String,Object>m=new HashMap<>();
            m.put("book",book);
            m.put("借出次数",list.get(i).get("num"));
            rankLists.put(i+1,m);
        }

        return rankLists;
    }


    /**
     * @description 留言或者回复信息
     * @author leibaoyu
     * @date 2021/11/13 22:54
     * @param msg
     * @param mode 决定留言或者回复
     * @return boolean
     * @throws
     */
    public boolean msgOrReply(Message msg,int mode) {
        if(mode== MODE.LEAVE_MESSAGE.getVal()){
            if(roleMapper.getUserById(msg.getFather_id())==null)
                return false;
            infoMapper.leaveMsg(msg);
            return true;
        }
        if(mode== MODE.REPLY_MESSAGE.getVal()) {            //先判断是否已经回复过，然后再进行回复
            Message m = infoMapper.getMsgsByMsgId(msg.getFather_id());
            if(m==null)
                return false;
            if(m.getState().equals("已读"))
                return false;
            infoMapper.replyMsg(msg);
            infoMapper.updateMsgState(msg.getFather_id());
            return true;
        }
        return false;
    }

    /**
     * @description 获取信息
     * @author leibaoyu
     * @date 2021/11/14 0:16
     * @param account   当为"null"时获取所有留言
     * @param mode
     * @return java.util.List<com.by.tsgl.bean.Message>
     * @throws
     */
    public List<Message> getMsgOrReply(String account,int mode) {

        User u=roleMapper.getUserByAccount(account);
        if(mode==MODE.REPLY_MESSAGE.getVal()){
            String user_id=String.valueOf(u.getUser_id());
            return infoMapper.getReplyByUserID(user_id);
        }
        //只有获取所有留言的时候才能将account设为"null"
        if(mode==MODE.LEAVE_MESSAGE.getVal()){
            if(account.equals("null"))
                return infoMapper.getAllMsgs();
            String user_id=String.valueOf(u.getUser_id());
            List<Message>p=infoMapper.getReplyByUserID(user_id);
            List<Message>t=new ArrayList<>();
            for(Message m:p){
                t.add(infoMapper.getMsgsByMsgId(m.getFather_id()));
            }

            return t;
        }
        return null;
    }

    /**
     * @description 获取读者id
     * @author leibaoyu
     * @date 2021/11/8 20:23
     * @param account
     * @return java.lang.String
     * @throws
     */
    public String getReaderIdByAccount(String account){
       Reader reader = infoMapper.getReaderByAccount(account);
       if(reader==null)
           return null;
       return reader.getReader_id();
    }

    /**
     * @description 获取订单号
     * @author leibaoyu
     * @date 2021/11/8 21:56
     * @param deal_id
     * @return com.by.tsgl.bean.Deal
     * @throws
     */
    public Deal getDealById(String deal_id){
        return infoMapper.getDealById(deal_id);
    }
    public Deal getDealByOutTradeNo(String out_trade_no){
        return infoMapper.getDealByOutTradeNo(out_trade_no);
    }

    /**
     * @description 根据交易流水号获取退款记录
     * @author leibaoyu
     * @date 2021/11/11 11:18
     * @param trade_no
     * @return com.by.tsgl.bean.Refund
     * @throws
     */
    public Refund getRefundByTradeNo(String trade_no){
        return infoMapper.getRefund(trade_no);
    }

    /**
     * @description 根据account获取罚金信息
     * @author leibaoyu
     * @date 2021/11/11 11:19
     * @param account
     * @return com.by.tsgl.bean.Fine
     * @throws
     */
    public Fine getFineById(String account){
        String reader_id= getReaderIdByAccount(account);
        if(reader_id==null)
            return null;
        return infoMapper.getFine(reader_id);
    }

    /**
     * @description 返回已经某个用户已借图书集合
     * @author leibaoyu
     * @date 2021/11/16 23:14
     * @param account
     * @return java.util.List<com.by.tsgl.bean.Book>
     * @throws
     */
    public List<Map<String,Object>> getBorrowedBooks(String account) throws ParseException {
        if (roleMapper.getUserByAccount(account) != null) {
            String userId = accountMapper.getUserId(account);
            String readerId = readerMapper.getReaderId(userId);

            List<Map<String, Object>> mapList = infoMapper.getBorrowedBooks(readerId);

            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String[] rc = new String[]{"borrow_time", "should_return", "return_time"};
            for (Map<String, Object> map : mapList) {
                for (String tp : rc) {
                    if (map.containsKey(tp))
                        map.put(tp, sdf2.format(sdf1.parse(map.get(tp).toString())));
                }
            }
            return mapList;
        }
        return null;
    }

    /**
     * @description 首页的信息展示
     * @author leibaoyu
     * @date 2021/11/23 16:26
     * @param account
     * @return java.util.Map<java.lang.String, java.lang.Object>
     * @throws
     */
    public Map<String,Object> getIndexMsg(String account){
        Map<String,Object>rc=new HashMap<>();
        try {
            int bookNum = infoMapper.getBookNum();
            int accessNum= infoMapper.getAccessNum();
            int notifyNum=dataStateMapper.getNotify(account,null).size();


            double book_not_in=infoMapper.getBookByState(MODE.LEND.getStr(),null).size();
            double connot_read=infoMapper.getBookByState(null,"严重受损").size();
            double book_in_canread=infoMapper.getBookByState(MODE.IN_THE_MUSEUM.getStr(),null).size()-connot_read;


            rc.put("bookNum",bookNum);
            rc.put("accessNum",accessNum);
            rc.put("notifyNum",notifyNum);

            DecimalFormat df=new DecimalFormat("0.00");

            rc.put("book_in_can_read",df.format(book_in_canread/bookNum*100));
            rc.put("book_not_in",df.format(book_not_in/bookNum*100));
            rc.put("can_not_read",df.format(connot_read/bookNum*100));

        }catch (Exception e){
            System.out.println(e);
            rc.put("error","未知错误");
        }
        return rc;
    }

    //获取发布过公告的管理员信息
    public List<Map<String,Object>> getAnnouncementAdminInfo() {
         return infoMapper.getAnnouncementAdminInfo();
    }


    public User getUserInfo(String account) {
        User u = accountMapper.getUserInfo(account);
        u.setPassword("");
        return u;
    }

    public Map<String,Object> getMyInfo(String account){
        if(accountMapper.getUserId(account)==null){
            Map map=new HashMap<>();
            map.put("error","账户不存在");
            return map;
        }

        return roleMapper.getCommonUserInfoByAccount(account);
    }

    public List<Map<String,Object>> getAllBorrowedBooks(){
        return infoMapper.getAllBorrowedBooks();
    }
}
