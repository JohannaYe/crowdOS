package com.shengjie.entity.kernel.resource;

import com.shengjie.entity.kernel.DecomposeException;
import com.shengjie.entity.kernel.constraint.Constraint;
import com.shengjie.entity.kernel.Decomposer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class SimpleTask extends AbstractTask{

    public SimpleTask(List<Constraint> constraints, TaskDistributionType taskDistributionType) {
        super(constraints, taskDistributionType);
        status = TaskStatus.READY;
    }

    @Override
    public Decomposer<Task> decomposer() {
        SimpleTask simpleTask = this;
        return new Decomposer<Task>() {
            @Override
            public List<Task> trivialDecompose() {
                return Collections.singletonList(simpleTask);
            }


            @Override
            public List<Task> scaleDecompose(int scale) throws DecomposeException {
                List<Task> subTasks = new LinkedList<>();
                List<Decomposer<Constraint>> decomposers = new ArrayList<Decomposer<Constraint>>(constraints.size()){{
                    for (Constraint constraint : constraints) {
                        add(constraint.decomposer());
                    }
                }};
                subTaskHelper(subTasks, decomposers, null, 0, scale);
                return subTasks;
            }

            private void subTaskHelper(
                    List<Task> subTasks,
                    List<Decomposer<Constraint>> decomposers,
                    List<Constraint> newConstraints,
                    int pos,
                    int scale
            ) throws DecomposeException {
                if (pos == 0){
                    newConstraints = new ArrayList<>(decomposers.size());
                }
                if (pos == decomposers.size()){
                    subTasks.add(new SimpleTask(newConstraints, taskDistributionType));
                    return;
                }
                Decomposer<Constraint> current = decomposers.get(pos);
                for (Constraint constraint : current.decompose(scale)) {
                    newConstraints.add(constraint);
                    subTaskHelper(subTasks, decomposers, newConstraints, pos+1, scale);
                }
            }

        };
    }
}
