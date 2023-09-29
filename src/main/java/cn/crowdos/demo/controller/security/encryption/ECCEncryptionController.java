package cn.crowdos.demo.controller.security.encryption;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.*;
import javax.crypto.Cipher;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@RestController
public class ECCEncryptionController {

    private KeyPair keyPair;

    public ECCEncryptionController() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        // 创建ECC密钥对
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC");
        ECGenParameterSpec ecSpec = new ECGenParameterSpec("secp256r1"); // 使用secp256r1曲线
        keyPairGenerator.initialize(ecSpec);
        this.keyPair = keyPairGenerator.generateKeyPair();
    }

    // 加密地址：http://localhost:8080/encryptECC
    @PostMapping("/encryptECC")
    public String encryptECC(@RequestBody String plaintext) throws Exception {
        // 获取公钥
        PublicKey publicKey = keyPair.getPublic();

        // 创建Cipher对象并使用公钥加密数据
        Cipher cipher = Cipher.getInstance("ECIES");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);

        byte[] encryptedBytes = cipher.doFinal(plaintext.getBytes());

        // 将加密结果转为Base64编码字符串
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    // 解密地址：http://localhost:8080/decryptECC
    @PostMapping("/decryptECC")
    public String decryptECC(@RequestBody String encryptedText) throws Exception {
        // 获取私钥
        PrivateKey privateKey = keyPair.getPrivate();

        // 创建Cipher对象并使用私钥解密数据
        Cipher cipher = Cipher.getInstance("ECIES");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);

        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedText);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

        // 将解密结果转为字符串
        return new String(decryptedBytes);
    }
}
