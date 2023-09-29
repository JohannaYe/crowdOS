package cn.crowdos.demo.controller.security.encryption;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import java.util.Base64;

@RestController
public class TripleDESEncryptionController {

    // 加密地址：http://localhost:8080/encrypt3des
    @PostMapping("/encrypt3des")
    public String encrypt3DES(@RequestBody String plaintext) {
        try {
            // 创建3DES密钥
            byte[] desKeyBytes = "YourSecretKey".getBytes();
            DESedeKeySpec desKeySpec = new DESedeKeySpec(desKeyBytes);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
            SecretKey secretKey = keyFactory.generateSecret(desKeySpec);

            // 创建3DES加密器
            Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
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

    // 解密地址：http://localhost:8080/decrypt3des
    @PostMapping("/decrypt3des")
    public String decrypt3DES(@RequestBody String encryptedText) {
        try {
            // 创建3DES密钥
            byte[] desKeyBytes = "YourSecretKey".getBytes();
            DESedeKeySpec desKeySpec = new DESedeKeySpec(desKeyBytes);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
            SecretKey secretKey = keyFactory.generateSecret(desKeySpec);

            // 创建3DES解密器
            Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);

            // 解密数据
            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedText);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

            // 将解密结果转为字符串
            return new String(decryptedBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
}
