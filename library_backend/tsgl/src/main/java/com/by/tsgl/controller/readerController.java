package com.by.tsgl.controller;

import com.by.tsgl.bean.ReaderGrade;
import com.by.tsgl.service.ReaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
public class readerController {
    @Autowired
    ReaderService readerService;

    @ResponseBody
    @PostMapping("/registerReader")
    public Map<String, Object> registerReader(@RequestBody Map a){
        String account = (String) a.get("account");
        return readerService.registerReader(account);
    }

    @ResponseBody
    @PostMapping("/getReader")
    public Map<String, Object> getReader(@RequestBody Map a){
        String account = (String) a.get("account");
        return readerService.getReader(account);
    }


    @ResponseBody
    @GetMapping("/admin/getAllReaderGrade")
    public List<ReaderGrade> getAllReaderGrade(){
        return readerService.getAllReaderGrade();
    }

    @ResponseBody
    @GetMapping("/admin/getReaderGradeById")
    public ReaderGrade getReaderGradeById(@RequestParam String grade_id){
        return readerService.getReaderGradeById(grade_id);
    }

    @ResponseBody
    @PostMapping("/admin/updateReaderGradeById")
    public Map<String, Object> updateReaderGradeById(@RequestBody ReaderGrade readerGrade){
        return readerService.updateReaderGradeById(readerGrade);
    }
}
