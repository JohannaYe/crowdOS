package com.shengjie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shengjie.entity.RobotTask;
import com.shengjie.mapper.RobotTaskMapper;
import com.shengjie.service.RobotTaskService;
import org.springframework.stereotype.Service;

@Service
public class RobotTaskServiceImpl extends ServiceImpl<RobotTaskMapper, RobotTask> implements RobotTaskService {
}
