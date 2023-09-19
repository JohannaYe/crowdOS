package com.shengjie.entity;


import lombok.Data;

@Data
public class RobotExecutionTask {

    private Integer RobotExecutionTaskId;

    private Integer robotId;
    //机器人ID

    private Integer robotTaskId;
    //任务ID

    private Integer RobotExecutionTaskStatus;
    //机器人任务执行状态，未执行：0，执行中：1，已完成：2，已取消：-1

}
