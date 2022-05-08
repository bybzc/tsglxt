package com.by.tsgl.mapper;

import com.by.tsgl.bean.Reader;
import com.by.tsgl.bean.ReaderGrade;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Mapper
public interface ReaderMapper {
    void registerReader(String user_id);

    /**
     * 获取所有的读者证等级
      * @return
     */
    List<ReaderGrade> getAllReaderGrade();

    ReaderGrade getReaderGradeById(String grade_id);

    void updateReaderGradeById(ReaderGrade readerGrade);

    int getBorrowingNumByAccount(String account);
    int getAllowBorrowNumByAccount(String account);
    String getReaderId(String user_id);

    void reduceBorrowingNum(String reader_id);

    //更新读者状态
    public void updateReader(Reader reader);

    Reader getReader(String user_id);

    Reader getReaderByReaderId(String reader_id);
}
