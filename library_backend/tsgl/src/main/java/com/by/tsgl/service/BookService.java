package com.by.tsgl.service;

import com.by.tsgl.bean.Book;
import com.by.tsgl.bean.Borrow;
import com.by.tsgl.bean.Reader;
import com.by.tsgl.mapper.BookMapper;
import com.by.tsgl.mapper.ReaderMapper;
import com.by.tsgl.util.MODE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class BookService {

//    @Autowired
//    AdminMapper adminMapper;

    @Autowired
    BookMapper bookMapper;

    @Autowired
    InfoService infoService;

    @Autowired
    ReaderMapper readerMapper;

    //    添加图书
    public boolean addBook(Book book) {
        if (book == null)
            return false;
        bookMapper.addBook(book);
        return true;
    }

    /**
     * 通过不同模式匹配图书信息，适合模糊搜索
     * null          mode=0   返回全部信息
     * nameOfBook    mode=1
     * author        mode=2
     * press         mode=3
     * isbn          mode=4
     * keywords      mode=5
     * call_number   mode=6
     */

//    获取图书信息 mode=0
    public ArrayList<Book> getAllBooks() {
        return bookMapper.searchBooks("", MODE.ALL.getVal());
    }

    //    查询图书，关键字  mode=[1,6]
    public ArrayList<Book> searchBooks(Map o) {
        String nameOfBook = o.get("nameOfBook").toString();
        String author = o.get("author").toString();
        String press = o.get("press").toString();         //出版社
        String isbn = o.get("isbn").toString();
        String keyword = o.get("keywords").toString();        //主题词
        String call_number = o.get("call_number").toString();     //分类

        ArrayList<Book> books = new ArrayList<>();

        if (!nameOfBook.equals("")) {
            books.addAll(bookMapper.searchBooks(nameOfBook, MODE.NAME.getVal()));
        }
        if (!author.equals("")) {
            books.addAll(bookMapper.searchBooks(author, MODE.AUTHOR.getVal()));
        }
        if (!press.equals("")) {
            books.addAll(bookMapper.searchBooks(press, MODE.PRESS.getVal()));
        }
        if (!isbn.equals("")) {
            books.addAll(bookMapper.searchBooks(isbn, MODE.ISBN.getVal()));
        }
        if (!keyword.equals("")) {
            books.addAll(bookMapper.searchBooks(keyword, MODE.KEYWORD.getVal()));
        }
        if (!call_number.equals("")) {
            books.addAll(bookMapper.searchBooks(call_number, MODE.CALL_NUMBER.getVal()));
        }

        return books;
    }

    //    通过图书id查询图书
    public Book searchBookById(String book_id) {
        return bookMapper.getBookById(book_id);
    }

    public ArrayList<Book> searchBooksByIds(ArrayList<String> books) {
        return bookMapper.searchBooksByIds(books);
    }


    //      通过图书id删除
    public void deleteBook(String book_id) {
        bookMapper.deleteBook(book_id);
    }

    //    修改图书信息
    public void modifyIfo(Book book) {
        bookMapper.modifyBook(book);
    }

    //    借书
    @Transactional //作为事务管理
    public  Map<String,Object>  borrowBook(List<String> book_ids, String account) {
        Map<String,Object> returnMap=new HashMap<>();

        String reader_id = infoService.getReaderIdByAccount(account);
        if (reader_id == null){
            returnMap.put("isSuccess",false);
            returnMap.put("reason","NoReader");//账号未注册借阅证
            return returnMap;
        }

        int bookNum=book_ids.size(); //当前请求借阅的数目
//        System.out.println("借"+bookNum+"本书");
        //该账号允许借阅的数量
        int allowBorrowBookNum= readerMapper.getAllowBorrowNumByAccount(account);
        //该账号已借的数量
        int borrwingBookNum=readerMapper.getBorrowingNumByAccount(account);

        if(borrwingBookNum+ bookNum > allowBorrowBookNum){
            //借书数超过上限
            System.out.println("超过上限！正在借阅"+borrwingBookNum+"当前等级只允许借阅"+allowBorrowBookNum+"本书");
            returnMap.put("isSuccess",false);
            returnMap.put("reason","OverLimit");//账号超过借书限制
            returnMap.put("allowBorrowBookNum",allowBorrowBookNum);
            returnMap.put("borrwingBookNum",borrwingBookNum);
            return returnMap;
        }

        ArrayList<String> borrowIds = new ArrayList<>();                                   //判断图书是否可借出
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");      //设置借书日期
        returnMap.put("isSuccess",true);//默认为true 后面出错则调整为其他 最后返回
        List<String>nonExistBookIDs = new ArrayList<>();//不存在的id
        List<String>damagedBooks = new ArrayList<>();//严重受损的图书
        List<String>borrowedBooks = new ArrayList<>();//已借出的图书
        System.out.println("service ids"+book_ids);
        for (String book_id : book_ids) {
            //先获取书 获取不到则失败  获取到进行状态的判断  判断是否可以借书
            Book book = bookMapper.getBookById(book_id);
            if(book==null){
                returnMap.put("isSuccess",false);
                nonExistBookIDs.add(book_id);
                continue;
            }
            else {
                //有这本书
                String b_state = book.getState();
                System.out.println(book_id+b_state);

                if(b_state.equals("在馆")){
                    if(book.getSituation().equals("严重受损")){
                        damagedBooks.add(book_id);
                        continue;
                    }
                    else {
                        Borrow borrow = new Borrow();
                        Calendar calendar = Calendar.getInstance();
                        //根据借阅证获取第一次可借出的时长
                        int time = bookMapper.getBorrowTime(reader_id);
                        calendar.add(Calendar.DATE, time);
                        borrow.setBorrow_time(sdf.format(new Date()));                   //设置借书日期
                        borrow.setShould_return(sdf.format(calendar.getTime()));         //设置应该归还时间
                        borrow.setBorrow_state("借出");                                   //设置记录状态
                        borrow.setBook_id(book_id);                                      //书籍id
                        borrow.setReader_id(reader_id);                                  //读者id
                        borrow.setRenew_num(0);

                        try{
                            bookMapper.updateBook(book_id);
                            bookMapper.updateReader(reader_id);
                            bookMapper.insertBorrow(borrow);
                            borrowIds.add(book_id);
                        }catch (Exception e){
                            System.out.println("捕获到异常");
                            returnMap.put("isSuccess",false);
                            returnMap.put("reason","UnknownReason");//账号超过借书限制
                            //再次throw异常，确保springboot事务能够回滚
                            throw new RuntimeException(returnMap.toString()+"捕获异常："+e);
                        }

                    }

                } else if (b_state.equals("预定") ) {
                    //TODO 查询是否该用户预定的书 完成借阅
                    continue;
                }
                else if(b_state.equals("借出")){
                    borrowedBooks.add(book_id);
                    continue;
                }
            }
        }
        if(nonExistBookIDs.size()!=0){
            returnMap.put("isSuccess",false);
            returnMap.put("不存在的id",nonExistBookIDs.toString());
        }
        if(damagedBooks.size()!=0){
            returnMap.put("isSuccess",false);
            returnMap.put("损坏的书",damagedBooks.toString());
        }
        if(borrowedBooks.size()!=0){
            returnMap.put("isSuccess",false);
            returnMap.put("已被借出的书",borrowedBooks.toString());
        }
        if(borrowIds.size()!=0){
            returnMap.put("成功借阅的书",borrowIds.toString());
        }
        return returnMap;
    }

    //     图书续借
    public List<Book> renewBook(List<String> book_ids, String account) throws ParseException {

        List<Book> renewList = new ArrayList<>();
        for (String book_id : book_ids) {
            String reader_id = infoService.getReaderIdByAccount(account);
            if (reader_id == null)
                continue;
            Borrow borrow = bookMapper.getReaderBorrowRecord(reader_id, book_id);         //获取该读者证对应的这本书的borrow对象
            if (borrow == null)
                continue;
            borrow.setRenew_num(borrow.getRenew_num() + 1);                               //续借次数更新

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  //should_retuen 更新
            Date date = sdf.parse(borrow.getShould_return());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.DATE, borrow.getRenew_day());
            borrow.setShould_return(sdf.format(calendar.getTime()));

            bookMapper.updateBorrow(borrow);
            renewList.add(bookMapper.getBookById(book_id));
        }
        return renewList;
    }

    //    还书
    @Transactional //作为事务管理
    public Map returnBook(List<String> book_ids, String account) {
        //TODO 不是一个事务
        //TODO 还书时要检测是否超期，如果超期则提醒缴费
        String reader_id = infoService.getReaderIdByAccount(account);
        Map<String, Object> map = new HashMap<>();

        if (reader_id == null || book_ids.size() == 0) {
            map.put("isReturn", false);
            return map;
        }



        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");      //设置还书日期
        Date currentDate = new Date();
        String returnDate = sdf.format(currentDate);

        List<Book> rc = new LinkedList<>();          //记录未能归还的书籍 可能是图书信息输入错误
        List<Book> rt = new LinkedList<>();          //成功归还的图书
        for (String i : book_ids) {
            if (bookMapper.canBookReturned(i, reader_id)) {
                bookMapper.updateBookReturn(i, reader_id);
                bookMapper.updateBorrowReturn(i, reader_id,returnDate);
                readerMapper.reduceBorrowingNum(reader_id);//减少借阅证的已借数量
                rt.add(searchBookById(i));
            } else {
                rc.add(searchBookById(i));
            }
        }
        map.put("returned", rt);
        map.put("unableToReturn", rc);
        return map;
    }

    //    预定图书
    public Map<String, Object> reserveBook(String date, List<String> book_ids, String account) throws ParseException {
        String reader_id = infoService.getReaderIdByAccount(account);
        Map<String, Object> map = new HashMap<>();
        List<String> rc1 = new ArrayList<>();      //记录成功的
        Map<String, String> rc2 = new HashMap<>();      //记录失败的
        List<String> rc3 = new ArrayList<>();         //记录可以借出的

        if (reader_id == null) {
            map.put("error", "该用户未拥有借阅证");
            return map;
        }
        Reader reader = bookMapper.getReaderRights(reader_id);

        for (String book_id : book_ids) {
            try {
                if (bookMapper.judgeBookReserve(book_id) != null) {
                    rc2.put(book_id, "预定失败，该图书可能已借出!");
                    continue;
                }

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Calendar calendar = Calendar.getInstance();
                Date d = sdf.parse(date);

                calendar.setTime(d);
                calendar.add(Calendar.DATE, reader.getBorrow_day());
                Borrow borrow = new Borrow();

                borrow.setBorrow_state("预定");
                borrow.setBook_id(book_id);
                borrow.setReader_id(reader_id);

                borrow.setBorrow_time(sdf.format(d));        //预定时间
                borrow.setShould_return(sdf.format(calendar.getTime()));
                borrow.setReturn_time("");

                bookMapper.updateReserve(book_id, "预定", "在馆");
                bookMapper.insertBorrow(borrow);
                rc1.add(book_id);
            } catch (Exception e) {
                rc2.put(book_id, "数据库异常!");
            }

        }
        map.put("successToReserve", rc1);
        map.put("unadbleToReserve", rc2);
        return map;
    }
    //取消预定
    public Map<String, Object> cancelReserve(List<String> book_ids, String account) {
        Map<String, Object> map = new HashMap<>();
        List<String> rc1 = new ArrayList<>();      //记录成功的

        for (String book_id : book_ids) {

            List<Borrow> borrows = bookMapper.getReserveRecord(infoService.getReaderIdByAccount(account));
            if (!borrows.isEmpty()) {
                for (Borrow borrow : borrows) {
                    if (!borrow.getBook_id().equals(book_id))
                        continue;
                    borrow.setBorrow_state("取消预定");
                    bookMapper.updateBorrow(borrow);
                    bookMapper.updateReserve(book_id, "在馆", "预定");
                    rc1.add(book_id);
                    break;
                }
            }
        }
        map.put("successToCancel", rc1);
        return map;
    }

    //获取预定的书籍
    public List<Book> getReserveRecord(String account) {
        String reader_id = infoService.getReaderIdByAccount(account);
        if (reader_id == null)
            return null;
        List<Borrow> borrows = bookMapper.getReserveRecord(reader_id);
        List<Book> books = new ArrayList<>();
        for (Borrow borrow : borrows) {
            books.add(bookMapper.getBookById(borrow.getBook_id()));
        }
        return books;
    }

}