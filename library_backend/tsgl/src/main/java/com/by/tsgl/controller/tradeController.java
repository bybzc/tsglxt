package com.by.tsgl.controller;
/**
 * @author leibaoyu
 * @version 1.0
 * @date 2021/11/4 21:12
 */

import com.by.tsgl.mapper.InfoMapper;
import com.by.tsgl.service.InfoService;
import com.by.tsgl.service.TradeService;
import com.by.tsgl.util.MODE;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/alipay")
public class tradeController {

    @Autowired
    private TradeService tradeService;
    @Autowired
    private InfoService infoService;
    /**
     * @description 缴纳押金
     * @author leibaoyu
     * @date 2021/11/6 17:16
     * @param account
     * @param totalAmount
     * @return java.lang.String
     * @throws
     */
    @ResponseBody
    @GetMapping("/deposit")
    public String payDeposit(@RequestParam("account") String account,
                             @RequestParam("totalAmount") String totalAmount) {
        String subject=MODE.S_DEPOSIT.getStr();
        return tradeService.payByDeposit(account,totalAmount,subject,null);
    }

    /**
     * @description 缴纳罚金
     * @author leibaoyu
     * @date 2021/11/11 22:53
     * @param account
     * @return java.lang.String
     * @throws
     */
    @GetMapping("/fine")
    @ResponseBody
    public String fine(@RequestParam("account") String account){

        String reader_id=infoService.getReaderIdByAccount(account);
        Map<String,Object>res=new HashMap<>();
        if(reader_id==null)
            res.put("error1", "该用户没有借阅证");

        Map map=tradeService.getFineBaseData(reader_id);

        String subject= MODE.S_FINE.getStr();
        String totalAmount=map.get("totalAmount").toString();
        if(Double.parseDouble(totalAmount)==0)
            res.put("error2", "该用户不需要缴纳罚金");
        if(!res.keySet().isEmpty())
            return res.toString();
        return tradeService.payByDeposit(account,totalAmount,subject,map);
    }

    /**
     * @description 调用充值接口，进行余额充值
     * @author leibaoyu
     * @date 2021/11/11 23:22
     * @param account
     * @param totalAmount
     * @return java.lang.String
     * @throws
     */
    @GetMapping("/recharge")
    @ResponseBody
    public String recharge(@RequestParam("account") String account,
                           @RequestParam("totalAmount") String totalAmount){
        String subject= MODE.S_RECHARGE.getStr();
        return tradeService.pagePay(account,totalAmount,subject);
    }

    /**
     * @description 回调验证
     * @author leibaoyu
     * @date 2021/11/4 21:12
     * @param params
     * @return boolean
     * @throws
     */
    @GetMapping("/returncall")
    @ResponseBody
    public String notify(@RequestParam Map<String,String> params) {
        if(!params.containsKey("sign")||!params.containsKey("app_id")||!params.containsKey("out_trade_no")||!params.containsKey("total_amount"))
            return "调用异常!";
       return String.format("<form name=\"notify\" method=\"get\" action=\"%s\">\n" +
                "<input type=\"submit\" value=\"提交\" style=\"display:none\" >\n" +
                "</form>\n" +
                "<script>document.forms[0].submit();</script>",tradeService.returCall(params));
    }

    /**
     * @description 用户请求退款
     * @author leibaoyu
     * @date 2021/11/8 20:20
     * @param deal_id
     * @param reason
     * @return boolean
     * @throws
     */
    @GetMapping("/refund")
    @ResponseBody
    public String refund(@RequestParam("deal_id") String deal_id,
                         @RequestParam(value="reason",required = false) String reason){
        return String.format("<form name=\"notify\" method=\"get\" action=\"%s\">\n" +
                "<input type=\"submit\" value=\"提交\" style=\"display:none\" >\n" +
                "</form>\n" +
                "<script>document.forms[0].submit();</script>",tradeService.refund(deal_id,reason));
    }
}