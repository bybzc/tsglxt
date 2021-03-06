package com.by.tsgl.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * @author leibaoyu
 * @version 1.0
 * @date 2021/11/21 19:05
 */
@Configuration
public class WebSocketConfig {

     @Bean
     public ServerEndpointExporter serverEndpointExporter() {
         return new ServerEndpointExporter();
     }
}
