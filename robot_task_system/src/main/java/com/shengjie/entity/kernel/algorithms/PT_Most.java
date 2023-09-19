package com.shengjie.entity.kernel.algorithms;

import java.util.*;

/**
 * @author wushengjie
 * @date 2022/11/24
 */
public class PT_Most {

    private int workerNum;

    private int taskNum;

    private double[][] distanceMatrix;  //距离矩阵(工人和任务的距离)

    private double[][] taskDistanceMatrix; //任务距离矩阵，对角线元素为0或MAX_VALUE

    private double[][] distanceMatrixTemp; //距离矩阵的副本

    private int q; //每个工人最多执行多少个任务,或者说工人需要在有限时间内完成所有任务

    private int[] p; //每个任务需要多少个工人执行

    private Map<Integer, List<Integer>> assignMap = new HashMap<>(); //保存任务分配结果

    private Map<Integer, List<Integer>> assignMapTemp = new HashMap<>(); //保存任务分配结果的副本

    private final double INF = Double.MAX_VALUE; //距离矩阵为该值时，表示(i,j)不再分配任务


    //保存已经分配好任务的工人
    private List<Integer> workerList = new ArrayList<>();

    private double distance;

    private double[][] distanceMatrixTempCal; //距离矩阵的临时变量，最后用于计算总距离


    /**
     * @param workerNum          工人数量
     * @param taskNum            任务数量
     * @param distanceMatrix     工人任务距离矩阵，保存工人和任务之间的距离（或者为某工人完成某任务的代价）
     * @param taskDistanceMatrix 任务距离矩阵，保存任务和任务之间的距离
     * @param p                  约束条件，每个任务需要多少工人
     * @param q                  约束条件，每个工人最多分配多少任务
     */
    public PT_Most(int workerNum, int taskNum, double[][] distanceMatrix, double[][] taskDistanceMatrix, int[] p, int q) {
        this.workerNum = workerNum;
        this.taskNum = taskNum;
        this.distanceMatrix = distanceMatrix;
        this.taskDistanceMatrix = taskDistanceMatrix;
        //将任务距离矩阵对角线元素置为INF,方便计算
        for (int i = 0; i < taskDistanceMatrix.length; i++) {
            taskDistanceMatrix[i][i] = INF;
        }
        this.q = q;
        this.p = p;

        //初始化Map
        for (int i = 0; i < workerNum; i++) {
            this.assignMap.put(i, new ArrayList<>());
        }

        //复制距离矩阵
        distanceMatrixTempCal = new double[workerNum][taskNum];
        for (int i = 0; i < workerNum; i++) {
            for (int j = 0; j < taskNum; j++) {
                distanceMatrixTempCal[i][j] = distanceMatrix[i][j];
            }
        }

    }

    /**
     * 算法主函数
     */
    public void taskAssign() {


        int taskIndex;
        int workerIndex;


        //临时保存工人任务距离矩阵
        distanceMatrixTemp = new double[workerNum][taskNum];
        for (int i = 0; i < workerNum; i++) {
            for (int j = 0; j < taskNum; j++) {
                distanceMatrixTemp[i][j] = distanceMatrix[i][j];
            }
        }

        //检查所有任务是否分配完成
        while (!isTaskAssignFinish()) {


            while (!isTaskAssignFinish()) {


                //随机选择一个还未分配完任务的工人,并选择离该工人最近的任务
                List<Integer> randomWorkerList = new ArrayList<>();

                //遍历assignMapTemp，找到还未分配完任务的工人
                for (int i = 0; i < workerNum; i++) {
                    if (assignMap.get(i).size() < q) {
                        randomWorkerList.add(i);
                    }
                }

//                System.out.println(randomWorkerList);

                //所有工人都已经分配任务，跳出循环
                if (randomWorkerList.size() == 0) {
                    break;
                }


                Collections.shuffle(randomWorkerList);
                workerIndex = randomWorkerList.get(0);

                //taskIndex可能为-1
                taskIndex = findMinTaskToWorker(workerIndex);

                //该工人已经没有任务可分配，跳出循环
                if (taskIndex == -1) {

//                    printDistanceMatrix();
//                    System.out.println("1");
                    continue;
                }

                if (isAssignTask(taskIndex)) {
//                    System.out.println("2");
                    continue;
                }

                //加入分配结果Map
                assignMap.get(workerIndex).add(taskIndex);
                //重置矩阵
                distanceMatrix[workerIndex][taskIndex] = INF;


                //计数器
                int count = 0;

                while (!isAssignWorker(workerIndex)) {

                    if (isTaskAssignFinish()) {
                        break;
                    }

                    //若有5个任务，依次选择4个任务
                    if (count == taskNum - 1) {
                        break;
                    }


                    int[] taskIndexArray = findMinTaskToTask(taskIndex);


                    int taskIndex_ = taskIndexArray[count];
                    count++;


                    //检查该任务是否还需要工人
                    if (isAssignTask(taskIndex_)) {

                        continue;
                    } else {
                        //加入分配结果Map
                        assignMap.get(workerIndex).add(taskIndex_);
                        //重置矩阵
                        distanceMatrix[workerIndex][taskIndex_] = INF;

                        isAssignTask(taskIndex_);

                    }
                }

//                printAssignMap();
//                printDistanceMatrix();


            }
            //出BUG的话就是该函数有问题
            SelectMaxTU();
//            System.out.println("111111");
//            printAssignMap();
//            printDistanceMatrix();


        }

        countDistance();


    }

