package com.by.tsgl;

import com.by.tsgl.bean.User;
import com.by.tsgl.controller.infoController;
import com.by.tsgl.mapper.AccountMapper;
import com.by.tsgl.mapper.InfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
class TsglApplicationTests {
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    AccountMapper accountMapper;

    @Autowired
    InfoMapper infoMapper;

    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext wac;

    @Before // 在测试开始前初始化工作
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }
    
    @Test
    void contextLoads() {
//        System.out.println(infoMapper.getAdminAnnouncement("1"));
    }
    @Test
    void testGetUserId(){
        System.out.println(accountMapper.getUserId("2222"));
    }
    @Test
    void byTest(){

        try {
            MvcResult mvcResult = mockMvc.perform(get("http://localhost:5000/announcement"))
                    .andExpect(status().isOk())
                    .andReturn();
            log.info(mvcResult.getResponse().getContentAsString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
