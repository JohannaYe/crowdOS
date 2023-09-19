package com.shengjie.algorithms.GGA_U;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author wushengjie
 * @date 2022/11/09
 * 基于贪心的任务分配算法MostFirst
 * GGA-U用来初始化种群
 * 距离矩阵中，row表示工人，col表示任务
 */
public class MostFirst {

    private int workerNum; //工人数量

    private int taskNum; //任务数量
    private double[][] distanceMatrix; //距离矩阵

    private int q ; //每个工人最多执行多少个任务

    private int[] p; //每个任务需要多少个工人执行

    private Map<Integer, List<Integer>> assignMap = new HashMap<>(); //任务分配结果

    private final double INF = 99999999;

    private double threshold; //阈值

    private double[][] pwtMatrix; //pwt矩阵，工人i历史经过任务j位置的概率


    private List<List<Integer>> recordList = new ArrayList<>(); //保存工人历史时间段的位置记录，长度为24


    //保存工人历史时间段的位置记录，每个列表长度为workNum->24(时间段)->2(坐标)
    private List<List<List<Double>>> workerLocationList = new ArrayList<>();

    private List<List<Double>> taskLocationList = new ArrayList<>(); //保存任务的位置，每个列表长度为taskNum->2

    private double radius = 100; //工人的搜索半径


    /**
     * @param workerNum 工人数量
     * @param taskNum 任务数量
     * @param distanceMatrix 距离矩阵
     * @param p 每个任务需要多少个工人执行
     * @param q 每个工人最多执行多少个任务
     * @param workerLocationList 工人历史时间段的位置记录，每个列表长度为workNum->24(时间段)->2(坐标)
     * @param taskLocationList 任务的位置，每个列表长度为taskNum->2
     */
    public MostFirst(int workerNum, int taskNum, double[][] distanceMatrix, int[] p , int q,
                     List<List<List<Double>>> workerLocationList,
                     List<List<Double>> taskLocationList) {
        this.workerNum = workerNum;
        this.taskNum = taskNum;
        this.distanceMatrix = distanceMatrix;
        this.q = q;
        this.p = p;
        this.workerLocationList = workerLocationList;
        this.taskLocationList = taskLocationList;

        //初始化Map
        for (int i = 0; i < workerNum; i++) {
            this.assignMap.put(i, new ArrayList<>());
        }

        this.pwtMatrix = new double[workerNum][taskNum];

    }


    /**
     * 算法主体函数
     */
    public void taskAssign() {

        int count = 0;
        while (true) {
            count++;

            //防止因没有合适的工人陷入死循环
            if (count > taskNum*10) {
                break;
            }

            if (allTaskFinish()){
                break;
            }

            //初始化pwtMatrix
            for (int i = 0; i < workerNum; i++) {
                for (int j = 0; j < taskNum; j++) {
                    pwtMatrix[i][j] = 0;
                }
            }

            //找到还可分配任务的工人
            List<Integer> workerList = new ArrayList<>();
            for (int i = 0; i < workerNum; i++) {
                if (assignMap.get(i).size() < q) {
                    workerList.add(i);
                }
            }

            //找到还需要工人的任务
            List<Integer> taskList = new ArrayList<>();
            for (int i = 0; i < taskNum; i++) {
                if (countTaskIndex(i) < p[i]) {
                    taskList.add(i);
                }
            }

            //遍历workerList
            for (int i = 0; i < workerList.size(); i++) {
                int workerIndex = workerList.get(i);
                //遍历taskList
                for (int j = 0; j < taskList.size(); j++) {
                    int taskIndex = taskList.get(j);
                    //计算pwt
                    pwtMatrix[workerIndex][taskIndex] = calculatePWT(workerIndex, taskIndex);
                }
            }

            //对于每个工人，计算其大于等于阈值的任务数量,pwtMatrix中的值为不为0即代表该任务还需要工人，该工人还可接受任务
            int[] taskNums = new int[workerNum];
            for (int i = 0; i < workerNum; i++) {
                for (int j = 0; j < taskNum; j++) {
                    if (pwtMatrix[i][j] >= threshold) {
                        taskNums[i]++;
                    }
                }
            }

            //选取任务数量最多的工人
            int maxTaskNum = 0;
            int maxWorker = 0;
            for (int i = 0; i < workerNum; i++) {
                if (taskNums[i] > maxTaskNum) {
                    maxTaskNum = taskNums[i];
                    maxWorker = i;
                }
            }

            //将该工人的任务分配给该工人
            for (int j = 0; j < taskNum; j++) {
                if (isAssignWorker(maxWorker)) {
                    if (pwtMatrix[maxWorker][j] >= threshold) {
                        if (isAssignTask(j)) {
                            assignMap.get(maxWorker).add(j);
                            distanceMatrix[maxWorker][j] = INF;
                            isAssignWorker(maxWorker);
                            isAssignTask(j);
                        }
                    }
                } else {
                    break;
                }
            }

        }
    }

