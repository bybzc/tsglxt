package com.by.tsgl.controller;
/**
 * @author leibaoyu
 * @version 1.0
 * @date 2021/11/3 20:25
 */
import com.by.tsgl.bean.Book;
import com.by.tsgl.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.*;


@Controller

public class bookController {
    @Autowired
    BookService bookService;

    /**
     * 添加图书
     * @param book
     * @return
     */
    @ResponseBody
    @PostMapping("/admin/addbook")
    public boolean addBook(@RequestBody Book book) {
        return bookService.addBook(book);
    }

    /**
     * 获取图书信息，通过关键字，主要是通过mode配对 不同sql语句
     * @param o
     * @return
     */
    @ResponseBody
    @PostMapping("/getBookIfo")
    public ArrayList<Book> getIfo(@RequestBody Map o){

        ArrayList<Book>books=bookService.searchBooks(o);

        return books;
    }

    /**
     * 获取所有图书信息
     * @return
     */
    @ResponseBody
    @GetMapping("/allBookIfos")
    public ArrayList<Book> getAllBooks() {
        return bookService.getAllBooks();
    }

    /**
     * 修改图书信息
     * @param book
     * @return
     */
    @ResponseBody
    @PostMapping("/admin/modify")
    public boolean modifyIfo(@RequestBody Book book) {
        if(bookService.searchBookById(book.getBook_id())!=null) {
            bookService.modifyIfo(book);
            return true;
        }
        return false;
    }

    /**
     * 删除若干图书
     * @param
     * @return
     */
    @ResponseBody
    @PostMapping("/admin/deleteBooks")
    public Map<String, Object> deleteBook(@RequestBody Map map){
        List<String> list=(List<String>)map.get("book_ids");//删除的图书id
        Map<String,Object> resultMap=new HashMap<>();
        List<String> successList= new ArrayList<>();
        List<String> unsuccessList= new ArrayList<>();
        for (String i : list){
            try{
                if(bookService.searchBookById(i)!=null) {
                    bookService.deleteBook(i);
                    successList.add(i);
                }
            }catch (Exception e){
                unsuccessList.add(i);
                System.out.println(e);
            }
        }
        if(unsuccessList.size()==0){
            //没有失败
            resultMap.put("isSuccess",true);
            resultMap.put("删除成功",successList.toString());
            return resultMap;
        }
        else if(successList.size()!=0){
            resultMap.put("isSuccess",false);
            resultMap.put("删除成功",successList.toString());
            return resultMap;
        }
        else{
            //全部失败
            resultMap.put("isSuccess",false);
            return resultMap;
        }
    }


    /**
     * 借出图书，可以批量借出，如果图书id不存在则该图书无效
     * @param
     * @return
     */
    @ResponseBody
    @PostMapping("/borrow")
    public  Map<String,Object> borrowBook(@RequestBody Map o) {
        String account=o.get("account").toString();
        Object obj=o.get("book_ids");
        List<String>book_ids=(List<String>) obj;
        System.out.println(book_ids);
        if(book_ids.isEmpty())
            return null;
        return bookService.borrowBook(book_ids,account);
    }

    /**
     * 图书续借,根据读者等级来判断续借次数和续借时长
     * @param o
     * @return
     * @throws ParseException
     */
    @ResponseBody
    @PostMapping("/renew")
    public List<Book> renewBook(@RequestBody Map o) throws ParseException {
        String account=o.get("account").toString();
        Object obj=o.get("book_ids");
        List<String>book_ids=(List<String>) obj;
        return bookService.renewBook(book_ids, account);
    }

    /**
     * 图书归还，一次可以归还多本书籍，但是只返回未归还的书籍信息。
     * @param o
     * @return
     */
    @ResponseBody
    @PostMapping("/returnbooks")
    public Map returnBooks(@RequestBody Map o){

        String account=o.get("account").toString();
        Object books=o.get("book_ids");
        List<String>book_ids = (List<String>)books;


        return bookService.returnBook(book_ids,account);
    }

    /**
     * 图书预定，读者预定时间，系统判断该时间段是否可以预定
     * @param o
     * @return
     * @throws ParseException
     */
    @ResponseBody
    @PostMapping("/reserve")
    public Map<String ,Object> reserveBook(@RequestBody Map o) throws ParseException {
        String date=o.get("date").toString();
        String account=o.get("account").toString();
        Object obj=o.get("book_ids");
        List<String> book_ids=(List<String>)obj;
        return bookService.reserveBook(date,book_ids,account);
    }

    /**
     * @description 取消预定
     * @author leibaoyu
     * @date 2021/11/19 12:45
     * @param o
     * @return java.util.Map<java.lang.String, java.lang.Object>
     * @throws
     */
    @ResponseBody
    @PostMapping("/cancelreservation")
    public Map<String,Object> cancelReservation(@RequestBody Map o ){
        Object obj=o.get("book_ids");
        List<String> book_ids=(List<String>)obj;
        String account=o.get("account").toString();
        return bookService.cancelReserve(book_ids,account);
    }

    /**
     * @description 查询预定记录
     * @author leibaoyu
     * @date 2021/11/19 12:45
     * @param account
     * @return java.util.List<com.by.tsgl.bean.Book>
     * @throws
     */
    @ResponseBody
    @GetMapping("/getreservations")
    public List<Book> getReservations(  @RequestParam("account") String account){
        return bookService.getReserveRecord(account);
    }
}
