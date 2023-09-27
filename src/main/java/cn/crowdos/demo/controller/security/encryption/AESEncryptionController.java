package cn.crowdos.demo.controller.security.encryption;


import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import java.util.Base64;

@RestController
public class AESEncryptionController {
//http://localhost:8080/encrypt3DES
    @PostMapping("/encrypt3DES")
    public String encrypt3DES(@RequestBody String plaintext) {
        try {
            // 创建3DES密钥
            byte[] desKeyBytes = "YourSecretKey".getBytes();
            DESedeKeySpec desKeySpec = new DESedeKeySpec(desKeyBytes);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
            SecretKey secretKey = keyFactory.generateSecret(desKeySpec);

            // 创建3DES加密器
            Cipher cipher = Cipher.getInstance("DESede");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            // 加密数据
            byte[] encryptedBytes = cipher.doFinal(plaintext.getBytes());

            // 将加密结果转为Base64编码字符串
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
    @GetMapping("/hello")
    public String encrypt3DES() {
        try {


            // 将加密结果转为Base64编码字符串
            return "helloworld";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
}
