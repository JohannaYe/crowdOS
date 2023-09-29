package cn.crowdos.demo.service;

import cn.crowdos.demo.entity.RobotInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class InfoEncryptionService {

    private static final String ENCRYPT_URL = "http://localhost:8080/encryptAES";
    private static final String DECRYPT_URL = "http://localhost:8080/decryptAES";

    @Autowired
    private RestTemplate restTemplate;

    // 构造函数或@Autowired注入RestTemplate

    public String encryptData(RobotInfo data) {
        try {
            // 发起HTTP POST请求调用加密端点
            String encryptedData = restTemplate.postForObject(ENCRYPT_URL, data, String.class);
            return encryptedData;
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    public String decryptData(String encryptedData) {
        try {
            // 发起HTTP POST请求调用解密端点
            String decryptedData = restTemplate.postForObject(DECRYPT_URL, encryptedData, String.class);
            return decryptedData;
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
}
