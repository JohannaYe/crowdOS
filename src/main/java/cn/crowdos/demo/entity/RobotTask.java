package cn.crowdos.demo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;


@Data
public class RobotTask {

    private Integer taskId = 0;               //任务id
    private String taskName;              //任务名

    private Integer workRobotType = 0;;  //执行任务所需的机器人类型  [ "分配", "运输机器人" ]

     //[[ "导航", "一期菜鸟驿站" ],[ "拿取", "快递" ],[ "导航", "笃行宿舍楼" ],[ "放下", "快递" ]]
    private List<Action> actions;


    private Integer status = 0;  //任务状态，未分配：0，已分配：1，已完成：2，已取消：-1

    private Integer taskType = 0;  //任务类型
    private Integer taskFrom = 0; //任务来源  人类发布的：0，机器人感知到的：1

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")


    private Date postTime = new Date();                //任务发布时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date deadLine = new Date();                //任务截止时间


    private Integer userId = 0;              //任务发布者的id

    private String userName = "default";              //任务发布者的用户名

    private Double longitude = 0.0;       //任务坐标，对应二维x
    private Double latitude = 0.0;        //任务坐标，对应二维y

    private Integer venueId = 0;       //场所的id
    private String venueName = "default"; //任务场所，目的地

//    private Float coin;             //任务赏金
//    private String describe;        //任务详细描述
//    private Integer status;        //任务完成的状态，0为未分配未完成，1为已分配未完成，2为完成

}


