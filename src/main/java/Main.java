
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class Main extends Application {
    private static Stage stage;

    public static Stage getStage() {
        return stage;
    }

    public static void setStage(Stage stage) {
        Main.stage = stage;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Main.setStage(primaryStage);
        Parent root = FXMLLoader.load(getClass().getResource("/LoginWindowController.fxml"));
        Main.getStage().setTitle("J-AVC");
        Main.getStage().initStyle(StageStyle.UNDECORATED);
        Main.getStage().setScene(new Scene(root, 600, 400));
        Main.getStage().setResizable(false);
        Main.getStage().show();
    }

    public static void main(String[] args) throws Exception {
       launch(args);
     /*  byte [] test = new byte[]{1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,
               1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,
               1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,
               1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,
               1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,
               1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,};
        byte[] encrypted =EncryptionManager.encrypt(test);
        byte[] decrypted = EncryptionManager.decrypt(encrypted);
        for(int i =0; i<test.length;i++)
        {
            System.out.println("TEST : "+test[i]);
        }
        for(int i =0; i<encrypted.length;i++)
        {
            System.out.println("ENCRYPTED : "+encrypted +" " + i);
        }
        for(int i =0; i<decrypted.length;i++)
        {
            System.out.println("DECRYPTED " +decrypted[i]);
        }
    */

    }
}
