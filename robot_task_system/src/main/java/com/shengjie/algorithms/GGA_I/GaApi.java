package com.shengjie.algorithms.GGA_I;


import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.io.*;
import java.util.*;


/**
 * 遗传算法
 * @author wushengjie
 * @date 2022/11/09
 */
public class GaApi {

    private int cityNum; //城市数

    private int taskNum = 40;
    private int workerNum= 8;

    //阈值
    private double threshold = 10000;

    //种群的基因个数
    private int populationNum = 100;
    //迭代次数
    private int maxGen = 30000; //进化代数
    private double crossRate= 0.98f;//交叉概率
    private double mutateRate = 0.4f;//变异概率
    private double[][] distanceMatrix; //地图数据

    public GaApi(double[][] distanceMatrix) {
        this.cityNum = distanceMatrix[0].length;
        this.distanceMatrix = distanceMatrix;
    }

    public void solve() {
        //变量声明
        //种群
        List<Individual> population = new ArrayList<>();
        //随机数工具
        Random random = new Random();
        //开始计算时间
        long startTime = System.currentTimeMillis();
        //存储最优个体
        Individual bestIndividual = null;

        //jfree绘图工具包
        XYSeriesCollection dataset = new XYSeriesCollection();
        XYSeries series1 = new XYSeries("Data");

        //求解
        //初始化种群
        this.initPopulation(population);
        //迭代优化
        for (int t = 0; t < maxGen; t++) {
            //选择
            Individual localBestIndividual = this.select(population, random);
            if (bestIndividual == null || localBestIndividual.getDistance() < bestIndividual.getDistance()) {
                bestIndividual = localBestIndividual;
                System.out.println("最短距离：" + bestIndividual.getDistance());
            }
            //交叉
            this.crossover(population, random);
            //变异
            this.mutate(population, random);


            //绘图工具包,添加数据
            double x = t;
            series1.add(x, bestIndividual.getDistance());

        }

        //绘图工具包,添加数据
        dataset.addSeries(series1);
        //绘图工具包,创建JFreeChart对象
        JFreeChart chart = ChartFactory.createXYLineChart(
                "GGA-I",
                "Generation",
                "Distance",
                dataset,
                PlotOrientation.VERTICAL,
                false,true, false);


        //绘图工具包,利用awt进行显示
        ChartFrame chartFrame = new ChartFrame("GGA-I", chart);
        chartFrame.pack();
        chartFrame.setVisible(true);

        //获取最优解
        System.out.println("最短距离：" + bestIndividual.getDistance());
        System.out.println("最优染色体：" + Arrays.toString(bestIndividual.getGenes()));
        System.out.println("最优参与者任务分配方案:");
        for (int i = 0; i < bestIndividual.getAssignResultList().size(); i++){
            System.out.println(bestIndividual.getAssignResultList().get(i).toString());
        }

        System.out.println("运行时间：" + (System.currentTimeMillis() - startTime) + "ms");
    }

    /**
     * 初始化种群
     * @param population
     */
    public void initPopulation(List<Individual> population) {
        for (int i = 0; i < this.populationNum; i++) {
            population.add(new Individual(this.taskNum,this.workerNum));
        }
    }

    /**
     * 计算种群中每个个体的适应度和生存率
     * 选择出最优个体（适应值最高）
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
            individual.calculateDistanceAndFitness(this.distanceMatrix);
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
        Individual bestIndividual = this.calculateFitnessAndSurvivalRateOfIndividualAndChooseBestOne(population);
        ///1、选择种群的最优个体，然后复制几次，将最优个体复制多个，存到新的集合中
        for (int i = 0; i < cloneNumOfBestIndividual; i++) {
            //deepClone对对象进行深拷贝，否则，改变一个属性，其他几个的属性会跟着变化
            newPopulation.add((Individual) this.deepClone(bestIndividual));
        }

        ///2、轮盘赌确定剩余个体
        for (int i = 0; i < (this.populationNum - cloneNumOfBestIndividual); i++) {
            double p = random.nextDouble();
            double sumP = 0;
            for (Individual individual : population) {
                sumP += individual.getSurvivalRate();
                if (sumP >= p) {
                    //选择个体
                    newPopulation.add((Individual) this.deepClone(individual));
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
     * @param population
     * @param random
     */

    public void crossover(List<Individual> population, Random random) {

        double p = random.nextDouble();

        if ( p < this.crossRate) {
            //随机找两个索引
            int i = random.nextInt(population.size());
            int j = random.nextInt(population.size());
            while (i == j) {
                j = random.nextInt(population.size());
            }
            Individual individualI = population.get(i);
            Individual individualJ = population.get(j);

//            System.out.println("执行交叉前-----------------------------------");
//            System.out.println(Arrays.toString(individualI.getGenes()));
//            System.out.println(Arrays.toString(individualJ.getGenes()));


            //使用LinkedHashSet存储元素，方便替换元素时，对元素进行删减
            LinkedHashSet<Integer> hashSetI = new LinkedHashSet<>();
            LinkedHashSet<Integer> hashSetJ = new LinkedHashSet<>();
            for (int k = 0; k < this.cityNum; k++) {
                hashSetI.add(individualI.getGenes()[k]);
                hashSetJ.add(individualJ.getGenes()[k]);
            }
            //开始交换的位置，一直交换到尾部，即单点交叉(begin至少要从1开始，否则没有意义)
            int begin = random.nextInt(this.cityNum - 1) + 1;
            for (int k = begin; k < this.cityNum; k++) {
                //交换两个基因的对应位置
                int temp = individualI.getGenes()[k];
                individualI.getGenes()[k] = individualJ.getGenes()[k];
                individualJ.getGenes()[k] = temp;
                //删除对应的元素，该元素已经在k位置了
                hashSetI.remove(individualI.getGenes()[k]);
                hashSetJ.remove(individualJ.getGenes()[k]);
            }
            //重新填充非交叉区域的元素
            begin = 0;
            for (Integer element : hashSetI) {
                individualI.getGenes()[begin++] = element;
            }
            begin = 0;
            for (Integer element : hashSetJ) {
                individualJ.getGenes()[begin++] = element;
            }
//            System.out.println("执行交叉后-----------------------------------");
//            System.out.println(Arrays.toString(individualI.getGenes()));
//            System.out.println(Arrays.toString(individualJ.getGenes()));
//            System.out.println();
            //交叉完重新初始化分配列表
            individualI.resetAssignResultList();
            individualJ.resetAssignResultList();

            population.set(i,individualI);
            population.set(j,individualJ);
        }
    }


    /**
     * 变异：随机交换个体基因的两个元素
     * @param population
     * @param random
     */
    public void mutate(List<Individual> population, Random random) {
        for (Individual individual : population) {
            //当随机数小于变异概率时，对个体的基因进行变异
            if (random.nextDouble() < this.mutateRate) {
                //对个体基因中的两个元素进行交换
                individual.swapTwoElement();

                //变异完重新初始化分配列表
                individual.resetAssignResultList();
            }
        }
    }

    /**
     * 深拷贝对象
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
}
