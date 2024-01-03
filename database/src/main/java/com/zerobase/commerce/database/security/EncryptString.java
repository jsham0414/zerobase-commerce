package com.zerobase.commerce.database.security;

import org.springframework.stereotype.Component;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Component
public class EncryptString {
    // AES 암호화를 위해 사용되는 키는 16, 24, 36 Byte여야 한다.
    private final String secretKey = "12345678901234567890123456789012";

    private final Base64.Encoder encoder = Base64.getEncoder();
    private final Base64.Decoder decoder = Base64.getDecoder();

    String encryptString(String plainString) throws InvalidAlgorithmParameterException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        byte[] encryptedString = cipherPkcs5(Cipher.ENCRYPT_MODE, secretKey).doFinal(plainString.getBytes(StandardCharsets.UTF_8));

        return encoder.encodeToString(encryptedString);
    }

    String decryptString(String encrypedString) throws InvalidAlgorithmParameterException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        byte[] byteString = decoder.decode(encrypedString.getBytes(StandardCharsets.UTF_8));

        return new String(cipherPkcs5(Cipher.DECRYPT_MODE, secretKey).doFinal(byteString), StandardCharsets.UTF_8);
    }

    Cipher cipherPkcs5(Integer opMode, String secretKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException {
        Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
        var sk = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "AES");
        var iv = new IvParameterSpec(secretKey.substring(0, 16).getBytes(StandardCharsets.UTF_8));
        c.init(opMode, sk, iv);
        return c;
    }
}
