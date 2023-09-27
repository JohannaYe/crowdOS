package cn.crowdos.demo.controller.consensus.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class NodeApplication {

    public static void main(String[] args) {
        SpringApplication.run(NodeApplication.class, args);

        // 模拟节点1提交值
        RestTemplate restTemplate = new RestTemplate();
        String node1Value = "ConsensusValue123";
        String response1 = restTemplate.postForObject("http://localhost:8080/consensus/submitValue", node1Value, String.class);
        System.out.println("Node 1: " + response1);

        // 模拟节点2提交值
        String node2Value = "ConsensusValue123";
        String response2 = restTemplate.postForObject("http://localhost:8081/consensus/submitValue", node2Value, String.class);
        System.out.println("Node 2: " + response2);

        // 获取共识值
        String consensusValue = restTemplate.getForObject("http://localhost:8080/consensus/getConsensusValue", String.class);
        System.out.println("Current Consensus Value: " + consensusValue);
    }
}
