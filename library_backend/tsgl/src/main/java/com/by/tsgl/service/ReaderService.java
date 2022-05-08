package com.by.tsgl.service;

import com.by.tsgl.bean.Reader;
import com.by.tsgl.bean.ReaderGrade;
import com.by.tsgl.mapper.AccountMapper;
import com.by.tsgl.mapper.ReaderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReaderService {

    @Autowired
    ReaderMapper readerMapper;
    @Autowired
    AccountMapper accountMapper;
    public Map<String, Object> registerReader(String account){
        if(account==null){
            //没有指定为哪个用户办理读者证
            Map<String,Object> map = new HashMap<>();
            map.put("isSuccess",false);
            map.put("msg","缺少账号");
            return map;
        }
         if(accountMapper.isInfoNotFull(account)){
            //检查账号信息是否填写完全（仅允许信息完整的用户注册读者证）
            Map<String,Object> map = new HashMap<>();
            map.put("isSuccess",false);
            map.put("msg","该账号信息不完整，无法注册读者证");
            return map;
        }
        if(!accountMapper.isAccount(account)){
            //不是一个账号
            Map<String,Object> map = new HashMap<>();
            map.put("isSuccess",false);
            map.put("msg","账号不存在");
            return map;
        }
        String user_id = accountMapper.getUserId(account);
        //TODO : 在这里增加一个已有读者证的判断
        try{
            readerMapper.registerReader(user_id);
        }catch (Exception e){
            System.out.println(e);
            Map<String,Object> map = new HashMap<>();
            map.put("isSuccess",false);
            //预计出错原因只有重复注册读者证
            map.put("msg","该账号已注册读者证");
            return map;
        }
        Map<String,Object> map = new HashMap<>();
        map.put("isSuccess",true);
        return map;
    }


    public List<ReaderGrade> getAllReaderGrade(){
        return readerMapper.getAllReaderGrade();
    }

    public ReaderGrade getReaderGradeById(String grade_id) {
        return readerMapper.getReaderGradeById(grade_id);
    }

    public Map<String,Object> updateReaderGradeById(ReaderGrade readerGrade) {
        if(readerGrade.getGrade_id()==0){
            //没有指定为哪个用户办理读者证，int类型无空，空值默认为0
            Map<String,Object> map = new HashMap<>();
            map.put("isSuccess",false);
            map.put("msg","缺少等级id");
            return map;
        }

        try{
            readerMapper.updateReaderGradeById(readerGrade);
        }catch (Exception e){
            System.out.println(e);
            Map<String,Object> map = new HashMap<>();
            map.put("isSuccess",false);
            //预计出错原因只有重复注册读者证
            map.put("msg","更新出现错误");
            return map;
        }
        Map<String,Object> map = new HashMap<>();
        map.put("isSuccess",true);
        return map;
    }

    public Map<String, Object> getReader(String account) {
        try{
            String user_id = accountMapper.getUserId(account);
            Reader reader = readerMapper.getReader(user_id);

            //获取该读者拥有的权限
            ReaderGrade readerGrade = readerMapper.getReaderGradeById(reader.getGrade_id());
            reader.setGrade(readerGrade);

            Map<String,Object> map = new HashMap<>();
            map.put("isSuccess",true);
            map.put("reader",reader);
            return map;
        }catch (Exception e){
            Map<String,Object> map = new HashMap<>();
            map.put("isSuccess",false);
            return map;
        }

    }
}
