package com.by.tsgl.mapper;
/**
 * @author leibaoyu
 * @version 1.0
 * @date 2021/11/3 18:03
 */

import com.by.tsgl.bean.Book;
import com.by.tsgl.bean.Borrow;
import com.by.tsgl.bean.Reader;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.List;

@Mapper
public interface BookMapper {

    //  基本图书增删改查
    public void addBook(Book book);

    public void deleteBook(String book_id);

    public void modifyBook(Book b);

    public ArrayList<Book> searchBooks(String param, int mode);

    public ArrayList<Book> searchBooksByIds(ArrayList<String> param);

    public Book getBookById(String book_id);


    //  更新借阅记录
    public int getBorrowTime(String reader_id);

    public Book judgeBookById(String book_id);                  //判断图书是否可借出

    public void insertBorrow(Borrow borrow);

    public void updateBook(String book_id);

    public void updateReader(String reader_id);

    //  续借
    public Borrow getReaderBorrowRecord(String reader_id, String book_id);

    public void updateBorrow(Borrow borrow);

    //  归还图书
    public void updateBorrowReturn(String book_id, String reader_id,String returnDate);                     //更新借阅记录的状态

    public void updateBookReturn(String book_id, String reader_id);     //更新图书的状态

    public boolean canBookReturned(String book_id, String reader_id);    //判断图书是否可归还


    //  预定图书
    public void updateReserve(String book_id,String state,String preState);                      //更新图书状态为 预定

    public Book judgeBookReserve(String book_id);                   //判断图书是否可以被预定

    public Reader getReaderRights(String reader_id);                //获取该读者的权益

    public List<Borrow>getReserveRecord(String reader_id);          //获得预定记录
}
