package cn.crowdos.demo.entity;


import lombok.Data;

@Data
public class Action {
    private String motion;  //动作 导航
    private String perform;  //目标 菜鸟驿站

    public Action(String motion, String perform){
        this.motion = motion;
        this.perform = perform;
    }
}
