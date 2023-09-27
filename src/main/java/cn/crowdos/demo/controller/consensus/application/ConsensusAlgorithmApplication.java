package cn.crowdos.demo.controller.consensus.application;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ConsensusAlgorithmApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConsensusAlgorithmApplication.class, args);

        // 模拟两个节点进行投票共识
        Node node1 = new Node("Node 1");
        Node node2 = new Node("Node 2");

        // 节点1投票
        node1.vote(true);

        // 节点2投票
        node2.vote(false);

        // 计算共识结果
        boolean consensusResult = node1.calculateConsensus() && node2.calculateConsensus();

        System.out.println("共识结果：" + consensusResult);
    }
}

class Node {
    private String name;
    private boolean vote;

    public Node(String name) {
        this.name = name;
    }

    public void vote(boolean decision) {
        this.vote = decision;
        System.out.println(name + "投票：" + decision);
    }

    public boolean calculateConsensus() {
        // 在这里可以添加共识算法的逻辑
        return vote;
    }
}
