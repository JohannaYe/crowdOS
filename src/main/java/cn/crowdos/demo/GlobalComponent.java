package cn.crowdos.demo;

import cn.crowdos.demo.entity.RobotTask;
import cn.crowdos.kernel.algorithms.PT_Most;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Data
@Component
public class GlobalComponent {

    private boolean flag = false;

    private Integer taskId = 0;

    private Map<String, List<RobotTask>> taskAssignMap = new HashMap<>();

    private List<RobotTask> taskList = new ArrayList<>();

    @Autowired
    private VenueCoordinateComponent venueCoordinateComponent;

    @Autowired
    private RobotCoordinateComponent robotCoordinateComponent;

    public void taskAssign() {

        //todo 已经分配的任务不再分配，taskId不能重复，保证唯一性

        //taskAssignMap:任务分配结果
        Map<String, List<RobotTask>> taskAssignMap = new HashMap<>();

        //workNum:机器人数量,为robotCoordinate中键值的数量
        int workNum = robotCoordinateComponent.getRobotLocationMap().size();


        List<RobotTask> robotTaskList = taskList;

        //taskNum:任务数量，为robotTaskList的长度
        int taskNum = robotTaskList.size();

        //根据robotTask中的taskId排序
        Collections.sort(robotTaskList, new Comparator<RobotTask>() {
            @Override
            public int compare(RobotTask o1, RobotTask o2) {
                return o1.getTaskId() - o2.getTaskId();
            }
        });


        List<Integer> taskIdList = new ArrayList<>();
        for (RobotTask robotTask : robotTaskList) {
            //取出robotTaskList中的所有taskId
            taskIdList.add(robotTask.getTaskId());
        }

        //初始化距离矩阵
        double[][] distanceMatrix = new double[workNum][taskNum];
        //根据robotTask中的longitude和latitude、以及robotCoordinate中的longitude和latitude循环计算距离矩阵
        for (int i = 0; i < workNum; i++) {
            for (int j = 0; j < taskNum; j++) {
                //获取机器人的坐标
                Double robotLongitude = robotCoordinateComponent.getRobotLocationMap().get(i).get(0);
                Double robotLatitude = robotCoordinateComponent.getRobotLocationMap().get(i).get(1);

                //获取任务的坐标
                Double taskLongitude = robotTaskList.get(j).getLongitude();
                Double taskLatitude = robotTaskList.get(j).getLatitude();
                //计算距离
//              double distance = DistanceUtil.getDistance(robotLongitude,robotLatitude,taskLongitude,taskLatitude);

                //根据机器人的位置和任务的位置计算欧式距离
                double distance = Math
                        .sqrt(Math.pow(robotLongitude - taskLongitude, 2) + Math.pow(robotLatitude - taskLatitude, 2));

                distanceMatrix[i][j] = distance;
            }
        }

        //初始化任务距离矩阵
        double[][] taskDistanceMatrix = new double[taskNum][taskNum];
        //根据robotTask中的longitude和latitude循环计算任务距离矩阵
        for (int i = 0; i < taskNum; i++) {
            for (int j = 0; j < taskNum; j++) {
                //获取任务的坐标
                Double taskLongitude1 = robotTaskList.get(i).getLongitude();
                Double taskLatitude1 = robotTaskList.get(i).getLatitude();
                Double taskLongitude2 = robotTaskList.get(j).getLongitude();
                Double taskLatitude2 = robotTaskList.get(j).getLatitude();
                //计算欧式距离
                double distance = Math
                        .sqrt(Math.pow(taskLongitude1 - taskLongitude2, 2) + Math.pow(taskLatitude1 - taskLatitude2, 2));
                taskDistanceMatrix[i][j] = distance;
            }
        }

        //每个任务需要的工人数量
        int[] p = new int[taskNum];
        for (int i = 0; i < taskNum; i++) {
            p[i] = 1;
        }

        //每个工人最多接受多少个任务
        int q = taskNum / workNum + 1;

        PT_Most pt_most = new PT_Most(workNum, taskNum, distanceMatrix, taskDistanceMatrix, p, q);
        pt_most.taskAssign();
        Map<Integer, List<Integer>> assignMap = pt_most.getAssignMap();

//        System.out.println(assignMap);
//        System.out.println("taskIdList的长度：" + taskIdList.size());

        //将assignMap转换为taskAssignMap
        for (Map.Entry<Integer, List<Integer>> entry : assignMap.entrySet()) {

            //todo 传入的机器人名称是robot0和robot1，且id也是0和1，这里需要优化，如果不是，分配可能不对
            String robotName = "robot" + entry.getKey();
            List<Integer> taskIdList_ = entry.getValue();

            //taskIdList_中的值，对应taskIdList中的索引

            List<RobotTask> robotTaskList1 = new ArrayList<>();
            for (Integer taskId : taskIdList_) {
                System.out.println(taskId);

                Integer index = taskIdList.get(taskId);
                //在robotTaskList中取出对应的RobotTask，查找对应taskId为index的RobotTask
                RobotTask robotTask = robotTaskList.stream().filter(robotTask1 -> robotTask1.getTaskId() == index)
                        .findFirst().get();

                //todo 这里有问题，如果taskIdList中没有taskId，会报错
                robotTaskList1.add(robotTask);
            }
            taskAssignMap.put(robotName, robotTaskList1);
        }

        //复制taskAssignMap到taskAssignMap
        this.taskAssignMap = taskAssignMap;

        //发起一个HTTP get请求
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:8080/robotTask/unityTaskAssignBegin";
        restTemplate.getForObject(url, String.class);

    }
}
