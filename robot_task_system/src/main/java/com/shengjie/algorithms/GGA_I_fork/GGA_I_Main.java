package com.shengjie.algorithms.GGA_I_fork;

import java.io.*;
import java.util.*;


/**
 * GGA-I算法主体
 *
 * @author wushengjie
 * @date 2022/11/21
 */
public class GGA_I_Main {

    private int workerNum; //工人数量

    private int taskNum; //任务数量

    private double[][] distanceMatrix; //距离矩阵

    private double[][] taskDistanceMatrix; //任务距离矩阵

    private int q; //每个工人最多执行多少个任务

    private int[] p; //每个任务需要多少个工人执行

    private double threshold = 10000; //阈值
    private int populationNum = 100; //种群个数

    private int maxGen = 300; //进化代数
    private double crossRate = 0.98;//交叉概率
    private double mutateRate = 0.4;//变异概率

    private final double INF = Double.MAX_VALUE;

    private Map<Integer, List<Integer>> assignMap = new HashMap<>(); //保存任务分配结果


    /**
     * @param workerNum          工人数量
     * @param taskNum            任务数量
     * @param distanceMatrix     工人任务距离矩阵，保存工人和任务之间的距离（或者为某工人完成某任务的代价）
     * @param taskDistanceMatrix 任务距离矩阵，保存任务和任务之间的距离
     * @param p                  约束条件，每个任务需要多少工人
     * @param q                  约束条件，每个工人最多分配多少任务
     */
    public GGA_I_Main(int workerNum, int taskNum, double[][] distanceMatrix, double[][] taskDistanceMatrix, int[] p, int q) {
        this.workerNum = workerNum;
        this.taskNum = taskNum;
        this.distanceMatrix = distanceMatrix;
        this.taskDistanceMatrix = taskDistanceMatrix;
        //将任务矩阵对角元素置为INF
        for (int i = 0; i < taskNum; i++) {
            this.taskDistanceMatrix[i][i] = INF;
        }
        this.q = q;
        this.p = p;
    }

    /**
     * 算法主函数
     */
    public void taskAssign() {

        System.out.println("开始GGA-I算法求解任务分配问题......");
        //变量声明
        //种群
        List<Individual> population = new ArrayList<>();
        //随机数工具
        Random random = new Random();
        //开始计算时间
        long startTime = System.currentTimeMillis();
        //存储最优个体
        Individual bestIndividual = null;

        //求解
        //初始化种群
        initPopulation(population);
//        System.out.println("初始化种群结束");
        //迭代优化
        for (int t = 0; t < maxGen; t++) {

//            System.out.println("第" + t + "代");
            //选择
            Individual localBestIndividual = select(population, random);
            if (bestIndividual == null || localBestIndividual.getDistance() < bestIndividual.getDistance()) {
                bestIndividual = localBestIndividual;
//                System.out.println("最短距离：" + bestIndividual.getDistance());
            }
            //交叉
            crossover(population, random);
//            System.out.println("交叉结束");
            //变异
            mutate(population, random);
//            System.out.println("变异结束");


        }


        //获取最优解
        System.out.println("最短代价为：" + bestIndividual.getDistance());
        System.out.println("最优染色体：" + bestIndividual.getAssignMap().toString());
        System.out.println("最优工人任务分配方案:");
        for (Map.Entry<Integer, List<Integer>> entry : bestIndividual.getAssignMap().entrySet()) {
            System.out.println("工人" + entry.getKey() + "分配的任务为：" + entry.getValue());
        }
        System.out.println("运行时间：" + (System.currentTimeMillis() - startTime) + "ms");

        //复制bestIndividual.getAssignMap()到assignMap
        assignMap = new HashMap<>(bestIndividual.getAssignMap());
    }

    /**
     * 初始化种群
     *
     * @param population
     */
    public void initPopulation(List<Individual> population) {
        for (int i = 0; i < populationNum; i++) {
            if (i == 0) {
                population.add(new Individual(workerNum, taskNum, distanceMatrix, taskDistanceMatrix, p, q, 0));
            } else {
                population.add(new Individual(workerNum, taskNum, distanceMatrix, taskDistanceMatrix, p, q));
            }

        }
    }

