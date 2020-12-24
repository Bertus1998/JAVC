import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

public class EncryptionManager {
    private final static Key  key = new SecretKeySpec("Hd0z2s!3#wGyRq15".getBytes(),"AES");

    public static Key getKey() {
        return key;
    }

    public static byte[] encrypt(byte[] Data) throws Exception {

        Cipher c = Cipher.getInstance("AES");
        c.init(Cipher.ENCRYPT_MODE, getKey());
        byte[] encVal = c.doFinal(Data);
         return encVal;
    }
    public static byte[] decrypt(byte[] encryptedData) throws Exception {

        Cipher c = Cipher.getInstance("AES");
        c.init(Cipher.DECRYPT_MODE, getKey());
        byte[] decValue = c.doFinal(encryptedData);
        return decValue;
    }
}
