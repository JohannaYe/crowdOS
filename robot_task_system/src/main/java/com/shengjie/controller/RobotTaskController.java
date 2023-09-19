package com.shengjie.controller;

import com.shengjie.*;
import com.shengjie.common.R;
import com.shengjie.entity.CoordinateTask;
import com.shengjie.entity.RobotInfo;
import com.shengjie.entity.RobotTask;
import com.shengjie.entity.CoordinateParticipant;
import com.shengjie.entity.kernel.algorithms.PT_Most;
import com.shengjie.entity.kernel.constraint.Coordinate;
import com.shengjie.entity.kernel.constraint.POIConstraint;
import com.shengjie.entity.kernel.resource.Participant;
import com.shengjie.entity.kernel.resource.Task;
import com.shengjie.service.RobotTaskService;
import com.shengjie.utils.TaskAssign;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;

import java.util.*;


@RestController
@RequestMapping("/robotTask")
@Slf4j

public class RobotTaskController {

    @Autowired
    private RobotTaskService robotTaskService;

    @Autowired
    private VenueCoordinateComponent venueCoordinateComponent;

    @Autowired
    private RobotCoordinateComponent robotCoordinateComponent;

    @Autowired
    private CrowdKernelComponent crowdKernelComponent;

    @Autowired
    private GlobalComponent globalComponent;



//    @PostMapping()
//    public R<String> addTask(@RequestBody RobotTask robotTask) {
//
//        //获取任务的场所id，找到对应坐标
//        Integer venueId = robotTask.getVenueId();
//        Double longitude = venueCoordinateComponent.getVenueLocationMap().get(venueId).get(0);
//        Double latitude = venueCoordinateComponent.getVenueLocationMap().get(venueId).get(1);
//
//
//        //添加任务到系统内核
//        Coordinate coordinate = new Coordinate(longitude,latitude);
//        POIConstraint poiConstraint = new POIConstraint(coordinate);
//        CoordinateTask coordinateTask = new CoordinateTask(Collections.singletonList(poiConstraint)
//                , Task.TaskDistributionType.ASSIGNMENT);
//        coordinateTask.setVenueId(venueId);
//        crowdKernelComponent.getKernel().submitTask(coordinateTask);
//
//
//        //将任务信息存入数据库
//        System.out.println(robotTask);
////        robotTaskService.save(robotTask);
//
//        return R.success("提交任务成功！当前任务场所的坐标为: "+coordinate.toString());
//
//    }

    @PostMapping()
    public R<String> addTask(@RequestBody RobotTask robotTask) {

        //将robotTask的taskId设置为全局变量taskId

        robotTask.setTaskId(globalComponent.getTaskId());

        globalComponent.setTaskId(globalComponent.getTaskId() + 1);


        //将任务添加到globalComponent.taskList中
        System.out.println("robotTask"+robotTask);
        System.out.println("globalComponent.getTaskList()"+globalComponent.getTaskList());

        globalComponent.getTaskList().add(robotTask);
        return R.success("提交任务成功！");
    }

    //查询数据库中的所有任务
    @GetMapping()
    public R<List<RobotTask>> listAllRobotTasks() {
        List<RobotTask> robotTasks = robotTaskService.list();
        if (robotTasks.isEmpty()) {
            return R.error("没有任务");
        }
        return R.success(robotTasks);
    }


    @GetMapping("getRobotSite")
    public R<Map<String,Integer>> getRobotSite(){

        //暂时模拟机器人在哪个位置
        Map<String,Integer> robotSiteMap = new HashMap<>();
//        List<Participant> participants = crowdKernelComponent.getKernel().getParticipants();
//
//
//        //遍历participants,从crowdKernel中获取参与者的venueId
//        for (Participant participant : participants) {
//            int i = 0;
//            if (participant instanceof CoordinateParticipant) {
//                CoordinateParticipant coordinateParticipant = (CoordinateParticipant) participant;
//                robotSiteMap.put("robot"+i,coordinateParticipant.getVenueId());
//                i++;
//            }
//        }
        System.out.println("robotSiteMap");
        robotSiteMap.put("robot0", 3);
        robotSiteMap.put("robot1", 4);
        robotSiteMap.put("robot2", 5);


        return R.success(robotSiteMap);

    }

