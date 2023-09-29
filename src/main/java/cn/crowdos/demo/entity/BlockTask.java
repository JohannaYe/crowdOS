package cn.crowdos.demo.entity;

public class BlockTask {
    private String taskData;
    private String proof;
    private String previousHash;
    // 其他任务相关字段和方法

    public String getTaskData() {
        return taskData;
    }

    public void setTaskData(String taskData) {
        this.taskData = taskData;
    }

    public String getProof() {
        return proof;
    }

    public void setProof(String proof) {
        this.proof = proof;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public void setPreviousHash(String previousHash) {
        this.previousHash = previousHash;
    }

    // 其他任务相关方法
}
