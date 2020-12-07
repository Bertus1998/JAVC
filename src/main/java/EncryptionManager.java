import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;

public class EncryptionManager {
    public static String crypto(String password)
    {
        try {
            String key = "Bar12345Bar12345"; // 128 bit key
            Key aesKey = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");// encrypt the text
            cipher.init(Cipher.ENCRYPT_MODE, aesKey);
            byte[] encrypted = cipher.doFinal(password.getBytes());

            return new String(encrypted);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
    public static String decrypto(String encrypted)
    {
        try {
            String key = "Bar12345Bar12345"; // 128 bit key
            // Create key and cipher
            Key aesKey = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, aesKey);
            return new String(cipher.doFinal(encrypted.getBytes()));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
