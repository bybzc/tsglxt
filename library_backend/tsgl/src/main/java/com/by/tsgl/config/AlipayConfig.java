package com.by.tsgl.config;

/**
 * @author leibaoyu
 * @version 1.0
 * @date 2021/11/3 20:25
 */

import com.alipay.easysdk.factory.Factory;
import com.alipay.easysdk.kernel.Config;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class AlipayConfig {


    //@Value():将配置文件的值配置过来
    //appid
    @Value("${alipay.appId}")
    private String appId;
    //协议
    @Value("${alipay.protocol}")
    private String protocol;
    //网关
    @Value("${alipay.gatewayHost}")
    private String gatewayHost;
    //RSA2
    @Value("${alipay.signType}")
    private String signType;
    //私钥
    @Value("${alipay.merchantPrivateKey}")
    private String merchantPrivateKey;
    //支付宝公钥字符串即可
    @Value("${alipay.alipayPublicKey}")
    private String alipayPublicKey;
    //可设置异步通知接收服务地址
    @Value("${alipay.notifyUrl}")
    private String notifyUrl;

    @Bean
    public Config getAlipayConfig(){
        Config config=new Config();
        config.appId=appId;
        config.protocol=protocol;
        config.gatewayHost=gatewayHost;
        config.signType=signType;
        config.merchantPrivateKey=merchantPrivateKey;
        config.alipayPublicKey=alipayPublicKey;
        config.notifyUrl=notifyUrl;
        Factory.setOptions(config);
        return config;
    }

}