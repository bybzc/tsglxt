package com.by.tsgl.service;

import com.alipay.easysdk.factory.Factory;
import com.alipay.easysdk.kernel.util.ResponseChecker;
import com.alipay.easysdk.payment.common.models.AlipayTradeRefundResponse;
import com.alipay.easysdk.payment.page.models.AlipayTradePagePayResponse;
import com.alipay.easysdk.payment.wap.models.AlipayTradeWapPayResponse;
import com.by.tsgl.bean.*;
import com.by.tsgl.mapper.InfoMapper;
import com.by.tsgl.mapper.ReaderMapper;
import com.by.tsgl.mapper.RoleMapper;
import com.by.tsgl.mapper.TradeMapper;
import com.by.tsgl.util.MODE;
import com.by.tsgl.util.OrderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author leibaoyu
 * @version 1.0
 * @date  2021/11/4 14:21
 */
@Service
public class TradeService {
    Logger log = LoggerFactory.getLogger(TradeService.class);
    @Autowired
    private TradeMapper tradeMapper;
    @Autowired
    private InfoService infoService;
    @Autowired
    private BookService bookService;
    @Autowired
    private InfoMapper infoMapper;
    @Autowired
    private ReaderMapper readerMapper;

    @Value("${alipay.returnUrl}")
    private String returnUrl;

    @Value("${alipay.notifyUrl}")
    private String notifyUrl;
    @Value("${alipay.errorUrl}")
    private String errorUrl;
    @Value("$alipay.refundUrl")
    private String refundUrl;
    @Value("$alipay.quitUrl")
    private String quitUrl;
    @Value("${ourConfig.collectionAccount}")
    private String collectionAccount;

    /**
     * @description 支付接口，优先从余额扣除
     * @author leibaoyu
     * @date 2021/11/11 23:14
     * @param account
     * @param map
     * @param subject
     * @return java.lang.String
     * @throws
     */
    public String payByDeposit(String account,String totalAmount,String subject,Map map) {
//        Map<String,Object> res=new HashMap<>();

        Reader reader = infoMapper.getReaderByAccount(account);

        if (reader == null) {
//            res.put("error2", "该用户没有读者证，请重新确认信息!");
            return "该用户没有读者证，请重新确认信息!";
        }
        double balance = Double.parseDouble(reader.getDeposit_num()) - Double.parseDouble(totalAmount);

        if (subject.equals(MODE.S_DEPOSIT.getStr())) {           //缴纳押金
            if (balance >= 0) {                                 //用户余额比押金多
                String outTradeNo = OrderUtil.getOrderNo(reader.getReader_id());        //生成商户的订单号

                //产生交易记录
                Deal deal = new Deal(reader.getReader_id(), outTradeNo, totalAmount, subject, collectionAccount, null);
                deal.setDealState("已支付");
                deal.setPay_method("余额");
                tradeMapper.createOrder(deal);      //创建订单

                reader.setDeposit_num(String.valueOf(Double.parseDouble(reader.getDeposit_num())-Double.parseDouble(deal.getTotalAmount())));
                int level=1;
                double tp=Double.parseDouble(reader.getDeposit_num());

                if(tp<=50)
                    level=2;
                else if(tp>50&&tp<=100)
                    level=3;
                else if(tp>100&&tp<=200)
                    level=4;
                else if(tp>=300)
                    level=5;
                reader.setGrade_id(String.valueOf(level));

                //更新读者的余额和等级
                readerMapper.updateReader(reader);


                return "已从用户余额扣除";
            } else {        //余额不足调起支付接口
              return  pagePay(account, totalAmount, subject);
            }
        }
        else
            if (subject.equals(MODE.S_FINE.getStr())) {          //缴纳罚金
            if (balance >= 0) {                                     //如果余额充足
                String outTradeNo = OrderUtil.getOrderNo(reader.getReader_id());        //创建订单
                Deal deal = new Deal(reader.getReader_id(), outTradeNo, totalAmount, subject, collectionAccount, null);
                deal.setDealState("已支付");
                deal.setPay_method("余额");
                tradeMapper.createOrder(deal);

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


                //封装罚款记录
                Fine fine = new Fine();
                fine.setFine_time(sdf.format(new Date()));
                fine.setFine_amount(Double.parseDouble(deal.getTotalAmount()));
                fine.setFine_out_trade_no(outTradeNo);

                //更新订单状态并创建罚款记录
                tradeMapper.updateOrderState(MODE.PAID.getVal(), sdf.format(new Date()), null, outTradeNo);
                tradeMapper.createFine(fine);

                //更新borrow状态
                //更新reader状态
                reader.setReader_state("正常");
                reader.setDeposit_num(String.valueOf(balance));

                int level=1;
                double tp=Double.parseDouble(reader.getDeposit_num());

                if(tp<=50)
                    level=2;
                else if(tp>50&&tp<=100)
                    level=3;
                else if(tp>100&&tp<=200)
                    level=4;
                else if(tp>=300)
                    level=5;
                reader.setGrade_id(String.valueOf(level));

                //更新读者的余额和等级
                readerMapper.updateReader(reader);


                Map<String,Object>books=( Map<String,Object>)map.get("books");
                for(String book_id:books.keySet()){
                    String state = books.get(book_id).toString();

                    //更新图书状态，并还书
                    switch (state) {
                        case "图书逾期","图书损坏" -> {
                            tradeMapper.updateBookState(MODE.IN_THE_MUSEUM.getStr(), book_id);
                        }
                        case "图书丢失" -> {
                            tradeMapper.updateBookState(MODE.LOSS.getStr(), book_id);
                        }
                    }
                }

                //更新订单状态
                List<String>borrowList=(ArrayList<String>)map.get("borrowList");
                for(String borrow_id:borrowList){
                    tradeMapper.updateBorrowState("已缴纳罚金", fine.getFine_time(), borrow_id); //更新借阅记录
                }




                return "已从用户余额扣除";
            } else {
                pagePay(account, totalAmount, subject);
            }
        }
//        res.put("error","未知错误");
        return "未知错误";
    }