    //todo 判断传入的参数是否合法，比如任务需要的工人数量p[i]与每个工人最多能接受的任务数量q不匹配
    private boolean isParamValid() {

        int sum = 0;

        //遍历p数组
        for (int i = 0; i < p.length; i++) {
            sum += p[i];
        }

        //所有参与者最多可接受多少任务是否等于或大于总任务数
        if (q * workerNum < sum) {
            return false;
        }

        return true;
    }

    /**
     * 选出分配任务最多的工人，只保留他和他分配的任务
     */
    public void SelectMaxTU() {

        //选出assignMap中最长的Value
        int max = 0;
        int maxKey = 0;
        for (Map.Entry<Integer, List<Integer>> entry : assignMap.entrySet()) {
            if (entry.getValue().size() > max) {

                if (workerList.contains(entry.getKey())) {
                    continue;
                }

                max = entry.getValue().size();
                maxKey = entry.getKey();
            }
        }

        workerList.add(maxKey);


        //深拷贝list
        List<Integer> list = new ArrayList<>(assignMap.get(maxKey));


//        System.out.println("清空前");
//        printAssignMap();

        //清空assignMap中的其他value
        //遍历workerList
        for (Map.Entry<Integer, List<Integer>> entry : assignMap.entrySet()) {
            if (!workerList.contains(entry.getKey())) {
                entry.getValue().clear();
            }
        }

//        System.out.println("清空后");
//        printAssignMap();


        //遍历list，将distanceMatrixTemp中对应的元素置为INF
        for (Integer integer : list) {
            distanceMatrixTemp[maxKey][integer] = INF;
            isAssignTask(integer);
        }

        isAssignWorker(maxKey);

        //重置矩阵
        for (int i = 0; i < workerNum; i++) {
            for (int j = 0; j < taskNum; j++) {
                distanceMatrix[i][j] = distanceMatrixTemp[i][j];
            }
        }

    }

    /**
     * 找到离任务最近的工人
     *
     * @param taskIndex
     * @return int
     */
    private int findMinWorkerToTask(int taskIndex) {
        double min = INF;
        int minIndex = -1;
        for (int i = 0; i < workerNum; i++) {
            if (distanceMatrix[i][taskIndex] < min) {
                min = distanceMatrix[i][taskIndex];
                minIndex = i;
            }
        }
        return minIndex;
    }

    /**
     * 找到离工人最近的任务
     *
     * @param workerIndex
     * @return int
     */
    private int findMinTaskToWorker(int workerIndex) {
        double min = INF;
        int minIndex = -1;
        for (int i = 0; i < taskNum; i++) {
            if (distanceMatrix[workerIndex][i] < min) {
                min = distanceMatrix[workerIndex][i];
                minIndex = i;
            }
        }
        return minIndex;
    }

    /**
     * 将所有任务离此任务的距离按升序排列，按任务序号储存
     *
     * @param taskIndex
     * @return {@code int[]}
     */
    private int[] findMinTaskToTask(int taskIndex) {

        //保存任务序号
        int[] taskIndexArray = new int[taskNum];

        Map<Integer, Double> taskDistanceMap = new HashMap<>();

        for (int i = 0; i < taskNum; i++) {
            taskDistanceMap.put(i, taskDistanceMatrix[taskIndex][i]);
        }

        //对map按value进行排序
        Map<Integer, Double> sortMap = sortMap(taskDistanceMap);


        //遍历taskDistanceMap，将对应的任务序号存入taskIndexArray
        int count = 0;
        for (Map.Entry<Integer, Double> entry : sortMap.entrySet()) {
            taskIndexArray[count] = entry.getKey();
            count++;
        }

        //打印taskIndexArray
//        System.out.println(taskIndex);
//        for (int i = 0; i < taskNum; i++) {
//            System.out.print(taskIndexArray[i] + " ");
//        }
//        System.out.println();

        return taskIndexArray;
    }


