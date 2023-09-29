package cn.crowdos.demo.controller.consensus;
import cn.crowdos.demo.entity.BlockTask;
import net.sf.jsqlparser.statement.Block;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping("/blockchain")
public class BlockchainController {

    private String currentProof = "1"; // 当前工作量证明值
    private BlockTask currentTask; // 当前任务数据
    private String previousHash = "0"; // 上一个区块的哈希值

    // 工作量证明算法
    private String proofOfWork(String lastProof) throws NoSuchAlgorithmException {
        int proof = 0;
        while (!isValidProof(lastProof, Integer.toString(proof))) {
            proof++;
        }
        return Integer.toString(proof);
    }

    // 验证工作量证明是否有效
    private boolean isValidProof(String lastProof, String proof) throws NoSuchAlgorithmException {
        String guess = lastProof + proof;
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = md.digest(guess.getBytes());
        StringBuilder hashHex = new StringBuilder();
        for (byte hashByte : hashBytes) {
            hashHex.append(String.format("%02x", hashByte));
        }
        return hashHex.toString().startsWith("0000"); // 示例中要求哈希以 "0000" 开头
    }

    // 添加新任务到区块链
    @PostMapping("/addTask")
    public String addTaskToBlockchain(String taskData) throws NoSuchAlgorithmException {
        // 计算工作量证明
        String proof = proofOfWork(currentProof);

        // 创建新区块
        BlockTask newTask = new BlockTask();
        newTask.setTaskData(taskData);
        newTask.setProof(proof);
        newTask.setPreviousHash(previousHash);

        // 更新当前任务和工作量证明
        currentTask = newTask;
        currentProof = proof;

        // 返回新任务的数据
        return "New Task Added: " + taskData;
    }

    // 获取当前区块链中的任务
    @GetMapping("/getTasks")
    public BlockTask getCurrentTask() {
        return currentTask;
    }
}