    /**
     * @description 网页支付
     * @author leibaoyu
     * @date 2021/11/4 20:11
     * @param account
     * @param totalAmount
     * @return java.lang.String
     * @throws
     */
    public String pagePay(String account,String totalAmount,String subject) {
        //returnUrl:同步通知地址
        String reader_id=infoService.getReaderIdByAccount(account);
        if(reader_id==null)
            return null;
        String outTradeNo= OrderUtil.getOrderNo(reader_id);
        String tradeNo=null;       //先生成订单，交易流水号后续更新。
        try {
            AlipayTradePagePayResponse response = Factory.Payment.Page().pay(subject, outTradeNo, totalAmount, returnUrl);
//            Factory.Payment.Common().asyncNotify(notifyUrl);
            if (ResponseChecker.success(response)) {
                log.info("调用成功！");
                Deal deal=new Deal(reader_id,outTradeNo,totalAmount,subject,collectionAccount,tradeNo);
                tradeMapper.createOrder(deal);

                return response.getBody();
            } else {
                log.error("调用失败！原因：" + response.getBody());
            }
        } catch (Exception e) {
            log.error("调用异常！原因：" + e.getMessage());
        }
        return null;
    }


    /**
     * @description 手机端支付
     * @author leibaoyu
     * @date 2021/11/4 20:15
     * @param subject
     * @param outTradeNo
     * @param totalAmount
     * @return java.lang.String
     * @throws
     */
    public String wapPay(String subject, String outTradeNo, String totalAmount) {
        //returnUrl:同步通知地址

        try {
            AlipayTradeWapPayResponse response = Factory.Payment.Wap().pay(subject,outTradeNo,totalAmount,quitUrl,returnUrl);
            if (ResponseChecker.success(response)) {
                log.info("调用成功！");
                return response.getBody();
            } else {
                log.error("调用失败！原因：" + response.getBody());
            }
        } catch (Exception e) {
            log.error("调用异常！原因：" + e.getMessage());
        }
        return null;
    }

