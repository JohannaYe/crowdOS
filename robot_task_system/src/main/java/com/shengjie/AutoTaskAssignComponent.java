package com.shengjie;

import com.shengjie.entity.RobotTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;


@Component
public class AutoTaskAssignComponent {

    @Autowired
    GlobalComponent globalComponent;

    @Scheduled(fixedRate = 10000) // 每隔1秒执行一次
    public void checkListSize() {
        if (globalComponent.getTaskList().size() > 0) {
            globalComponent.taskAssign();
            globalComponent.setFlag(true);
        }

    }

    public void sendHttpRequest() {
        // ...
    }
}
