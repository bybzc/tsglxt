package com.by.tsgl.mapper;

import com.by.tsgl.bean.Borrow;
import com.by.tsgl.bean.Deal;
import com.by.tsgl.bean.Message;
import com.by.tsgl.bean.Reader;
import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author leibaoyu
 * @version 1.0
 * @date 2021/11/15 16:30
 */
@Mapper
public interface DataStateMapper {
    public List<Map<String, Object>> checkBadBorrowState();

    public List<Deal> checkBadDealState();

    public void deleteBadDeal(String deal_id);

    public List<Map<String, Object>> checkBadReserveState();
    public void deleteBadBorrow(String borrow_id);
    public void updateBook(String book_id);

    public void insertNotify(List<Message> msg);
    public List<Message>getNotify(String account, String state);
    public void updateNotify(Message message);
}