    /**
     * 计算p(w,t),即对于非紧急任务，工人w经过任务t的位置的概率，只有大于阈值时，才将该任务分给这个工人。
     * 工人历史record分为24个时间段，计算每个时间段是否有经过任务t的位置。
     * （以任务t的位置为圆心，设定一个半径，如果工人的位置在圆内，则表示经过），如果经过，则该时间段的为1，否则为0。
     * 若一天内有12个时间段经过任务t的位置，则p(w,t) = 12/24 = 0.5
     *
     * @param workerIndex
     * @param taskIndex
     * @return double
     */
    public double calculatePWT(int workerIndex,int taskIndex){
        int count = 0;
        List<List<Double>> workerRecord = workerLocationList.get(workerIndex);
        List<Double> taskLocation = taskLocationList.get(taskIndex);

        //遍历workerRecord
        for (int i = 0; i < workerRecord.size(); i++) {
            List<Double> location = workerRecord.get(i);
            double distance = Math.sqrt(Math.pow(location.get(0) - taskLocation.get(0),2) + Math.pow(location.get(1) - taskLocation.get(1),2));
            if (distance <= radius){
                count++;
            }
        }

        return count/24.0;
    }


    /**
     * 查询Map所有List中该任务出现的次数，即该任务分给多少工人了
     * @param taskIndex
     * @return int
     */
    public int countTaskIndex(int taskIndex) {
        int count = 0;
        for (Map.Entry<Integer, List<Integer>> entry : assignMap.entrySet()) {
            if (entry.getValue().contains(taskIndex)) {
                count++;
            }
        }
        return count;
    }

    /**
     * 判断该工人是否可以再接受任务，并将该行元素全设为最大
     *
     * @param workerIndex
     * @return boolean
     */
    public boolean isAssignWorker(int workerIndex) {
        if (assignMap.get(workerIndex).size() >= q) {

            for (int i = 0; i < taskNum; i++) {
                distanceMatrix[workerIndex][i] = INF;
            }
            return true;
        }
        return false;
    }

    /**
     * 判断该任务是否还需要工人，并将该列元素全设为最大
     *
     * @param taskIndex
     * @return boolean
     */
    public boolean isAssignTask(int taskIndex) {

        if (countTaskIndex(taskIndex) >= p[taskIndex]) {
            for (int i = 0; i < workerNum; i++) {
                distanceMatrix[i][taskIndex] = INF;
            }
            return true;
        }
        return false;
    }

    /**
     * 检查所有任务是否分配完成
     *
     * @return boolean
     */
    public boolean allTaskFinish(){
        for (int i = 0; i < taskNum; i++) {
            if (countTaskIndex(i) < p[i]){
                return false;
            }
        }
        return true;
    }

    /**
     * 返回距离矩阵中最小值的下标
     * @return {@code int[]}
     */
    public int[] findMinIndex() {
        int[] index = new int[2];
        double min = distanceMatrix[0][0];
        for (int i = 0; i < distanceMatrix.length; i++) {
            for (int j = 0; j < distanceMatrix[i].length; j++) {
                if (distanceMatrix[i][j] < min) {
                    min = distanceMatrix[i][j];
                    index[0] = i;
                    index[1] = j;
                }
            }
        }
        return index;
    }


    /**
     *打印任务分配结果
     */
    public void printAssignMap() {
        for (Map.Entry<Integer, List<Integer>> entry : assignMap.entrySet()) {
            System.out.println("工人" + entry.getKey() + "分配的任务为：" + entry.getValue());
        }
    }

    /**
     *打印距离矩阵
     */
    public void printDistanceMatrix(){
        System.out.println("当前距离矩阵为------------------------------------");
        for (int i = 0; i < distanceMatrix.length; i++) {
            for (int j = 0; j < distanceMatrix[i].length; j++) {
                System.out.print(distanceMatrix[i][j] + " ");
            }
            System.out.println();
        }
    }

    public Map<Integer, List<Integer>> getAssignMap() {
        return assignMap;
    }
}












