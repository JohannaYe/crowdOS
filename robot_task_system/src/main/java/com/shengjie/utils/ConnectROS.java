package com.shengjie.utils;

import edu.wpi.rail.jrosbridge.Ros;
import edu.wpi.rail.jrosbridge.Topic;
import edu.wpi.rail.jrosbridge.messages.Message;

/**
 * 该类用于连接ROS
 * @author wushengjie
 * @date 2022/12/02
 */
public class ConnectROS {

    String hostname; //为ROS的Master节点IP

    Ros ros;

    String topicName; //话题名

    String MsgName; //消息名

    //构造方法
    public ConnectROS(String hostname) {
        this.hostname = hostname;
        this.ros = new Ros(hostname);
        this.ros.connect();
        if (ros.isConnected()) {
            System.out.println("与ROS连接成功！");
        }
    }


    /**
     * 发布导航消息，等同于rviz的2D Nav Goal，传入二维坐标
     *
     * @param x
     * @param y
     */
    public void sendNavMsg(double x, double y) {
        Topic navTopic = new Topic(ros, "/move_base_simple/goal", "geometry_msgs/PoseStamped");
        Message navMsg = new Message("{\"header\":"
                + "{\"seq\":0,\"stamp\":{\"secs\":0,\"nsecs\":0},\"frame_id\":\"map\"}"
                + ",\"pose\":{\"position\":{\"x\":" +
                x +
                ",\"y\":" +
                y +
                ",\"z\":0.0},"
                + "\"orientation\":{\"x\":0.0,\"y\":0.0,\"z\":0.0,\"w\":1.0}}}");
        navTopic.publish(navMsg);
    }
}
    //线程休眠
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }



//        ros.disconnect();
//        if (ros.isConnected()==false){
//            System.out.println("退出连接！");
//        }




    //话题发布--------------------------------------------------------------------
    //发布测试信息
//        Topic testTopic = new Topic(ros, "/test", "std_msgs/String");
//        Message testMsg = new Message("{\"data\": \"hello, world!\"}");
//        testTopic.publish(testMsg);

    //发布速度方向等控制信息
//        Topic cmdTopic = new Topic(ros, "/cmd_vel", "geometry_msgs/Twist");
//        Message cmdMsg = new Message("{\"linear\":{\"x\":0.3,\"y\":1.0,\"z\":0.0},"
//                + "\"angular\":{\"x\":0.0,\"y\":0.0,\"z\":0.0}}");
//        cmdTopic.publish(cmdMsg);

    //发布导航点位置信息，等同于rviz的2D Nav Goal，
//        Topic navTopic = new Topic(ros, "/move_base_simple/goal", "geometry_msgs/PoseStamped");
//        Message navMsg = new Message("{\"header\":"
//                + "{\"seq\":0,\"stamp\":{\"secs\":0,\"nsecs\":0},\"frame_id\":\"map\"}"
//                + ",\"pose\":{\"position\":{\"x\":0.5,\"y\":-1.2,\"z\":0.0},"
//                + "\"orientation\":{\"x\":0.0,\"y\":0.0,\"z\":0.0,\"w\":1.0}}}");
//        navTopic.publish(navMsg);





