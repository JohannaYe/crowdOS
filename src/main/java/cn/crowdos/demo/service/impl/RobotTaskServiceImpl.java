package cn.crowdos.demo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.crowdos.demo.entity.RobotTask;
import cn.crowdos.demo.mapper.RobotTaskMapper;
import cn.crowdos.demo.service.RobotTaskService;
import org.springframework.stereotype.Service;

@Service
public class RobotTaskServiceImpl extends ServiceImpl<RobotTaskMapper, RobotTask> implements RobotTaskService {
}
