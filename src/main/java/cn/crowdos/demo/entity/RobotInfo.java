package cn.crowdos.demo.entity;


import lombok.Data;

@Data
public class RobotInfo {
    //机器人id
    private Integer robotId;
    //机器人名
    private String robotName;
    //机器人类型
    private Integer robotType;

    //机器人状态
    private Integer robotStatus;  //执行任务中：1，空闲中：0，故障中：-1

    private Double longitude;       //机器人坐标，对应二维x
    private Double latitude;        //机器人坐标，对应二维y

}
