package com.shengjie.algorithms.GGA_I_new;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author wushengjie
 * @date 2022/11/21
 * 基于贪心的任务分配算法NearestFirst
 * GGA-I用来初始化种群
 * 距离矩阵中，row表示工人，col表示任务
 */
public class NearestFirst {

    private int workerNum; //工人数量

    private int taskNum; //任务数量
    private double[][] distanceMatrix; //距离矩阵

    private int q = 3; //每个工人最多执行多少个任务

    private int[] p; //每个任务需要多少个工人执行

    private Map<Integer, List<Integer>> assignMap = new HashMap<>(); //任务分配结果

    private final double INF = Double.MAX_VALUE;


    public NearestFirst(int workerNum,int taskNum, double[][] distanceMatrix,int[] p , int q) {
        this.workerNum = workerNum;
        this.taskNum = taskNum;
        this.distanceMatrix = distanceMatrix;
        this.q = q;
        this.p = p;

        //初始化Map
        for (int i = 0; i < workerNum; i++) {
            this.assignMap.put(i, new ArrayList<>());
        }
    }


    /**
     * 算法主体函数
     */
    public void taskAssign() {

        int[] index;

        //工人序号
        int workerIndex;

        //任务序号
        int taskIndex;

        while (true) {

            //检查所有任务是否分配完成
            if (allTaskFinish()) {
                break;
            }

            index = findMinIndex();
            workerIndex = index[0];
            taskIndex = index[1];

            //检查该工人是否还能执行任务,该任务是否还需要工人
            if(assignMap.get(workerIndex).size() >= q || countTaskIndex(taskIndex) == p[taskIndex]){

                ////该工人无法再接受任务了，将该行元素全设为最大
                if (assignMap.get(workerIndex).size() >= q){

                    for (int i = 0; i < taskNum; i++) {
                        distanceMatrix[workerIndex][i] = INF;
                    }
                }
                //该任务不再工人了，将该列元素全设为最大
                if (countTaskIndex(taskIndex) == p[taskIndex]){

                    for (int i = 0; i < workerNum; i++) {
                        distanceMatrix[i][taskIndex] = INF;
                    }
                }
                //跳过当前循环，不分配任务
                continue;
            }

            //加入分配结果Map
            assignMap.get(workerIndex).add(taskIndex);
            //重置矩阵
            distanceMatrix[workerIndex][taskIndex] = INF;

        }

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
     * 检查所有任务是否分配完成
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








