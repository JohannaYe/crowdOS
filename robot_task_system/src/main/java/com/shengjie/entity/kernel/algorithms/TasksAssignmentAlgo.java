package com.shengjie.entity.kernel.algorithms;


import com.shengjie.entity.kernel.resource.Participant;
import com.shengjie.entity.kernel.resource.Task;

import java.util.List;
import java.util.Map;


@FunctionalInterface
public interface TasksAssignmentAlgo {

    Map<Participant,List<Task>> getAssignmentScheme(List<Task> tasks);
}
