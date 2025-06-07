package com.example.eLearningPlatform.utils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class EncryptionUtil {

    private static final String AES_ALGORITHM = "AES";
    private static final String SECRET_KEY = "MySuperSecretKey"; // Store securely!

    // Convert secret key string to SecretKey object
    private static SecretKey getSecretKey() {
        return new SecretKeySpec(SECRET_KEY.getBytes(), AES_ALGORITHM);
    }

    // Encrypt data
    public static String encrypt(String data) throws Exception {
        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey());
        byte[] encryptedBytes = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    // Decrypt data
    public static String decrypt(String encryptedData) throws Exception {
        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, getSecretKey());
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
        return new String(decryptedBytes);
    }
}
