package com.by.tsgl.service;

/**
 * @author leibaoyu
 * @version 1.0
 * @date 2021/11/21 19:12
 */

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import com.alibaba.fastjson.JSONObject;
import com.by.tsgl.mapper.DataStateMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Slf4j
@ServerEndpoint("/websocket/{account}")
@Component
public class WebSocketServer {

    //静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static int onlineCount = 0;
    //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
    private static CopyOnWriteArraySet<WebSocketServer> webSocketSet
            = new CopyOnWriteArraySet<WebSocketServer>();
    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;
    //account
    private String account ="";

    private static DataStateMapper dataStateMapper;
    @Autowired
    public void init(DataStateMapper dataStateMapper){
        WebSocketServer.dataStateMapper=dataStateMapper;
    }

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session,@PathParam("account") String account) {
        this.session = session;
        webSocketSet.add(this);     //加入set中
        addOnlineCount();           //在线数加1
        log.info("有新窗口开始监听:"+ account +",当前在线人数为" + getOnlineCount());
        this.account = account;
        try {
            Map<String,String>map=new HashMap<>();
            map.put("state","connect");
            sendMessage(map);
        } catch (IOException  e) {
            log.error("websocket IO异常");
        }
    }
    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        webSocketSet.remove(this);  //从set中删除
        subOnlineCount();           //在线数减1
        log.info("用户"+this.account+"连接关闭！当前在线人数为" + getOnlineCount());
    }
    /**
     * 收到客户端消息后调用的方法
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("收到来自窗口"+ account +"的信息:"+message);
        //群发消息
        for (WebSocketServer item : webSocketSet) {
            try {
                item.sendMessage(message);
            } catch (IOException  e) {
                e.printStackTrace();
            }
        }
    }
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("发生错误");
        error.printStackTrace();
    }
    //实现服务器主动推送
    public void sendMessage(Object message) throws IOException {
        if(message==null)
            return;
         String msg=JSONObject.toJSONString(message);
        this.session.getBasicRemote().sendText(msg);
    }

    //群发自定义消息
    public static void sendInfo(List<String> message) throws IOException {

        for (WebSocketServer item : webSocketSet) {
            for (String account : message) {
                try {
                    //这里可以设定只推送给这个account的，为null则全部推送
                    if (item.account.equals(account)) {
                        int num = dataStateMapper.getNotify(account, "未读").size();
                        if (num != 0) {
                            log.info("推送消息到窗口" + account);
                            Map<String, Integer> map = new HashMap<>();
                            map.put("messageCount", num);
                            item.sendMessage(map);
                        }
                    }
                } catch (IOException  e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public static synchronized int getOnlineCount() {
        return onlineCount;
    }
    public static synchronized void addOnlineCount() {
        WebSocketServer.onlineCount++;
    }
    public static synchronized void subOnlineCount() {
        WebSocketServer.onlineCount--;
    }
}
