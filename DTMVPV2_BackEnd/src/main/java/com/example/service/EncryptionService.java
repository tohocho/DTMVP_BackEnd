package com.example.service;

import org.springframework.stereotype.Service;
import java.util.Base64;

@Service
public class EncryptionService {
    
    public String encrypt(String data) {
        // Usando Base64 como ejemplo simple de "encriptación"
        return Base64.getEncoder().encodeToString(data.getBytes());
    }
    
    public String decrypt(String encryptedData) {
        // Decodificación Base64
        return new String(Base64.getDecoder().decode(encryptedData));
    }
} 