    /**
     * 计算种群中每个个体的适应度和生存率
     * 选择出最优个体（适应值最高）
     *
     * @param population
     * @return {@code Individual}
     */
    public Individual calculateFitnessAndSurvivalRateOfIndividualAndChooseBestOne(List<Individual> population) {
        ///变量声明
        //存储种群中所有个体的总适应值
        double totalFitness = 0;
        //存储最优适应值
        double bestFitness = -Double.MAX_VALUE;
        //存储最优个体
        Individual bestIndividual = null;

        ///计算个体的适应值，并选择出最优个体
        for (Individual individual : population) {
            individual.calculateDistanceAndFitness();
            totalFitness += individual.getFitness();
            if (individual.getFitness() > bestFitness) {
                bestFitness = individual.getFitness();
                bestIndividual = individual;
            }
        }
        //将最优个体从种群中移除
        population.remove(bestIndividual);
        //删除最优个体对应的适应值
        totalFitness -= bestIndividual.getFitness();

        ///计算种群中剩余个体的生存率
        for (Individual individual : population) {
            //个体生存率=个体适应值/种群总适应值
            individual.setSurvivalRate(individual.getFitness() / totalFitness);
        }

        return bestIndividual;
    }

    /**
     * 选择优秀个体，选择出适应值最高的个体，将个体复制几个，剩余的名额使用轮盘赌从种群剩余个体中选出
     *
     * @param population
     * @param random
     * @return {@code Individual}
     */
    public Individual select(List<Individual> population, Random random) {
        //变量声明
        //新的种群
        List<Individual> newPopulation = new ArrayList<>();
        //将最优个体复制几次
        int cloneNumOfBestIndividual = 3;
        //至少有一次，否则不会被添加到新种群
//        cloneNumOfBestIndividual = cloneNumOfBestIndividual == 0 ? 1 : cloneNumOfBestIndividual;

        //选择个体，选择方式：1、选择种群的最优个体，然后复制几次；2、轮盘赌选择剩余的个体
        //计算种群中每个个体的适应度和生存率并选择出最优个体
        Individual bestIndividual = calculateFitnessAndSurvivalRateOfIndividualAndChooseBestOne(population);
        ///1、选择种群的最优个体，然后复制几次，将最优个体复制多个，存到新的集合中
        for (int i = 0; i < cloneNumOfBestIndividual; i++) {
            //deepClone对对象进行深拷贝，否则，改变一个属性，其他几个的属性会跟着变化
            newPopulation.add((Individual) deepClone(bestIndividual));
        }

        ///2、轮盘赌确定剩余个体
        for (int i = 0; i < (populationNum - cloneNumOfBestIndividual); i++) {
            double p = random.nextDouble();
            double sumP = 0;
            for (Individual individual : population) {
                sumP += individual.getSurvivalRate();
                if (sumP >= p) {
                    //选择个体
                    newPopulation.add((Individual) deepClone(individual));
                    break;
                }
            }
        }
        //用newPopulationt替换population
        population.clear();
        population.addAll(newPopulation);


        return bestIndividual;
    }

    /**
     * 交叉
     * 当随机数>pcl，小于pch时，随机找两个个体出来进行基因交叉互换
     *
     * @param population
     * @param random
     */

    public void crossover(List<Individual> population, Random random) {

        double p = random.nextDouble();

        if (p < crossRate) {

            //随机找两个索引,在种群中随机找两个个体
            int i = random.nextInt(population.size());
            int j = random.nextInt(population.size());
            while (i == j) {
                j = random.nextInt(population.size());
            }
            Individual individualI = population.get(i);
            Individual individualJ = population.get(j);

//            System.out.println("执行交叉前-----------------------------------");
//            System.out.println(individualI.getAssignMap().toString());
//            System.out.println(individualJ.getAssignMap().toString());

            //随机找一个索引，即随机找一个基因片段
            int task = random.nextInt(taskNum);

            //个体1中，此任务有哪些工人
            List<Integer> workerList1 = new ArrayList<>();
            //个体2中，此任务有哪些工人
            List<Integer> workerList2 = new ArrayList<>();
            for (int k = 0; k < workerNum; k++) {
                if (individualI.getAssignMap().get(k).contains(task)) {
                    workerList1.add(k);
                }
                if (individualJ.getAssignMap().get(k).contains(task)) {
                    workerList2.add(k);
                }
            }
            //交换workerList1和workerList2的元素，即交换两个任务的工人
            for (int k = 0; k < workerList1.size(); k++) {
                int worker1 = workerList1.get(k);
                individualI.getAssignMap().get(worker1).remove((Integer) task);
            }
            for (int k = 0; k < workerList2.size(); k++) {
                int worker2 = workerList2.get(k);
                individualJ.getAssignMap().get(worker2).remove((Integer) task);
            }

            for (int k = 0; k < workerList1.size(); k++) {
                int worker1 = workerList1.get(k);
                individualJ.getAssignMap().get(worker1).add(task);
            }
            for (int k = 0; k < workerList2.size(); k++) {
                int worker2 = workerList2.get(k);
                individualI.getAssignMap().get(worker2).add(task);
            }

            //交叉后，可能会出现工人的任务数超过了上限1个，所以需要进行修复
            repairIndividual(individualI);
            repairIndividual(individualJ);

//            System.out.println("执行交叉后-----------------------------------");
//            System.out.println(individualI.getAssignMap().toString());
//            System.out.println(individualJ.getAssignMap().toString());

            population.set(i, individualI);
            population.set(j, individualJ);
        }
    }

