import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

public class EncryptionManager {
    private static final String ALGO = "AES";
    private static final byte[] keyValue = "Ad0#2s!3oGyRq!5F".getBytes();
    public static byte[] decrypt(byte[] encryptedData) throws Exception {
        Key key = new SecretKeySpec(keyValue, ALGO);
        Cipher c = Cipher.getInstance(ALGO);
        c.init(Cipher.DECRYPT_MODE, key);

        byte[] decValue = c.doFinal(encryptedData);
        return decValue;
    }
    public static byte[] encrypt(byte[] Data) throws Exception {
        Key key = new SecretKeySpec(keyValue, ALGO);
        Cipher c = Cipher.getInstance(ALGO);
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encVal = c.doFinal(Data);
        //String encryptedValue = new BASE64Encoder().encode(encVal);
        return encVal;
    }
}
