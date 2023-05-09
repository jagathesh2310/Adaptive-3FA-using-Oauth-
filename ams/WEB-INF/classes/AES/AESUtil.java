package AES;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

public class AESUtil {
    public static SecretKey setKey(final String myKey) {
        SecretKeySpec secretKey;
       byte[] key;
       MessageDigest sha = null;
       try {
           key = myKey.getBytes("UTF-8");
           sha = MessageDigest.getInstance("SHA-1");
           key = sha.digest(key);
           key = Arrays.copyOf(key, 16);
           secretKey = new SecretKeySpec(key, "AES");
           return secretKey;
       } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
           e.printStackTrace();
       }
       return null;
   }

   public static String encrypt(final String strToEncrypt, final String secret) {
       try {
           SecretKey secretKey= setKey(secret);
           Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
           cipher.init(Cipher.ENCRYPT_MODE, secretKey);
           return Base64.getUrlEncoder()
                   .encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
       } catch (Exception e) {
           System.out.println("Error while encrypting: " + e.toString());
       }
       return null;
   }
   public static String decrypt(final String strToDecrypt, final String secret) {
    try {
        SecretKey secretKey= setKey(secret);
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return new String(cipher.doFinal(Base64.getUrlDecoder()
                .decode(strToDecrypt)));
    } catch (Exception e) {
        System.out.println("Error while decrypting: " + e.toString());
    }
    return null;
    }
    public static String key_generator(){
        String client_id;
        SecureRandom random1 = new SecureRandom();
        byte[] bytes1 = new byte[16];
        random1.nextBytes(bytes1);
        client_id=Base64.getUrlEncoder().encodeToString(bytes1);
        return client_id;
    }
    
}
