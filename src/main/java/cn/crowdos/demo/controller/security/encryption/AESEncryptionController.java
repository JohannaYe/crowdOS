package cn.crowdos.demo.controller.security.encryption;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.KeySpec;
import java.util.Base64;

@RestController
public class AESEncryptionController {

    private static final String SECRET_KEY = "YourSecretKey";
    private static final String SALT = "YourSalt";
    private static final int ITERATION_COUNT = 10000;
    private static final int KEY_LENGTH = 128;

    // 加密地址：http://localhost:8080/encryptAES
    @PostMapping("/encryptAES")
    public String encryptAES(@RequestBody String plaintext) {
        try {
            // 创建PBE密钥
            KeySpec keySpec = new PBEKeySpec(SECRET_KEY.toCharArray(), SALT.getBytes(), ITERATION_COUNT, KEY_LENGTH);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithHmacSHA256AndAES_128");
            SecretKey secretKey = keyFactory.generateSecret(keySpec);

            // 创建AES加密器
            Cipher cipher = Cipher.getInstance("AES");
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

    // 解密地址：http://localhost:8080/decryptAES
    @PostMapping("/decryptAES")
    public String decryptAES(@RequestBody String encryptedText) {
        try {
            // 创建PBE密钥
            KeySpec keySpec = new PBEKeySpec(SECRET_KEY.toCharArray(), SALT.getBytes(), ITERATION_COUNT, KEY_LENGTH);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithHmacSHA256AndAES_128");
            SecretKey secretKey = keyFactory.generateSecret(keySpec);

            // 创建AES解密器
            Cipher cipher = Cipher.getInstance("AES");
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