    /**
     * 交叉完修复个体，使其满足约束条件
     *
     * @param individual
     */
    public void repairIndividual(Individual individual) {

        Random random = new Random();

        for (int i = 0; i < workerNum; i++) {
            List<Integer> taskList = individual.getAssignMap().get(i);
            if (taskList.size() > q) {
                //如果工人的任务数超过了上限，最多只可能超过一个，那么就随机把一个任务移除
                int index = random.nextInt(taskList.size());
                taskList.remove(index);
            }
        }

        //将未分配的任务分配给工人
        int count = 0;
        int workerIndex;
        for (int i = 0; i < taskNum; i++) {
            count = 0;
            for (Map.Entry<Integer, List<Integer>> entry : individual.getAssignMap().entrySet()) {
                if (entry.getValue().contains(i)) {
                    count++;
                }
            }
            //若该任务未分配完
            while (count < p[i]) {
                workerIndex = random.nextInt(workerNum);
                if (individual.getAssignMap().get(workerIndex).size() >= q ||
                        individual.getAssignMap().get(workerIndex).contains(i)) {
                    continue;
                }
                individual.getAssignMap().get(workerIndex).add(i);
                break;
            }
        }
    }


    /**
     * 变异：随机交换个体基因的两个元素
     *
     * @param population
     * @param random
     */
    public void mutate(List<Individual> population, Random random) {
        for (Individual individual : population) {
            //当随机数小于变异概率时，对个体的基因进行变异
            if (random.nextDouble() < mutateRate) {
                //对个体基因中的两个元素进行交换
                individual.mutation2();

            }
        }
    }

    /**
     * 深拷贝对象
     *
     * @param srcObject
     * @return {@code Object}
     */
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

    public int getWorkerNum() {
        return workerNum;
    }

    public void setWorkerNum(int workerNum) {
        this.workerNum = workerNum;
    }

    public int getTaskNum() {
        return taskNum;
    }

    public void setTaskNum(int taskNum) {
        this.taskNum = taskNum;
    }

    public double[][] getDistanceMatrix() {
        return distanceMatrix;
    }

    public void setDistanceMatrix(double[][] distanceMatrix) {
        this.distanceMatrix = distanceMatrix;
    }

    public double[][] getTaskDistanceMatrix() {
        return taskDistanceMatrix;
    }

    public void setTaskDistanceMatrix(double[][] taskDistanceMatrix) {
        this.taskDistanceMatrix = taskDistanceMatrix;
    }

    public int getQ() {
        return q;
    }

    public void setQ(int q) {
        this.q = q;
    }

    public int[] getP() {
        return p;
    }

    public void setP(int[] p) {
        this.p = p;
    }

    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }

    public int getPopulationNum() {
        return populationNum;
    }

    public void setPopulationNum(int populationNum) {
        this.populationNum = populationNum;
    }

    public int getMaxGen() {
        return maxGen;
    }

    public void setMaxGen(int maxGen) {
        this.maxGen = maxGen;
    }

    public double getCrossRate() {
        return crossRate;
    }

    public void setCrossRate(double crossRate) {
        this.crossRate = crossRate;
    }

    public double getMutateRate() {
        return mutateRate;
    }

    public void setMutateRate(double mutateRate) {
        this.mutateRate = mutateRate;
    }

    public Map<Integer, List<Integer>> getAssignMap() {
        return assignMap;
    }

    public void setAssignMap(Map<Integer, List<Integer>> assignMap) {
        this.assignMap = assignMap;
    }

    @Override
    public String toString() {
        return "GaApi{" +
                "workerNum=" + workerNum +
                ", taskNum=" + taskNum +
                ", distanceMatrix=" + Arrays.toString(distanceMatrix) +
                ", taskDistanceMatrix=" + Arrays.toString(taskDistanceMatrix) +
                ", q=" + q +
                ", p=" + Arrays.toString(p) +
                ", threshold=" + threshold +
                ", populationNum=" + populationNum +
                ", maxGen=" + maxGen +
                ", crossRate=" + crossRate +
                ", mutateRate=" + mutateRate +
                ", INF=" + INF +
                '}';
    }
}

