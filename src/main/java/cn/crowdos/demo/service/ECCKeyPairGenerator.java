package cn.crowdos.demo.service;

import org.springframework.stereotype.Service;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

@Service
public class ECCKeyPairGenerator {

    public KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        // 使用EC算法生成密钥对
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC");
        return keyPairGenerator.generateKeyPair();
    }
}