    /**
     * 获取crowdKernel中的任务
     *
     * @return {@code R<List<Integer>>}
     */
    @GetMapping("getTaskSite")
    public R<List<Integer>> getTaskSite(){

        List<Task> tasks = crowdKernelComponent.getKernel().getTasks();

        //暂时存储数据库中的任务
        List<Integer> taskSiteList = new ArrayList<>();

        //遍历tasks,从crowdKernel中获取任务
        for (Task task : tasks) {
            if (task instanceof CoordinateTask) {
                CoordinateTask coordinateTask = (CoordinateTask) task;
                taskSiteList.add(coordinateTask.getVenueId());
            }
        }

        return R.success(taskSiteList);

    }

    @GetMapping("unityTaskAssignBegin")
    public R<Boolean> unityTaskAssignBegin() {
        globalComponent.setFlag(true);
        return R.success(globalComponent.isFlag());
    }


    @PostMapping("unityTaskAssignTest")
    public R<Map<String,List<RobotTask>>> unityTaskAssignTest(@RequestBody List<RobotTask> robotTaskList) {
        //保存任务分配结果
        Map<String, List<RobotTask>> taskAssignMap = new HashMap<>();
        System.out.println("接收到机器人感知到的任务： " + robotTaskList);
        //直接返回，先测试能否正常接收到数据
        taskAssignMap.put("robot0", robotTaskList);
        taskAssignMap.put("robot1", null);
        return R.success(taskAssignMap);
    }

    @PostMapping("unityTaskAssign")
    public R<Map<String,List<RobotTask>>> unityTaskAssign(@RequestBody List<RobotTask> robotTaskList){


        //测试能否正常接收到数据
        System.out.println("接收到机器人感知到的任务： "+robotTaskList);

        //将robotTask的taskId设置为全局变量taskId
        for (RobotTask robotTask : robotTaskList) {
            robotTask.setTaskId(globalComponent.getTaskId());
            globalComponent.setTaskId(globalComponent.getTaskId() + 1);
        }

        //将robotTaskList加入到globalComponent.taskList中
        globalComponent.getTaskList().addAll(robotTaskList);

        System.out.println("globalComponent.getTaskList()"+globalComponent.getTaskList());


        //todo 这段代码调用GPT来分解子任务，在utils包中
        List<List<String>> subTaskList = new ArrayList<>();
//        subTaskList.add(new ArrayList<>()); // 添加一个空列表
//        subTaskList.get(0).add("这是子任务的示例"); // 向第一个列表中添加元素
//        subTaskList.get(0).add("动作+目标");
//        subTaskList.add(new ArrayList<>()); // 添加第二个列表
//        subTaskList.get(1).add("导航"); //
//        subTaskList.get(1).add("菜鸟驿站"); //


        globalComponent.taskAssign();

        return R.success(globalComponent.getTaskAssignMap());

    }

    /**
     * 不调用CrowdOS进行任务分配
     *
     * @return {@code R<Map<String,List<Integer>>>}
     */
    @GetMapping("taskAssign2")
    public R<Map<String,List<Integer>>> taskAssign2(){

        //保存任务分配结果
        Map<String,List<Integer>> taskAssignMap = new HashMap<>();


        //机器人的位置，对应venueId
        int[] robotSite = {
                3,4,5
        };
        //任务的位置
        int[] taskSite = {
                0,1,2,6,7,8,9
        };


        int workNum = robotSite.length;
        int taskNum = taskSite.length;

        //初始化距离矩阵
        double[][] distanceMatrix = new double[workNum][taskNum];
        //初始化任务距离矩阵
        double[][] taskDistanceMatrix = new double[taskNum][taskNum];

        //通过循环计算距离矩阵
        for (int i = 0; i < workNum; i++) {
            for (int j = 0; j < taskNum; j++) {
                distanceMatrix[i][j] =
                        Math.sqrt(Math.pow(venueCoordinateComponent.getVenueLocationMap().get(robotSite[i]).get(0)-
                                venueCoordinateComponent.getVenueLocationMap().get(taskSite[j]).get(0),2) +
                                Math.pow(venueCoordinateComponent.getVenueLocationMap().get(robotSite[i]).get(1)-
                                        venueCoordinateComponent.getVenueLocationMap().get(taskSite[j]).get(1),2));
            }
        }


        //通过循环计算任务距离矩阵
        for (int i = 0; i < taskNum; i++) {
            for (int j = 0; j < taskNum; j++) {
                taskDistanceMatrix[i][j] =
                        Math.sqrt(Math.pow(venueCoordinateComponent.getVenueLocationMap().get(taskSite[i]).get(0)-
                                venueCoordinateComponent.getVenueLocationMap().get(taskSite[j]).get(0),2) +
                                Math.pow(venueCoordinateComponent.getVenueLocationMap().get(taskSite[i]).get(1)-
                                        venueCoordinateComponent.getVenueLocationMap().get(taskSite[j]).get(1),2));
            }
        }

        //每个任务需要的工人数量
        int[] p = new int[taskNum];
        for (int i = 0; i < taskNum; i++) {
            p[i] = 1;
        }

        //每个工人最多接受多少个任务
        int q = 3;

//        GGA_I_Main gga_i_main = new GGA_I_Main(workNum,taskNum,distanceMatrix,taskDistanceMatrix,p,q);
//        gga_i_main.taskAssign();
//        Map<Integer,List<Integer>> assignMap = gga_i_main.getAssignMap();

        PT_Most pt_most = new PT_Most(workNum,taskNum,distanceMatrix,taskDistanceMatrix,p,q);
        pt_most.taskAssign();
        Map<Integer,List<Integer>> assignMap = pt_most.getAssignMap();


        //复制assignMap到taskAssignMap
        for (Map.Entry<Integer, List<Integer>> entry : assignMap.entrySet()) {
            taskAssignMap.put("robot"+entry.getKey(),entry.getValue());
        }

        //遍历taskAssignMap的value,如果List中的值大于等于robotSite[0]，则将其加上robotSite.length
        for (Map.Entry<String, List<Integer>> entry : taskAssignMap.entrySet()) {
            List<Integer> taskList = entry.getValue();
            for (int i = 0; i < taskList.size(); i++) {
                if (taskList.get(i) >= robotSite[0]){
                    taskList.set(i,taskList.get(i)+robotSite.length);
                }
            }
        }


        return R.success(taskAssignMap);
    }


