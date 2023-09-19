package com.shengjie.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shengjie.GlobalComponent;
import com.shengjie.RobotCoordinateComponent;
import com.shengjie.common.R;
import com.shengjie.entity.RobotInfo;
import com.fasterxml.jackson.core.type.TypeReference;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MyHandler extends TextWebSocketHandler {

    @Autowired
    private RobotCoordinateComponent robotCoordinateComponent;

    @Autowired
    private GlobalComponent globalComponent;


    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception{
        // 处理WebSocket消息
        String payload = message.getPayload();

        //payload是前端传过来的json字符串，可以用json工具类转换成对象
        ObjectMapper objectMapper = new ObjectMapper();

        List<RobotInfo> robotInfoList = objectMapper.readValue(payload, new TypeReference<List<RobotInfo>>() {});


        //将获取的机器人位置信息保存到robotCoordinateComponent，然后controller层就可以获取
        for (RobotInfo robotInfo : robotInfoList) {
            robotCoordinateComponent.getRobotLocationMap()
                    .put(robotInfo.getRobotId(), Arrays.asList(robotInfo.getLongitude(),robotInfo.getLatitude()));
        }

        //打印robotCoordinateComponent
        System.out.println("获取到的机器人位置信息为：");
        System.out.println(robotCoordinateComponent.getRobotLocationMap());


//        System.out.println(payload);

//        session.sendMessage(new TextMessage("收到来自unity客户端的消息：" + payload));

        //这边可以定义一个if,在globalComponent定义flag,如果flag为true，则发送
        if (globalComponent.isFlag()){
//            session.sendMessage(new TextMessage("这里传输来自小程序的任务分配结果："));
            ObjectMapper objectMapper_ = new ObjectMapper();

            String TaskAssignResultJson = objectMapper_.writeValueAsString(R.success(globalComponent.getTaskAssignMap()));
            session.sendMessage(new TextMessage(TaskAssignResultJson));
            globalComponent.setFlag(false);
        }




    }



    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // WebSocket连接建立后执行的方法
        System.out.println("WebSocket连接已建立");
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // WebSocket连接关闭后执行的方法
        System.out.println("WebSocket连接已关闭");
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        // WebSocket连接出现错误时执行的方法
        System.out.println("WebSocket连接发生错误：" + exception.getMessage());
    }

//    js测试的代码

    /*
    let socket = new WebSocket("ws://localhost:8080/myHandler");

    socket.onopen = function(event) {
        console.log("WebSocket连接已打开");
        setInterval(function() {
            let message = {
                    robotName: 'jjj',
                    robotType: 'value2',
                    robotLocation: 'value3'
        };
            socket.send(JSON.stringify(message));
        }, 1000); // 每秒发送一条消息
    };

    socket.onmessage = function(event) {
        console.log("收到消息：" + event.data);
    };

    socket.onclose = function(event) {
        console.log("WebSocket连接已关闭");
    };

    socket.onerror = function(event) {
        console.log("WebSocket连接发生错误");
    };

    */
}
