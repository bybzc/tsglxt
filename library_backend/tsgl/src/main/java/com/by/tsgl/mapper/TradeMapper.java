package com.by.tsgl.mapper;
/**
 * @author leibaoyu
 * @version 1.0
 * @date 2021/11/3 21:03
 */
import com.by.tsgl.bean.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TradeMapper {
    /**交易模块*/
    //创建订单
    public void createOrder(Deal deal);
    //更新订单状态
    public void updateOrderState(int state,String date,String trade_no,String out_trade_no);


    /**退款模块*/
    //创建退款信息
    public void createRefund(Refund refund);
    //更新退款的记录
    public void updateOrderRefund(String refund_id,String out_trade_no);

    /**罚款模块*/
    //创建罚款信息
    public void createFine(Fine fine);
    //获取罚款对应的借阅记录
    public List<Borrow> getFineBorrow(String reader_id);
    //获取罚款对应的规则
    public FineRule getFineRuleByState(String state);
    //更新借阅状态
    public void updateBorrowState(String state,String time,String borrow_id);
    public void updateBookState(String state,String book_id);

}
