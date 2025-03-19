package com.example.service;

import org.springframework.stereotype.Service;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import jakarta.annotation.PostConstruct;

@Service
public class EncryptionService {
    
    private SecretKey secretKey;
    private static final String ALGORITHM = "AES";
    
    @Value("${encryption.key:MySecretKey12345}")
    private String encryptionKey;
    
    @PostConstruct
    public void init() {
        // Convertir la clave String en una SecretKey de 128 bits
        byte[] keyBytes = encryptionKey.getBytes(StandardCharsets.UTF_8);
        byte[] key16Bytes = new byte[16]; // 128 bits
        System.arraycopy(keyBytes, 0, key16Bytes, 0, Math.min(keyBytes.length, 16));
        secretKey = new SecretKeySpec(key16Bytes, ALGORITHM);
    }

    public String encrypt(String data) throws Exception {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedBytes = cipher.doFinal(data.getBytes());
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new Exception("Error al encriptar los datos: " + e.getMessage());
        }
    }
    
    public String decrypt(String encryptedData) throws Exception {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
            return new String(decryptedBytes);
        } catch (Exception e) {
            throw new Exception("Error al desencriptar los datos: " + e.getMessage());
        }
    }
} 