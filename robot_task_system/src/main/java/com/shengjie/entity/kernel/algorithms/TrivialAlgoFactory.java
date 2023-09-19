package com.shengjie.entity.kernel.algorithms;

import com.shengjie.entity.kernel.constraint.Coordinate;
import com.shengjie.entity.kernel.resource.Task;
import com.shengjie.entity.kernel.constraint.Constraint;
import com.shengjie.entity.kernel.constraint.POIConstraint;
import com.shengjie.entity.kernel.resource.Participant;
import com.shengjie.entity.kernel.system.SystemResourceCollection;
import com.shengjie.entity.kernel.system.resource.ParticipantPool;

import java.util.*;
import java.util.stream.Collectors;

public class TrivialAlgoFactory implements AlgoFactory{

    protected final SystemResourceCollection resourceCollection;

    public TrivialAlgoFactory(SystemResourceCollection resourceCollection){
        this.resourceCollection = resourceCollection;
    }
    @Override
    public ParticipantSelectionAlgo getParticipantSelectionAlgo() {

        return new ParticipantSelectionAlgo() {
            final ParticipantPool participantPool;
            {
                participantPool = resourceCollection.getResourceHandler(ParticipantPool.class).getResourceView();
            }
            @Override
            public List<Participant> getCandidates(Task task) {
                List<Participant> candidate = new LinkedList<>();
                for (Participant participant : participantPool) {
                    if (participant.available() && task.canAssignTo(participant)){
                        candidate.add(participant);
                    }
                }
                return candidate;
            }
        };
    }

    @Override
    public TaskRecommendationAlgo getTaskRecommendationAlgo() {
        return new TaskRecommendationAlgo() {
            final ParticipantPool participantPool;
            {
                participantPool = resourceCollection.getResourceHandler(ParticipantPool.class).getResourceView();
            }
            @Override
            public List<Participant> getRecommendationScheme(Task task) {
                List<Participant> candidate = new LinkedList<>();
                for (Participant participant : participantPool) {
                    if (participant.available() && task.canAssignTo(participant)){
                        candidate.add(participant);
                    }
                }
                return candidate;
            }
        };
    }

    @Override
    public TaskAssignmentAlgo getTaskAssignmentAlgo() {
        return new TaskAssignmentAlgo() {
            final ParticipantPool participantPool;
            {
                participantPool = resourceCollection.getResourceHandler(ParticipantPool.class).getResourceView();
            }
            @Override
            public List<Participant> getAssignmentScheme(Task task) {
                List<Participant> candidate = new LinkedList<>();
                for (Participant participant : participantPool) {
                    if (participant.available() && task.canAssignTo(participant)){
                        candidate.add(participant);
                    }
                }
                return candidate;
            }
        };
    }

    @Override
    public TasksAssignmentAlgo getTasksAssignmentAlgo() {
        return new TasksAssignmentAlgo() {
            final ParticipantPool participants;

            {
                participants = resourceCollection.getResourceHandler(ParticipantPool.class).getResourceView();
            }

            @Override
            public Map<Participant, List<Task>> getAssignmentScheme(List<Task> tasks) {
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
                    if (taskConstraint.size() != 1) {
                        return null;
                    }

                    POIConstraint poiConstraint = (POIConstraint)taskConstraint.get(0);
                    Coordinate taskLocation = poiConstraint.getLocation();

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
                PT_Most pt_most = new PT_Most(workerNum, taskNum, distanceMatrix, taskDistanceMatrix, p, 3);
                pt_most.taskAssign();

//                T_Most t_most = new T_Most(workerNum, taskNum, distanceMatrix, taskDistanceMatrix, p, 3);
//                t_most.taskAssign();

                // 保存任务分配结果
                Map<Participant, List<Task>> assignmentScheme = new HashMap<>();

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