    /**
     * 1、将Map的entrySet转换为List
     * 2、用Collections工具类的sort方法排序
     * 3、遍历排序好的list，将每组key，value放进LinkedHashMap(Map的实现类只有LinkedHashMap是根据插入顺序来存储)
     *
     * @param map
     * @return {@code Map<Integer, Double>}
     */
    public Map<Integer, Double> sortMap(Map<Integer, Double> map) {

        //利用Map的entrySet方法，转化为list进行排序
        List<Map.Entry<Integer, Double>> entryList = new ArrayList<>(map.entrySet());

        //利用Collections的sort方法对list排序
        Collections.sort(entryList, new Comparator<Map.Entry<Integer, Double>>() {
            @Override
            public int compare(Map.Entry<Integer, Double> o1, Map.Entry<Integer, Double> o2) {
                //正序排列，倒序反过来
                return Double.compare(o1.getValue(), o2.getValue());
            }
        });

        //遍历排序好的list，一定要放进LinkedHashMap，因为只有LinkedHashMap是根据插入顺序进行存储
        LinkedHashMap<Integer, Double> linkedHashMap = new LinkedHashMap<>();
        for (Map.Entry<Integer, Double> e : entryList
        ) {
            linkedHashMap.put(e.getKey(), e.getValue());
        }

        return linkedHashMap;
    }

    /**
     * 判断该工人是否还能接受，并将该行元素全设为最大
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
     * 计算此任务已经分配给多少工人了
     *
     * @param taskIndex
     * @return int
     */
    public int countTaskIndex(int taskIndex) {
        int count = 0;

        //计算任务分配map
        for (Map.Entry<Integer, List<Integer>> entry : assignMap.entrySet()) {
            if (entry.getValue().contains(taskIndex)) {
                count++;
            }
        }
        return count;
    }

    public int countTaskIndexInTemp(int taskIndex) {
        int count = 0;

        //计算任务分配map副本
        for (Map.Entry<Integer, List<Integer>> entry : assignMapTemp.entrySet()) {
            if (entry.getValue().contains(taskIndex)) {
                count++;
            }
        }
        return count;
    }

    /**
     * 检查所有任务是否分配完成
     *
     * @return boolean
     */
    public boolean isTaskAssignFinish() {
        for (int i = 0; i < taskNum; i++) {
            if (countTaskIndex(i) < p[i]) {
                return false;
            }
        }
        //若assignMap中value长度都为q，则说明所有任务都分配完成
        int countWorkerTask = 0;
        for (Map.Entry<Integer, List<Integer>> entry : assignMap.entrySet()) {
            if (entry.getValue().size() == q) {
                countWorkerTask++;
            }
        }
        //若所有工人都分配了q个任务，则说明所有任务都分配完成，工人无法再接受任务
        if (countWorkerTask == workerNum) {
            return true;
        }

        return true;
    }

    /**
     * 打印任务分配结果
     */
    public void printAssignMap() {
        for (Map.Entry<Integer, List<Integer>> entry : assignMap.entrySet()) {
            System.out.println("工人" + entry.getKey() + "分配的任务为：" + entry.getValue());
        }
    }

    /**
     * 打印距离矩阵
     */
    public void printDistanceMatrix() {
        System.out.println("当前距离矩阵为------------------------------------");
        for (int i = 0; i < distanceMatrix.length; i++) {
            for (int j = 0; j < distanceMatrix[i].length; j++) {
                System.out.print(distanceMatrix[i][j] + " ");
            }
            System.out.println();
        }
    }

    /**
     * 计算距离，工人完成任务需移动的总距离，即总代价
     */
    public void countDistance() {
        distance = 0;
        //遍历assignMap，计算距离
        for (int i = 0; i < workerNum; i++) {
            List<Integer> taskList = assignMap.get(i);
            if (taskList.size() == 0) {
                continue;
            }
            //计算工人i到第一个任务的距离
            distance += distanceMatrixTempCal[i][taskList.get(0)];
            //计算工人i从第一个任务到最后一个任务的距离
            for (int j = 0; j < taskList.size() - 1; j++) {
                distance += taskDistanceMatrix[taskList.get(j)][taskList.get(j + 1)];
            }
            //计算工人i从最后一个任务到工人i的距离
            distance += distanceMatrixTempCal[i][taskList.get(taskList.size() - 1)];
        }
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

    public double[][] getDistanceMatrixTemp() {
        return distanceMatrixTemp;
    }

    public void setDistanceMatrixTemp(double[][] distanceMatrixTemp) {
        this.distanceMatrixTemp = distanceMatrixTemp;
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

    public Map<Integer, List<Integer>> getAssignMap() {
        return assignMap;
    }

    public void setAssignMap(Map<Integer, List<Integer>> assignMap) {
        this.assignMap = assignMap;
    }

    public Map<Integer, List<Integer>> getAssignMapTemp() {
        return assignMapTemp;
    }

    public void setAssignMapTemp(Map<Integer, List<Integer>> assignMapTemp) {
        this.assignMapTemp = assignMapTemp;
    }

    public double getINF() {
        return INF;
    }

    public List<Integer> getWorkerList() {
        return workerList;
    }

    public void setWorkerList(List<Integer> workerList) {
        this.workerList = workerList;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double[][] getDistanceMatrixTempCal() {
        return distanceMatrixTempCal;
    }

    public void setDistanceMatrixTempCal(double[][] distanceMatrixTempCal) {
        this.distanceMatrixTempCal = distanceMatrixTempCal;
    }
}