    public String query(String outTradeNo) {
        return null;
    }

    /**
     * @description 付款成功后异步验证
     * @author leibaoyu
     * @date 2021/11/8 20:21
     * @param params
     * @return boolean
     * @throws
     */
    public String returCall(Map<String, String> params) {
        // 1.获取回调参数

        Logger log = LoggerFactory.getLogger(TradeService.class);
        log.info("支付宝异步验签");
        // 2.验签
        try {
            if (Factory.Payment.Common().verifyNotify(params)) {
                log.info("异步验签通过,交易成功");


                /***异步验签结束，对数据库进行更新*/
                String trade_no = params.get("trade_no");
                String out_trade_no = params.get("out_trade_no");
//                Factory.Payment.Page().asyncNotify(notifyUrl);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-hh HH:mm:ss");
                Deal deal = infoService.getDealByOutTradeNo(out_trade_no);      //获取对应订单并对其进行更新

//                if (deal.getSubject().equals(MODE.S_DEPOSIT.getStr())) {                                //缴纳押金
//                    tradeMapper.updateOrderState(MODE.PAID.getVal(), sdf.format(new Date()), trade_no, out_trade_no);   //更新订单状态
//                }else
                   if(deal.getSubject().equals(MODE.S_RECHARGE.getStr())){                           //余额充值
                    tradeMapper.updateOrderState(MODE.PAID.getVal(), sdf.format(new Date()), trade_no, out_trade_no);   //更新订单状态

                    Reader reader=readerMapper.getReaderByReaderId(deal.getReader_id());
                    reader.setDeposit_num(String.valueOf(Double.parseDouble(deal.getTotalAmount())+Double.parseDouble(reader.getDeposit_num())));

                       int level=1;
                       double tp=Double.parseDouble(reader.getDeposit_num());

                       if(tp<=50)
                           level=2;
                       else if(tp>50&&tp<=100)
                           level=3;
                       else if(tp>100&&tp<=200)
                           level=4;
                       else if(tp>=300)
                           level=5;


                    reader.setGrade_id(String.valueOf(level));

                    //更新读者的余额和等级
                    readerMapper.updateReader(reader);
                }
                else if (deal.getSubject().equals(MODE.S_FINE.getStr())) {                            //缴纳罚金

                    Map map = getFineBaseData(deal.getReader_id());                                     //获得罚金的基本信息

                    Fine fine = new Fine();                                                             //创建罚金对象
                    fine.setFine_time(sdf.format(new Date()));
                    fine.setFine_amount(Double.parseDouble(deal.getTotalAmount()));
                    fine.setFine_out_trade_no(out_trade_no);

                    tradeMapper.updateOrderState(MODE.PAID.getVal(), sdf.format(new Date()), trade_no, out_trade_no);   //更新订单
                    tradeMapper.createFine(fine);                                                                       //创建罚金记录

                    Reader reader = new Reader();
                    reader.setReader_id(deal.getReader_id());
                    reader.setReader_state("正常");
                    readerMapper.updateReader(reader);

                    Map<String, Object> books = (Map<String, Object>) map.get("books");
                    for (String book_id : books.keySet()) {
                        String state = books.get(book_id).toString();

                        //更新图书状态，并还书
                        switch (state) {
                            case "图书逾期", "图书损坏" -> {
                                tradeMapper.updateBookState(MODE.IN_THE_MUSEUM.getStr(), book_id);
                            }
                            case "图书丢失" -> {
                                tradeMapper.updateBookState(MODE.LOSS.getStr(), book_id);
                            }
                        }
                    }

                    //更新订单状态
                    List<String> borrowList = (ArrayList<String>) map.get("borrowList");
                    for (String borrow_id : borrowList) {
                        tradeMapper.updateBorrowState("已缴纳罚金", fine.getFine_time(), borrow_id); //更新借阅记录
                    }
                }
                return notifyUrl;
            }
        } catch (Exception e) {
            log.debug("支付宝验签异常：{}", e.getMessage());
        }
        return "error";
    }



