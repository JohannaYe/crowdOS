package cn.crowdos.demo.controller.security.encryption;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.util.Base64;

public class AESEncryption {
    public static void main(String[] args) throws Exception {
        String plaintext = "Hello, AES!";

        // 生成AES密钥
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128); // 使用128位密钥
        SecretKey secretKey = keyGen.generateKey();

        // 创建AES加密器
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        // 加密数据
        byte[] encryptedBytes = cipher.doFinal(plaintext.getBytes());

        // 将加密结果转为Base64编码字符串
        String encryptedText = Base64.getEncoder().encodeToString(encryptedBytes);
        System.out.println("Encrypted Text: " + encryptedText);

        // 解密数据
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
        String decryptedText = new String(decryptedBytes);
        System.out.println("Decrypted Text: " + decryptedText);
    }
}
