package cn.crowdos.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


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
