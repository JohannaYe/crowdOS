package cn.crowdos.demo.controller.consensus.controller;


import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/consensus")
public class ConsensusController {

    private String consensusValue; // 共识值

    @GetMapping("/getConsensusValue")
    public String getConsensusValue() {
        return consensusValue;
    }

    @PostMapping("/submitValue")
    public String submitValue(@RequestBody String value) {
        // 模拟节点提交值的操作
        if (consensusValue == null) {
            consensusValue = value;
        } else if (consensusValue.equals(value)) {
            // 达成共识，更新共识值
            consensusValue = value;
        }
        return "Submitted value: " + value;
    }
}
