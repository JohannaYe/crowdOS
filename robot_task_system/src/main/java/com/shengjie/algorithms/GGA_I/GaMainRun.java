package com.shengjie.algorithms.GGA_I;

import java.io.File;
import java.io.FileInputStream;


public class GaMainRun {

    public static void main(String[] args) throws Exception {
        //声明变量
        //距离矩阵，可以直接获取任意两个任务、参与者和任务、参与者和参与者的距离
        double[][] distanceMatrix;

        //读取数据
        String data = read(new File("D:\\shengjie-task-allocation\\robot_task_system\\src\\main\\java\\com\\shengjie\\algorithms\\att48.txt"), "GBK");
        String[] cityDataArr = data.split("\n");

        //初始化数组
        distanceMatrix = new double[cityDataArr.length][cityDataArr.length];
        for (int i = 0; i < cityDataArr.length; i++) {
            String[] city1Arr = cityDataArr[i].split(" ");
            int cityOne = Integer.valueOf(city1Arr[0]);
            for (int j = 0; j < i; j++) {
                String[] city2Arr = cityDataArr[j].split(" ");
                int cityTwo = Integer.valueOf(city2Arr[0]);
                if (cityOne == cityTwo) {
                    distanceMatrix[cityOne - 1][cityTwo - 1] = 0;
                } else {
                    distanceMatrix[cityOne - 1][cityTwo - 1] = getDistance(Double.valueOf(city1Arr[1]),
                            Double.valueOf(city1Arr[2]), Double.valueOf(city2Arr[1]), Double.valueOf(city2Arr[2]));
                    //对称赋值
                    distanceMatrix[cityTwo - 1][cityOne - 1] = distanceMatrix[cityOne - 1][cityTwo - 1];
                }
            }
        }

        GaApi gaApi = new GaApi(distanceMatrix);
        gaApi.solve();
    }

    /**
     * 给定两个城市坐标，获取两个城市的直线距离
     *
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    //欧式距离
    private static double getDistance(double x1, double y1, double x2, double y2) {
        return Math.sqrt((Math.pow((x1 - x2), 2) + Math.pow((y1 - y2), 2))/10);
    }

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
