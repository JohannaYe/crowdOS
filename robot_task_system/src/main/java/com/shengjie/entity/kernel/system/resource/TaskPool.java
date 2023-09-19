package com.shengjie.entity.kernel.system.resource;

import com.shengjie.entity.kernel.resource.Task;
import com.shengjie.entity.kernel.system.SystemResourceHandler;

import java.util.LinkedList;

public class TaskPool extends LinkedList<Task> implements Resource<TaskPool> {
    @Override
    public SystemResourceHandler<TaskPool> getHandler() {
        TaskPool tasks = this;

        return new SystemResourceHandler<TaskPool>() {

            @Override
            public TaskPool getResourceView() {
                return tasks;
            }

            @Override
            public TaskPool getResource() {
                return tasks;
            }
        };
    }
}
