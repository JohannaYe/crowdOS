package cn.crowdos.demo.service;

import cn.crowdos.kernel.Decomposer;
import cn.crowdos.kernel.constraint.Constraint;
import cn.crowdos.kernel.resource.Participant;
import cn.crowdos.kernel.resource.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.security.PublicKey;
import java.security.PrivateKey;
import javax.crypto.Cipher;
import java.util.Base64;
import java.util.List;

@Service
public class TaskSecurityService {

    @Autowired
    private ECCKeyPairGenerator eccKeyPairGenerator; // ECC密钥对生成器，用于加密和解密密钥管理

    // 加密任务数据
    public String encryptTaskData(Task taskData, PublicKey publicKey) {
        try {
            // 将任务数据对象转换为JSON格式的字符串
            String taskDataJson = convertTaskDataToJson(taskData);

            // 使用ECC公钥加密任务数据
            byte[] encryptedData = eccEncrypt(taskDataJson.getBytes(), publicKey);

            // 将加密后的数据转为Base64编码字符串
            return Base64.getEncoder().encodeToString(encryptedData);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    // 解密任务数据
    public Task decryptTaskData(String encryptedData, PrivateKey privateKey) {
        try {
            // 解码Base64编码的加密数据
            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedData);

            // 使用ECC私钥解密任务数据
            byte[] decryptedBytes = eccDecrypt(encryptedBytes, privateKey);

            // 将解密后的数据转换为任务数据对象
            String decryptedJson = new String(decryptedBytes);
            return convertJsonToTaskData(decryptedJson);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // 使用ECC公钥加密数据
    private byte[] eccEncrypt(byte[] data, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("ECIES");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(data);
    }

    // 使用ECC私钥解密数据
    private byte[] eccDecrypt(byte[] encryptedData, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("ECIES");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(encryptedData);
    }

    // 将任务数据对象转换为JSON字符串
    private String convertTaskDataToJson(Task taskData) {
        // 实现将对象转换为JSON的逻辑
        return taskData.toString();
    }

    // 将JSON字符串转换为任务数据对象
    private Task convertJsonToTaskData(String json) {
        // 实现将JSON转换为对象的逻辑,bugs
        try {
//            return objectMapper.readValue(json, Task.class);
            Task t1=new Task() {
                @Override
                public Decomposer<Task> decomposer() {
                    return null;
                }

                @Override
                public TaskDistributionType getTaskDistributionType() {
                    return null;
                }

                @Override
                public TaskStatus getTaskStatus() {
                    return null;
                }

                @Override
                public void setTaskStatus(TaskStatus taskStatus) {

                }

                @Override
                public List<Constraint> constraints() {
                    return null;
                }

                @Override
                public boolean canAssignTo(Participant participant) {
                    return false;
                }

                @Override
                public boolean assignable() {
                    return false;
                }

                @Override
                public boolean finished() {
                    return false;
                }
            };
            return t1;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
