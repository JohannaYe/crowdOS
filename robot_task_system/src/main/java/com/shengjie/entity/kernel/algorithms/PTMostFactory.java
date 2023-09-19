package com.shengjie.entity.kernel.algorithms;

import com.shengjie.entity.kernel.constraint.Constraint;
import com.shengjie.entity.kernel.constraint.Coordinate;
import com.shengjie.entity.kernel.resource.Participant;
import com.shengjie.entity.kernel.resource.Task;
import com.shengjie.entity.kernel.system.SystemResourceCollection;
import com.shengjie.entity.kernel.system.resource.ParticipantPool;
import com.shengjie.entity.kernel.constraint.POIConstraint;

import java.util.*;
import java.util.stream.Collectors;


public class PTMostFactory extends TrivialAlgoFactory {

    public PTMostFactory(SystemResourceCollection resourceCollection) {
        super(resourceCollection);
    }

    /**
     * 单任务分配
     *
     * @return {@code TaskAssignmentAlgo}
     */
    @Override
    public TaskAssignmentAlgo getTaskAssignmentAlgo() {
        return task -> {
            // prepare args
            ParticipantPool participants = resourceCollection.getResourceHandler(ParticipantPool.class).getResourceView();

            int taskNum = 1;

            List<Constraint> taskLocation = task.constraints().stream()
                    .filter(constraint -> constraint instanceof POIConstraint)
                    .collect(Collectors.toList());
            if (taskLocation.size() != 1){
                return null;
            }
            List<Participant> candidate = participants.stream()
                    .filter(task::canAssignTo)
                    .collect(Collectors.toList());
            int workerNum = candidate.size();


            Coordinate tLocation = (Coordinate) taskLocation.get(0);
            double[][] distanceMatrix = new double[workerNum][taskNum];
            for (int i = 0; i < candidate.size(); i++) {
                Participant worker = candidate.get(i);
                Coordinate wLocation = (Coordinate) worker.getAbility(Coordinate.class);
                distanceMatrix[i][0] = wLocation.euclideanDistance(tLocation);
            }

            double[][] taskDistanceMatrix = new double[][]{{1}};


            // create algorithm instance
            PT_Most pt_most = new PT_Most(workerNum, taskNum, distanceMatrix, taskDistanceMatrix, new int[]{1}, 1);
            pt_most.taskAssign();

            // parser result
            List<Participant> assignmentScheme = new LinkedList<>();
            pt_most.getAssignMap().keySet().forEach(participantId -> assignmentScheme.add(participants.get(participantId)));
            return assignmentScheme;
        };

    }

    /**
     * 多任务分配
     *
     * @return {@code TasksAssignmentAlgo}
     */
    @Override
    public TasksAssignmentAlgo getTasksAssignmentAlgo() {

        return new TasksAssignmentAlgo(){
            final ParticipantPool participants;
            {
                participants = resourceCollection.getResourceHandler(ParticipantPool.class).getResourceView();
            }
            @Override
            public Map<Participant,List<Task>> getAssignmentScheme(List<Task> tasks) {
                //保存所有任务的位置信息
                List<Coordinate> taskLocations = new ArrayList<>();
                //保存所有候选者的位置信息
                List<Coordinate> candidateLocations = new ArrayList<>();

                //保存所有任务的候选者
                List<Participant> candidates = new ArrayList<>();

                //遍历tasks
                for (Task task : tasks) {
                    List<Constraint> taskConstraint = task.constraints().stream()
                            .filter(constraint -> constraint instanceof POIConstraint)
                            .collect(Collectors.toList());
                    if (taskConstraint.size() != 1){
                        return null;
                    }
                    Coordinate taskLocation = (Coordinate) taskConstraint.get(0);

                    //将taskLocation加入taskLocations
                    taskLocations.add(taskLocation);
                }


                for (Participant participant : participants) {
                    for (Task task : tasks) {
                        if (!task.canAssignTo(participant)) {
                            continue;
                        }
                    }
                    candidates.add(participant);
                }

                int taskNum = tasks.size();
                int workerNum = candidates.size();

                System.out.println(taskNum);
                System.out.println(workerNum);

                //遍历candidates，将其位置信息加入candidateLocations
                for (Participant candidate : candidates) {
                    Coordinate candidateLocation = (Coordinate) candidate.getAbility(Coordinate.class);
                    candidateLocations.add(candidateLocation);
                }

                //距离矩阵
                double[][] distanceMatrix = new double[workerNum][taskNum];

                //任务距离矩阵
                double[][] taskDistanceMatrix = new double[taskNum][taskNum];


                //根据candidateLocations和taskLocations计算距离矩阵
                for (int i = 0; i < workerNum; i++) {
                    for (int j = 0; j < taskNum; j++) {
                        distanceMatrix[i][j] = candidateLocations.get(i).euclideanDistance(taskLocations.get(j));
                    }
                }

                //根据taskLocations计算任务距离矩阵
                for (int i = 0; i < taskNum; i++) {
                    for (int j = 0; j < taskNum; j++) {
                        taskDistanceMatrix[i][j] = taskLocations.get(i).euclideanDistance(taskLocations.get(j));
                    }
                }

                // 默认每个任务只需要一个参与者
                int[] p = new int[taskNum];
                for (int i = 0; i < taskNum; i++) {
                    p[i] = 1;
                }

                // create algorithm instance
                PT_Most pt_most = new PT_Most(workerNum, taskNum, distanceMatrix, taskDistanceMatrix, p, 1);
                pt_most.taskAssign();

                // 保存任务分配结果
                Map<Participant,List<Task>> assignmentScheme = new HashMap<>();

                //将pt_most.getAssignMap()中的数据加入assignmentScheme
                for (Map.Entry<Integer, List<Integer>> entry : pt_most.getAssignMap().entrySet()) {
                    Participant participant = candidates.get(entry.getKey());
                    List<Task> taskList = new ArrayList<>();
                    for (Integer taskId : entry.getValue()) {
                        taskList.add(tasks.get(taskId));
                    }
                    assignmentScheme.put(participant, taskList);
                }

                return assignmentScheme;
            }
        };

    }
}
