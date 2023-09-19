package com.shengjie.entity.kernel;

import com.shengjie.entity.kernel.algorithms.*;
import com.shengjie.entity.kernel.resource.Task;

import com.shengjie.entity.kernel.resource.Participant;
import com.shengjie.entity.kernel.system.SystemResourceCollection;
import com.shengjie.entity.kernel.system.SystemResourceHandler;
import com.shengjie.entity.kernel.system.resource.AlgoContainer;
import com.shengjie.entity.kernel.system.resource.Resource;
import com.shengjie.entity.kernel.system.resource.TaskPool;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Scheduler implements Resource<Scheduler> {
    // A private variable that is used to store the participant selection algorithm.
    private final ParticipantSelectionAlgo participantSelectionAlgo;
    // A private variable that is used to store the task recommendation algorithm.
    private final TaskRecommendationAlgo taskRecommendationAlgo;
    // A private variable that is used to store the task assignment algorithm.
    private final TaskAssignmentAlgo taskAssignmentAlgo;
    // A private variable that is used to store the algorithm factory.

    private final TasksAssignmentAlgo tasksAssignmentAlgo; //多任务分配
    private final AlgoFactory algoFactory;
    // Used to store the resource collection.
    private final SystemResourceCollection resourceCollection;

    // The constructor of the Scheduler class. It is used to initialize the scheduler.
    public Scheduler(SystemResourceCollection collection){
        this.resourceCollection = collection;
        SystemResourceHandler<AlgoFactory> resourceHandler = collection.getResourceHandler(AlgoContainer.class);
        this.algoFactory = resourceHandler.getResource();

        participantSelectionAlgo = algoFactory.getParticipantSelectionAlgo();
        taskRecommendationAlgo = algoFactory.getTaskRecommendationAlgo();
        taskAssignmentAlgo = algoFactory.getTaskAssignmentAlgo();

        tasksAssignmentAlgo = algoFactory.getTasksAssignmentAlgo();
    }

    /**
     *  For each task in the task pool, recommend a list of participants to complete the task
     *
     * @return A list of lists of participants.
     */
    public List<List<Participant>> recommendTasks(){
        TaskPool resourceView = resourceCollection.getResourceHandler(TaskPool.class).getResourceView();
        List<List<Participant>> recommendScheme = new ArrayList<>(resourceView.size());
        for (Task task : resourceView) {
            recommendScheme.add(taskRecommendation(task));
        }
        return recommendScheme;
    }

    /**
     *  For each task in the task pool, assign a list of participants to it
     *
     * @return A list of lists of participants.
     */
    public List<List<Participant>> assignTasks(){
        TaskPool resourceView = resourceCollection.getResourceHandler(TaskPool.class).getResourceView();
        List<List<Participant>> assignmentScheme = new ArrayList<>(resourceView.size());
        for (Task task : resourceView) {
            assignmentScheme.add(taskAssignment(task));
        }
        return assignmentScheme;
    }

    /**
     * The participant selection algorithm is used to get the candidates for a task.
     *
     * @param task The task for which the participants are to be selected.
     * @return A list of participants.
     */
    public List<Participant> participantSelection(Task task){
        return participantSelectionAlgo.getCandidates(task);
    }

    /**
     * This function returns a list of participants that are recommended for a given task.
     *
     * @param task The task for which you want to get the recommendations.
     * @return A list of participants.
     */
    public List<Participant> taskRecommendation(Task task){
        return taskRecommendationAlgo.getRecommendationScheme(task);
    }

    /**
     * This function returns a list of participants that are assigned to a task.
     *
     * @param task The task object that needs to be assigned to participants.
     * @return A list of participants.
     */
    public List<Participant> taskAssignment(Task task){
        return taskAssignmentAlgo.getAssignmentScheme(task);
    }

    public Map<Participant,List<Task>> tasksAssignment(List<Task> tasks){
        return tasksAssignmentAlgo.getAssignmentScheme(tasks);
    }

    /**
     * This function returns the AlgoFactory object that was created in the constructor.
     *
     * @return The algoFactory object.
     */
    public AlgoFactory getAlgoFactory() {
        return algoFactory;
    }

    @Override
    // A method that returns a SystemResourceHandler object.
    public SystemResourceHandler<Scheduler> getHandler() {
        Scheduler scheduler = this;
        return new SystemResourceHandler<Scheduler>() {
            @Override
            public Scheduler getResourceView() {
                return scheduler;
            }

            @Override
            public Scheduler getResource() {
                return scheduler;
            }
        };
    }
}