    /**
     * @description 退款
     * @author leibaoyu
     * @date 2021/11/8 20:55
     * @param deal_id
     * @return java.lang.String
     * @throws
     */
    public String refund(String deal_id,String reason){
        Deal deal= infoService.getDealById(deal_id);
        if(deal==null)
            return errorUrl+"?msg="+"申请失败，交易不存在!";
        if(!deal.getDealState().equals("交易成功"))
            return errorUrl+"?msg="+"退款失败，当前订单并未生效。";
        if(deal.getDealState().equals("已退款"))
            return errorUrl+"?msg="+"该订单已全额退款。";
        Logger log =LoggerFactory.getLogger(TradeService.class);
        log.info("退款开始");
        //退款
        try{
            AlipayTradeRefundResponse response = Factory.Payment.Common().refund(deal.getOutTradeNo(), deal.getTotalAmount());
            if(response.getMsg().equals("Success")){

                double amount=Double.parseDouble(deal.getTotalAmount());

                Refund rf=new Refund();
                rf.setSubject("退款");
                rf.setState("退款成功");
                rf.setAmount(amount);
                rf.setMethod("支付宝");
                rf.setTrade_no(deal.getTradeNo());
                rf.setRemark(reason);

                tradeMapper.createRefund(rf);
                String refund_id=infoService.getRefundByTradeNo(deal.getTradeNo()).getRefund_id();

                tradeMapper.updateOrderRefund(refund_id,deal.getOutTradeNo());

                SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-hh HH:mm:ss");
                tradeMapper.updateOrderState(MODE.REFUND.getVal(),sdf.format(new Date()),deal.getTradeNo(),deal.getOutTradeNo());
                log.info("退款成功!");
                return refundUrl+ "?msg="+"退款成功!";
            }
            else {
                log.info("系统异常");
                return errorUrl+"?msg="+"系统异常";
            }
        } catch (Exception e) {
            log.debug("退款失败：{}", e.getMessage());
        }
        return errorUrl+"?msg="+"退款失败!";
    }

    /**
     * @description 获取罚单的基本信息
     * @author leibaoyu
     * @date 2021/11/11 23:22
     * @param reader_id
     * @return java.util.Map
     * @throws
     */
    public Map getFineBaseData(String reader_id) {
        Map<String, Object> map = new HashMap<>();

        Map<String,Object> books = new HashMap<>();
        List<String>borrows=new ArrayList<>();
        String error = "";

        List<Borrow> borrowList = tradeMapper.getFineBorrow(reader_id);
        double totalAmount = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for (Borrow borrow : borrowList) {              //遍历问题记录
            String state = borrow.getBorrow_state();
            FineRule fineRule = tradeMapper.getFineRuleByState(state);        //根据状态获取罚金
            Book book = bookService.searchBookById(borrow.getBook_id());      //根据记录获取问题的书籍
            if (book == null) {
                error = error + borrow.getBook_id() + ",";
                continue;
            }
            double dayAmount = fineRule.getFrule_amount();
            double ratio = fineRule.getFrule_ratio() / 100;

            switch (state) {
                case "图书逾期" -> {
                    int between_days = 0;                                               //逾期天数
                    try {
                        Date shouldReturn = sdf.parse( borrow.getShould_return());       //应当归还的日期
                        between_days=(int) ((new Date().getTime() - shouldReturn.getTime()) / (1000*3600*24));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    double tpAmount = dayAmount * (between_days);
                    if (tpAmount > book.getPrice())
                        tpAmount = book.getPrice();
                    totalAmount += tpAmount;
                }
                case "图书丢失" -> totalAmount += book.getPrice() * (1 + ratio);
                case "图书损坏" -> totalAmount += book.getPrice() * ratio;
            }

            books.put(borrow.getBook_id(),state);//将问题列表加入
            borrows.add(borrow.getBorrow_id());
        }
        map.put("totalAmount", totalAmount);         //罚金金额
        map.put("borrowList", borrows);           //问题记录列表
        map.put("books", books);                    //问题书本

        return map;
    }
}
