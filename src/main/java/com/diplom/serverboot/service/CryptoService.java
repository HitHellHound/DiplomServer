package com.diplom.serverboot.service;

import org.springframework.stereotype.Component;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Component
public class CryptoService {
    private Cipher sessionCipherEncrypt = Cipher.getInstance(ASYMETRIC_ALGORITHM);
    private Cipher sessionCipherDecrypt = Cipher.getInstance(ASYMETRIC_ALGORITHM);
    private KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ASYMETRIC_ALGORITHM);
    private KeyFactory keyFactory = KeyFactory.getInstance(ASYMETRIC_ALGORITHM);
    public final static String ASYMETRIC_ALGORITHM = "RSA";

    public CryptoService() throws NoSuchPaddingException, NoSuchAlgorithmException {

    }

    public String sessionEncryption(byte[] info, Key key) {
        try {
            sessionCipherEncrypt.init(Cipher.ENCRYPT_MODE, key);
            byte[] encryptedInfo = sessionCipherEncrypt.doFinal(info);
            return Base64.getEncoder().encodeToString(encryptedInfo);
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        } catch (BadPaddingException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] sessionDecryption(String info, Key key) {
        byte[] infoByte = Base64.getDecoder().decode(info);
        try {
            sessionCipherDecrypt.init(Cipher.DECRYPT_MODE, key);
            return sessionCipherDecrypt.doFinal(infoByte);
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        } catch (BadPaddingException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    public KeyPair generateSessionKeys() {
        return keyPairGenerator.generateKeyPair();
    }

    public Key createPublicKeyFromBytes(byte[] key) {
        try {
            return keyFactory.generatePublic(new X509EncodedKeySpec(key));
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    public Key createPrivateKeyFromBytes(byte[] key) {
        try {
            return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(key));
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }
}
