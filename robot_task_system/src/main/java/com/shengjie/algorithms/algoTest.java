package com.shengjie.algorithms;

import java.io.File;
import java.io.FileInputStream;

public class algoTest {
    public static void main(String[] args) throws Exception {
        //声明变量
        //距离矩阵，可以直接获取任意两个任务、参与者和任务、参与者和参与者的距离
        double[][] distanceMatrixRead;

        //读取数据
        String data = read(new File("D:\\shengjie-task-allocation\\robot_task_system\\src\\main\\java\\com\\shengjie\\algorithms\\att48.txt"), "GBK");
        String[] cityDataArr = data.split("\n");

        //初始化数组
        distanceMatrixRead = new double[cityDataArr.length][cityDataArr.length];
        for (int i = 0; i < cityDataArr.length; i++) {
            String[] city1Arr = cityDataArr[i].split(" ");
            int cityOne = Integer.valueOf(city1Arr[0]);
            for (int j = 0; j < i; j++) {
                String[] city2Arr = cityDataArr[j].split(" ");
                int cityTwo = Integer.valueOf(city2Arr[0]);
                if (cityOne == cityTwo) {
                    distanceMatrixRead[cityOne - 1][cityTwo - 1] = 0;
                } else {
                    distanceMatrixRead[cityOne - 1][cityTwo - 1] = getDistance(Double.valueOf(city1Arr[1]),
                            Double.valueOf(city1Arr[2]), Double.valueOf(city2Arr[1]), Double.valueOf(city2Arr[2]));
                    //对称赋值
                    distanceMatrixRead[cityTwo - 1][cityOne - 1] = distanceMatrixRead[cityOne - 1][cityTwo - 1];
                }
            }
        }

        int workerNum = 8;  //工人数量

        int taskNum = 40;   //任务数量

        int q = 10; //每个工人最多接受的任务数量

        int[] p = new int[taskNum]; //每个任务需要的工人数量
        for (int i = 0; i < p.length; i++) {
            p[i] = 1;
        }

//        double[][] distanceMatrix = new double[][]{
//                {8, 11, 2, 13, 4},
//                {1, 9, 7, 5, 3},
//                {12, 10, 6, 14, 15},
//        };
//
//        double[][] taskDistanceMatrix = new double[][]{
//                {99999999, 1, 2, 3, 4},
//                {1, 99999999, 5, 6, 7},
//                {2, 5, 99999999, 8, 9},
//                {3, 6, 8, 99999999, 10},
//                {4, 7, 9, 10, 99999999},
//        };

        //打印矩阵
//        for (int i = 0; i < distanceMatrixRead.length; i++) {
//            for (int j = 0; j < distanceMatrixRead[i].length; j++) {
//                System.out.print(distanceMatrixRead[i][j] + " ");
//            }
//            System.out.println();
//        }
        //距离矩阵
        double[][] distanceMatrix = new double[workerNum][taskNum];
        for (int i = 0; i < workerNum; i++) {
            for (int j = 0; j < taskNum; j++) {
                distanceMatrix[i][j] = distanceMatrixRead[taskNum+i][j];
            }
        }

        //任务距离矩阵
        double[][] taskDistanceMatrix = new double[taskNum][taskNum];
        for (int i = 0; i < taskNum; i++) {
            for (int j = 0; j < taskNum; j++) {
                taskDistanceMatrix[i][j] = distanceMatrixRead[i][j];
            }
        }

//        测试GGA-I算法
//        GaApi gaApi = new GaApi(workerNum, taskNum, distanceMatrix, taskDistanceMatrix, p, q);
//        gaApi.solve();

//        GGA_I_Main gga_i_main = new GGA_I_Main(workerNum, taskNum, distanceMatrix, taskDistanceMatrix, p, q);
//        gga_i_main.taskAssign();


//        //jfree绘图工具包
//        XYSeriesCollection dataset = new XYSeriesCollection();
//        XYSeries series1 = new XYSeries("Data");
//
//        //绘图工具包,添加数据
//        double x = t;
//        series1.add(x, bestIndividual.getDistance());
//
//        //绘图工具包,添加数据
//        dataset.addSeries(series1);
//        //绘图工具包,创建JFreeChart对象
//        JFreeChart chart = ChartFactory.createXYLineChart(
//                "GGA-I",
//                "Generation",
//                "Distance",
//                dataset,
//                PlotOrientation.VERTICAL,
//                false,true, false);
//
//
//        //绘图工具包,利用awt进行显示
//        ChartFrame chartFrame = new ChartFrame("GGA-I", chart);
//        chartFrame.pack();
//        chartFrame.setVisible(true);



        //测试T-Most算法
//        long startTime = System.currentTimeMillis();
//        System.out.println("开始T-Most算法求解任务分配问题......");
//        System.out.println("当前有"+workerNum+"个工人，"+taskNum+"个任务，每个工人最多只能接受"+q+"个任务，每个任务需要的工人数为"+p[0]+"个");
//        T_Most t_most = new T_Most(workerNum, taskNum, distanceMatrix, taskDistanceMatrix, p, q);
//        t_most.taskAssign();
//        System.out.println("最短代价为："+t_most.getDistance());
//        System.out.println("最优工人任务分配方案:");
//        t_most.printAssignMap();
//        System.out.println("运行时间：" + (System.currentTimeMillis() - startTime) + "ms");
////
////        测试T-Random算法
//        long startTime = System.currentTimeMillis();
//        System.out.println("开始T-Random算法求解任务分配问题......");
//        System.out.println("当前有"+workerNum+"个工人，"+taskNum+"个任务，每个工人最多只能接受"+q+"个任务，每个任务需要的工人数为"+p[0]+"个");
//        T_Random t_random = new T_Random(workerNum, taskNum, distanceMatrix, taskDistanceMatrix, p, q);
//        t_random.taskAssign();
//        System.out.println("最短代价为："+t_random.getDistance());
//        System.out.println("最优工人任务分配方案:");
//        t_random.printAssignMap();
//        System.out.println("运行时间：" + (System.currentTimeMillis() - startTime) + "ms");
//
//        测试PT-Most算法
//        long startTime = System.currentTimeMillis();
//        System.out.println("开始PT-Most算法求解任务分配问题......");
//        System.out.println("当前有"+workerNum+"个工人，"+taskNum+"个任务，每个工人最多只能接受"+q+"个任务，每个任务需要的工人数为"+p[0]+"个");
//        PT_Most pt_most = new PT_Most(workerNum, taskNum, distanceMatrix, taskDistanceMatrix, p, q);
//        pt_most.taskAssign();
//        System.out.println("最优工人任务分配方案:");
//        pt_most.printAssignMap();
//        System.out.println("最短代价为："+pt_most.getDistance());
//        System.out.println("运行时间：" + (System.currentTimeMillis() - startTime) + "ms");


    }

    /**
     * 给定两个点的坐标，获取两个点的直线距离
     * 欧式距离
     *
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return double
     */
    private static double getDistance(double x1, double y1, double x2, double y2) {
        return Math.sqrt((Math.pow((x1 - x2), 2) + Math.pow((y1 - y2), 2))/10);
    }

    /**
     * 给定两个点的坐标，获取两个点的直线距离
     * 曼哈顿距离
     *
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return double
     */
    private static double getDistance2(double x1, double y1, double x2, double y2) {
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }

    /**
     * 用于读取文件数据
     *
     * @param f
     * @param charset
     * @return {@code String}
     * @throws Exception
     */
    private static String read(File f, String charset) throws Exception {
        FileInputStream fstream = new FileInputStream(f);
        try {
            int fileSize = (int) f.length();
            if (fileSize > 1024 * 512) {
                throw new Exception("File too large to read! size=" + fileSize);
            }

            byte[] buffer = new byte[fileSize];
            fstream.read(buffer);
            return new String(buffer, charset);
        } finally {
            try {
                fstream.close();
            } catch (Exception e) {
            }
        }
    }

}