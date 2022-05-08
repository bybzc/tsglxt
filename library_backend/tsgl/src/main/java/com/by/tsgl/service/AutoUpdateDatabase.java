package com.by.tsgl.service;

import com.by.tsgl.bean.*;
import com.by.tsgl.mapper.BookMapper;
import com.by.tsgl.mapper.DataStateMapper;
import com.by.tsgl.mapper.RoleMapper;
import com.by.tsgl.util.MODE;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.websocket.EncodeException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author leibaoyu
 * @version 1.0
 * @date 2021/11/15 16:24
 */
@Component
@EnableScheduling //开启定时任务
@Slf4j
public class AutoUpdateDatabase {
    @Autowired
    private DataStateMapper dataStateMapper;
    @Autowired
    private BookMapper bookMapper;
    @Autowired
    private RoleMapper roleMapper;


    @Scheduled(cron = "0 */10 * * * ?")
    public void updateBorrowState() throws ParseException, IOException, EncodeException {

        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdf_get=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        log.info(sdf.format(new Date())+" 检查借阅状态");
        //获取借书逾期的记录
        List<Map<String,Object>> badResults= dataStateMapper.checkBadBorrowState();
        List<String> accounts=new ArrayList<>();        //账户记录
        List<Message>messageList=new ArrayList<>();     //每个账户对应的通知

        for(Map<String,Object>map:badResults) {
            Message message = new Message();              //将通知封装成message对象
            Borrow borrow = new Borrow();                 //将信息封装到borrow
            borrow.setBorrow_id(map.get("borrow_id").toString());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(sdf_get.parse(map.get("should_return").toString()));
            calendar.add(Calendar.DATE, Integer.parseInt(map.get("renew_day").toString()));

            Reader reader = new Reader();
            reader.setReader_id(map.get("reader_id").toString());
            Book book=bookMapper.getBookById(map.get("book_id").toString());

            //图书剩余可续借次数
            int surplusNums = Integer.parseInt(map.get("surplus_renew_num").toString()) - Integer.parseInt(map.get("used_renew_num").toString());
            //获取账户信息
            String account = roleMapper.getUserById(map.get("user_id").toString()).getAccount();

            //如果用户还有续借次数，自动续借
            if (surplusNums > 0) {
                //更新借阅记录
                borrow.setRenew_num(Integer.parseInt(map.get("used_renew_num").toString()) + 1);
                borrow.setShould_return(sdf.format(calendar.getTime()));
                message.setContent(String.format("用户成功自动续借，剩余续借次数为%d，请在%s前将图书%s归还。", surplusNums-1, borrow.getShould_return(),book.getNameOfBook()));
            }
            //如果用户没有续借次数，更改图书借阅状态为图书逾期，并更改读者状态为 未按时间归还图书
            else if (surplusNums == 0) {
                borrow.setBorrow_state(MODE.TS_LATE.getStr());
                borrow.setRenew_num(Integer.parseInt(map.get("used_renew_num").toString()));

                    reader.setReader_state("未按规定时间还书");
                    roleMapper.updateReader(reader);
                    message.setContent(String.format("图书%s已经在%s逾期，请尽快归还，以免产生更多的费用。",book.getNameOfBook() ,map.get("should_return").toString()));

            }
            bookMapper.updateBorrow(borrow);


            accounts.add(account);
            //补充message的信息
            message.setDate(sdf.format(new Date()));
            message.setFather_id(map.get("user_id").toString());
            message.setTitle("系统通知");
            message.setState("未读");
            messageList.add(message);

            // 去除重复信息，防止重复通知
            HashSet set = new HashSet(messageList);
            messageList.clear();
            messageList.addAll(set);
        }

        //如果信息列表是空，就跳过发送，如果非空就向用户发送信息
        if(!messageList.isEmpty()) {
            dataStateMapper.insertNotify(messageList);
            WebSocketServer webSocketServer = new WebSocketServer();
            webSocketServer.sendInfo(accounts);
        }

        //向用户发送通知
        log.info("检查完毕...");
    }

    /**
     * @description 删除超过15分钟未支付的订单
     * @author leibaoyu
     * @date 2021/11/18 11:19
     * @param
     * @return void
     * @throws
     */
    @Scheduled(cron = "0 */15 * * * ?")
    public void updateOrderState(){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        log.info(sdf.format(new Date())+" 检查订单状态");
        List<Deal> badResults= dataStateMapper.checkBadDealState();
        for(Deal deal:badResults){
            dataStateMapper.deleteBadDeal(deal.getDeal_id());
        }
        log.info(sdf.format(new Date())+" 检查订单完成");
    }

    @Scheduled(cron = "0 0 */1 * * ?")
    public void updateReserveState(){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        log.info(sdf.format(new Date())+" 检查预定状态");
        List<Map<String,Object>> badResults= dataStateMapper.checkBadReserveState();
        for(Map<String,Object>map:badResults){
           dataStateMapper.deleteBadBorrow(map.get("borrow_id").toString());
           dataStateMapper.updateBook(map.get("b_id").toString());
        }
        log.info(sdf.format(new Date())+" 检查预定信息完成");
    }

}

