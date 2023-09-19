package com.shengjie.algorithms.GGA_I;

import java.io.*;
import java.util.*;

/**
 * 个体
 */
//执行Serializable接口为了”序列化实现深拷贝”
public class Individual implements Serializable {

    //基因序列
    private int[] genes;

    //保存虚点的列表,最后一个工人不保存
    private List<Integer> dummyPoint;

    //任务分配的结果，每一个列表对应一个工人分配结果
    private List<List<Integer>> assignResultList;


    //每个工人最多执行多少个任务
    private int q = 5;

    //路程
    private double distance;
    //适应度（在tsp问题中，路程越短越好，即路程越短，适应度越高，取fitness=1.0/distance）
    private double fitness;
    //生存率（适应度越高，生存率越高）
    private double survivalRate;


    public Individual(int taskNum, int workerNum) {
        this.genes = new int[workerNum + taskNum];
        this.dummyPoint = new ArrayList<>();
        this.assignResultList = new ArrayList<>();

        for (int i = taskNum; i < taskNum + workerNum - 1; i++) {
            this.dummyPoint.add(i);

        }


        this.initGenesByRandom(taskNum, workerNum);

    }

    /**
     * 随机初始化基因
     */
    public void initGenesByRandom(int taskNum, int workerNum) {

        List<Integer> tempList = new ArrayList<>();

        for (int i = 0; i < this.genes.length; i++) {
            tempList.add(i);
        }


        //随机打乱集合的元素
        Collections.shuffle(tempList);
        
        //最后一个工人的序号
        Integer begin = taskNum + workerNum - 1;

        //找到最后一个工人的位置
        int index = tempList.indexOf(begin);

        //将最后一个工人的位置放到第一位
        //Collections.swap(assignList,0,index);
        Integer temp = tempList.get(0);
        tempList.set(0,begin);
        tempList.set(index,temp);


        //TODO:这里加一个判断，即限制条件：每个工人最多执行多少个任务，若不满足则重新初始化,可以写一个函数，不然交叉和变异完也要判断


//        System.out.println(tempList.toString());
        for (int i = 0; i < tempList.size(); i++) {
            this.genes[i] = tempList.get(i);
        }

        List<Integer> assignList = new ArrayList<>();


        List<List<Integer>> allAssignList = new ArrayList<>();


        for (int i = 0; i < this.genes.length; i++) {

            //判断当前点是否为虚点
            if (this.dummyPoint.contains(this.genes[i])){

                allAssignList.add((List<Integer>) this.deepClone(assignList));

                assignList.clear();

                assignList.add(this.genes[i]);

            }
            else {
                assignList.add(this.genes[i]);
            }

        }

        allAssignList.add(assignList);


        this.assignResultList = allAssignList;


    }

    /**
     * 计算个体的tsp回路路程距离和适应值
     * 若5个任务，3个工人，基因序列为[1,0,5,4,2,6,3]，5，6为虚点
     * 表示 工人1执行1,0任务 工人2执行4,2任务 工人3执行3任务
     */
    public void calculateDistanceAndFitness(double[][] distanceMatrix) {

        //计算路程距离
        //计算从第0个城市到最后一个城市的路程

        this.distance = 0;


        for(List<Integer> list1: this.assignResultList){
            for (int i = 0; i < list1.size()-1; i++){
                this.distance += distanceMatrix[list1.get(i)][list1.get(i+1)];
            }
            //计算最后一个城市到第0个城市的路程
            this.distance += distanceMatrix[list1.get(0)][list1.get(list1.size()-1)];

        }



        ///计算适应值
        this.fitness = 1.0 / this.distance;
    }

    /**
     * 对个体基因中的两个元素进行交换
     */
    public void swapTwoElement() {
        //对序列中的元素进行打乱，即可产生新的序列

        Random random = new Random();

        //0位置不生成，第一个位置固定给第一个工人
        int i = random.nextInt(this.genes.length-1);
        int j = random.nextInt(this.genes.length-1);
        while (i == j) {
            j = random.nextInt(this.genes.length-1);
        }

        int temp = this.genes[i+1];
        this.genes[i+1] = this.genes[j+1];
        this.genes[j+1] = temp;
    }

    public List<List<Integer>> getAssignResultList() {
        return assignResultList;
    }

    public int[] getGenes() {
        return genes;
    }

    public void setGenes(int[] genes) {
        this.genes = genes;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getFitness() {
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    public double getSurvivalRate() {
        return survivalRate;
    }

    public void setSurvivalRate(double survivalRate) {
        this.survivalRate = survivalRate;
    }

    @Override
    public String toString() {
        return "Individual{" +
                "genes=" + Arrays.toString(genes) +
                ", distance=" + distance +
                '}';
    }

    public Object deepClone(Object srcObject) {
        ByteArrayOutputStream bos = null;
        ObjectOutputStream oos = null;
        ByteArrayInputStream bis = null;
        ObjectInputStream ois = null;
        Object result = null;
        try {
            bos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(bos);
            //将对象写到流里
            oos.writeObject(srcObject);
            //从流里读回来
            bis = new ByteArrayInputStream(bos.toByteArray());
            ois = new ObjectInputStream(bis);
            result = ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                bos.close();
                oos.close();
                bis.close();
                ois.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public void resetAssignResultList(){

        List<Integer> assignList = new ArrayList<>();

        List<List<Integer>> allAssignList = new ArrayList<>();

        for (int i = 0; i < this.genes.length; i++) {

            //判断当前点是否为虚点
            if (this.dummyPoint.contains(this.genes[i])){

                allAssignList.add((List<Integer>) this.deepClone(assignList));

                assignList.clear();

                assignList.add(this.genes[i]);

            }
            else {
                assignList.add(this.genes[i]);
            }

        }

        allAssignList.add(assignList);


        this.assignResultList = allAssignList;


    }

    //此方法用来判断是否满足约束条件
    public boolean restriction(){
        for (List<Integer> singleList:assignResultList) {
            if (singleList.size()>q){
                return false;
            }
        }
        return true;
    }

}