    /**
     * 一键添加任务和参与者到CrowdOS内核
     *
     */
    @PostMapping("/addRobotTaskAndRobot")
    public void addRobotTaskAndRobot(){
        //机器人的位置，对应venueId
        int[] robotSite = {
                3,4,5
        };
        //遍历robotSite，添加机器人为CrowdOS的参与者
        for (int i = 0; i < robotSite.length; i++) {
            Integer venueId = Integer.valueOf(robotSite[i]);
            Double longitude = venueCoordinateComponent.getVenueLocationMap().get(venueId).get(0);
            Double latitude = venueCoordinateComponent.getVenueLocationMap().get(venueId).get(1);
            Coordinate coordinate = new Coordinate(longitude,latitude);
            CoordinateParticipant coordinateParticipant = new CoordinateParticipant(coordinate);
            coordinateParticipant.setVenueId(venueId);
            crowdKernelComponent.getKernel().registerParticipant(coordinateParticipant);
        }

        //任务的位置
        int[] taskSite = {
                0,1,2,6,7,8,9
        };
        //遍历taskSite，添加任务为CrowdOS的任务
        for (int i = 0; i < taskSite.length; i++) {
            Integer venueId = taskSite[i];
            Double longitude = venueCoordinateComponent.getVenueLocationMap().get(venueId).get(0);
            Double latitude = venueCoordinateComponent.getVenueLocationMap().get(venueId).get(1);
            Coordinate coordinate = new Coordinate(longitude,latitude);
            POIConstraint poiConstraint = new POIConstraint(coordinate);
            CoordinateTask coordinateTask = new CoordinateTask(Collections.singletonList(poiConstraint)
                    , Task.TaskDistributionType.ASSIGNMENT);
            coordinateTask.setVenueId(venueId);
            crowdKernelComponent.getKernel().submitTask(coordinateTask);
        }
    }


    /**
     * 调用CrowdOS进行任务分配，每次任务分配时，是将保存在内存中所有任务和参与者进行分配，所以每次分配完要重启程序
     *
     * @return {@code R<Map<String,List<Integer>>>}
     */
    @GetMapping("taskAssign")
    public R<Map<String,List<Integer>>> taskAssign(){

//        addRobotTaskAndRobot();
        Map<Participant,List<Task>> tasksAssignmentScheme = crowdKernelComponent.getKernel()
                .getTasksAssignmentScheme(crowdKernelComponent.getKernel().getTasks());

        Map<String,List<Integer>> tasksAssignMap = new HashMap<>();

        //复制tasksAssignmentScheme到tasksAssignMap
        for (Map.Entry<Participant, List<Task>> entry : tasksAssignmentScheme.entrySet()) {
            List<Integer> taskList = new ArrayList<>();
            for (Task task : entry.getValue()) {
                taskList.add(((CoordinateTask)task).getVenueId());
            }
            tasksAssignMap.put("robot"+(((CoordinateParticipant)entry.getKey()).getVenueId()-3),taskList);
        }


        return R.success(tasksAssignMap);
    }

}
